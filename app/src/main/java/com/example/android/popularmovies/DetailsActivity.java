package com.example.android.popularmovies;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.DesignUtils;
import com.example.android.popularmovies.utilities.JSONUtils;
import com.example.android.popularmovies.utilities.MovieReview;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Movie>{
    private ImageView poster;
    private TextView title;
    private TextView voteAverage;
    private TextView releaseDate;
    private TextView overview;
    private int movieId;
    private Typeface courgette;//font for the movie's title
    private final static String BASIC_URL = "https://image.tmdb.org/t/p";
    private final static int DETAIL_LOADER_ID= 101;
    RecyclerView reviewsRecyclerView;
    private ArrayList<MovieReview> movieReviews;
    private MovieReviewsAdapter movieReviewsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setupWindowAnimations();
        initializations();

    }

    private void initializations() {
        courgette = Typeface.createFromAsset(getAssets(), "Courgette-Regular.ttf");
        poster = (ImageView) findViewById(R.id.details_poster);
        title = (TextView) findViewById(R.id.details_title);
        title.setTypeface(courgette);
        voteAverage = (TextView) findViewById(R.id.details_vote_average);
        releaseDate = (TextView) findViewById(R.id.details_release_date);
        overview = (TextView) findViewById(R.id.details_overview);
        reviewsRecyclerView=(RecyclerView)findViewById(R.id.recyclerView_reviews);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        reviewsRecyclerView.setLayoutManager(linearLayoutManager);
        reviewsRecyclerView.setHasFixedSize(true);
        movieReviews=new ArrayList<>();
        movieReviewsAdapter=new MovieReviewsAdapter(movieReviews);
        reviewsRecyclerView.setAdapter(movieReviewsAdapter);
        Intent intent = getIntent();
        movieId = intent.getIntExtra("clickedMovieId", 0);
        //new fetchMovieDetailsTask().execute(String.valueOf(movieId));
        getSupportLoaderManager().initLoader(DETAIL_LOADER_ID,null,this);

    }

    /**
     * set a slide-enter-transition and a fade-return-transition
     */
    private void setupWindowAnimations() {
        if (Build.VERSION.SDK_INT >= 21) {
            Fade fade = new Fade();
            fade.setDuration(1000);
            Slide slide = new Slide();
            slide.setSlideEdge(Gravity.BOTTOM);
            slide.setDuration(1000);
            getWindow().setEnterTransition(slide);
            getWindow().setReturnTransition(fade);

        }
    }

    /**
     * fetch the views with the movie's data
     */
    private void displayMovieDetails(Movie movie) {
        String posterPath = movie.getPosterPath();
        displayImageFromUrl(posterPath);
        title.setText(movie.getTitle());
        voteAverage.setText(String.valueOf(movie.getVoteAverage()));
        releaseDate.setText(movie.getReleaseDate());
        overview.setText(movie.getOverview());
        ArrayList<MovieReview> reviews=movie.getReviews();
        Log.e("0000000",""+reviews.size());
        movieReviews=reviews;
        movieReviewsAdapter.setMoviesData(reviews);

    }

    /**
     * show the movie's poster image inside the ImageView using the Picasso external-library
     */
    private void displayImageFromUrl(String posterPath) {
        String basicUrl = BASIC_URL;
        String fixedSizeForPoster;
        fixedSizeForPoster = calculatePosterSize();
        String imageUrl = basicUrl + fixedSizeForPoster + posterPath;
        Picasso.with(getApplicationContext()).load(imageUrl).into(poster);
    }

    /**
     * calculate the size of the image to download
     */
    private String calculatePosterSize() {
        float density = getResources().getDisplayMetrics().density;
        Point size = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(size);
        float width = DesignUtils.calculateScreenWidth(size, density);
        float height = DesignUtils.calculateScreenHeight(size, density);
        return DesignUtils.calculatePosterSizeForDetails(density, width, height);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                forceLoad();
            }

            @Override
            public Movie loadInBackground() {


                URL movieDetailsRequestURL = NetworkUtils.buildUrlForDetails(String.valueOf(movieId));
                URL movieReviewsUrl=NetworkUtils.buildUrlForReviews(String.valueOf(movieId));


                try {
                    String jsonMovieDetailsResponse = NetworkUtils
                            .getResponseFromHttpUrl(movieDetailsRequestURL);
                    String reviews=NetworkUtils.getResponseFromHttpUrl(movieReviewsUrl);
                    Movie movieSelected = JSONUtils
                            .getMovieDetailsFromJson(jsonMovieDetailsResponse,reviews);

                    return movieSelected;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }            }
        };
    }

    @Override
    public void onLoadFinished(Loader loader, Movie data) {
        displayMovieDetails(data);

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }


   /* private class fetchMovieDetailsTask extends AsyncTask<String, Void, Movie> {

         //request te movie's details to display

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
                        .getMovieDetailsFromJson(jsonMovieDetailsResponse);
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
*/

}
