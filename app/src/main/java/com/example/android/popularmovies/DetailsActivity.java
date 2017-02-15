package com.example.android.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.JSONUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class DetailsActivity extends AppCompatActivity {
    ImageView poster;
    TextView title;
    TextView voteAverage;
    TextView releaseDate;
    TextView overview;
    int movieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        poster=(ImageView)findViewById(R.id.details_poster);
        title=(TextView)findViewById(R.id.details_title);
        voteAverage=(TextView)findViewById(R.id.details_vote_average);
        releaseDate=(TextView)findViewById(R.id.details_release_date);
        overview=(TextView)findViewById(R.id.details_overview);
        Intent intent=getIntent();
        movieId=intent.getIntExtra("clickedMovieId",0);
        new fetchMovieDetailsTask().execute(String.valueOf(movieId));
    }

    class fetchMovieDetailsTask extends AsyncTask<String,Void,Movie>{
        @Override
        protected Movie doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String movieId = params[0];
            URL movieDetailsRequestURL = NetworkUtils.buildUrlForDetails(movieId);

            try {
                String jsonMovieDetailsResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieDetailsRequestURL);
                Movie movieSelected= JSONUtils
                        .getMovieDetailsFromJson(DetailsActivity.this, jsonMovieDetailsResponse);
                return movieSelected;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie movie) {
            displayMovieDetails(movie);
        }
    }

    private void displayMovieDetails(Movie movie) {
        String posterPath = movie.getPosterPath();
        String basicUrl = "https://image.tmdb.org/t/p";
        String fixedSizeForPoster = "/w185";
        String imageUrl = basicUrl + fixedSizeForPoster + posterPath;
        Picasso.with(getApplicationContext()).load(imageUrl).into(poster);
        title.setText(movie.getTitle());
        voteAverage.setText(String.valueOf(movie.getVoteAverage()));
        releaseDate.setText(movie.getReleaseDate());
        overview.setText(movie.getOverview());
    }
}
