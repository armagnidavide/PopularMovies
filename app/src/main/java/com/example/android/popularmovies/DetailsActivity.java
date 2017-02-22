package com.example.android.popularmovies;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.DesignUtils;
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
    Typeface courgette;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setupWindowAnimations();
        courgette = Typeface.createFromAsset(getAssets(), "Courgette-Regular.ttf");
        poster = (ImageView) findViewById(R.id.details_poster);
        title = (TextView) findViewById(R.id.details_title);
        title.setTypeface(courgette);
        voteAverage = (TextView) findViewById(R.id.details_vote_average);
        releaseDate = (TextView) findViewById(R.id.details_release_date);
        overview = (TextView) findViewById(R.id.details_overview);
        Intent intent = getIntent();
        movieId = intent.getIntExtra("clickedMovieId", 0);
        new fetchMovieDetailsTask().execute(String.valueOf(movieId));
    }
    private void setupWindowAnimations() {
        if(Build.VERSION.SDK_INT>=21){
            Fade fade = new Fade();
            fade.setDuration(1000);
            Slide slide=new Slide();
            slide.setSlideEdge(Gravity.BOTTOM);
            slide.setDuration(1000);
            getWindow().setEnterTransition(slide);
            getWindow().setReturnTransition(fade);

        }
    }

    private void displayMovieDetails(Movie movie) {
        String posterPath = movie.getPosterPath();
        displayImageFromUrl(posterPath);
        title.setText(movie.getTitle());
        voteAverage.setText(String.valueOf(movie.getVoteAverage()));
        releaseDate.setText(movie.getReleaseDate());
        overview.setText(movie.getOverview());
    }

    private void displayImageFromUrl(String posterPath) {
        String basicUrl = "https://image.tmdb.org/t/p";
        String fixedSizeForPoster;
        fixedSizeForPoster = calculatePosterSize();
        String imageUrl = basicUrl + fixedSizeForPoster + posterPath;
        Picasso.with(getApplicationContext()).load(imageUrl).into(poster);
    }

    private String calculatePosterSize() {
        float density=getResources().getDisplayMetrics().density;
        Point size = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(size);
        float width = DesignUtils.calculateScreenWidth(size, density);
        float height = DesignUtils.calculateScreenHeight(size, density);
        return  DesignUtils.calculatePosterSizeForDetails(density,width,height);
    }

    class fetchMovieDetailsTask extends AsyncTask<String, Void, Movie> {
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
                Movie movieSelected = JSONUtils
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


}
