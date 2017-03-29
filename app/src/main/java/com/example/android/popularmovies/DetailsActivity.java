package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.dataPersistence.FavouriteMoviesContract;
import com.example.android.popularmovies.utilities.DesignUtils;
import com.example.android.popularmovies.utilities.JSONUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Movie>, MovieVideosAdapter.goToYoutubeClickListener {
    private ImageView poster;
    private TextView title;
    private TextView voteAverage;
    private TextView releaseDate;
    private TextView overview;
    private int movieId;
    private Typeface courgette;//font for the movie's title
    private final static String BASIC_URL = "https://image.tmdb.org/t/p";
    private final static int DETAIL_LOADER_ID = 101;
    private final static int LOADER_ADD_FAVOURITE = 102;
    private final static int LOADER_CHECK_FAVOURITE = 103;
    private final static int LOADER_REMOVE_FROM_FAVOURITES = 104;
    private RecyclerView reviewsRecyclerView;
    private ArrayList<MovieReview> movieReviews;
    private MovieReviewsAdapter movieReviewsAdapter;
    private RecyclerView videosRecyclerView;
    private ArrayList<String> videosIds;
    private MovieVideosAdapter movieVideosAdapter;
    private Button btnAddFavourite;
    private LoaderManager.LoaderCallbacks<Cursor> checkLoader;
    private LoaderManager.LoaderCallbacks<Uri> addLoader;
    private LoaderManager.LoaderCallbacks<String> removeLoader;
    private boolean isFavourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setupWindowAnimations();
        initializations();
        setListeners();

    }


    private void initializations() {
        Intent intent = getIntent();
        movieId = intent.getIntExtra("clickedMovieId", 0);
        initializeLoaders();
        btnAddFavourite = (Button) findViewById(R.id.btn_add_as_favourites);
        courgette = Typeface.createFromAsset(getAssets(), "Courgette-Regular.ttf");
        poster = (ImageView) findViewById(R.id.details_poster);
        title = (TextView) findViewById(R.id.details_title);
        title.setTypeface(courgette);
        voteAverage = (TextView) findViewById(R.id.details_vote_average);
        releaseDate = (TextView) findViewById(R.id.details_release_date);
        overview = (TextView) findViewById(R.id.details_overview);
        reviewsRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_reviews);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        reviewsRecyclerView.setLayoutManager(linearLayoutManager);
        reviewsRecyclerView.setHasFixedSize(true);
        movieReviews = new ArrayList<>();
        movieReviewsAdapter = new MovieReviewsAdapter(movieReviews);
        reviewsRecyclerView.setAdapter(movieReviewsAdapter);

        videosRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_videos);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        videosRecyclerView.setLayoutManager(linearLayoutManager2);
        videosRecyclerView.setHasFixedSize(true);
        videosIds = new ArrayList<>();
        movieVideosAdapter = new MovieVideosAdapter(videosIds, this);
        videosRecyclerView.setAdapter(movieVideosAdapter);
        //new fetchMovieDetailsTask().execute(String.valueOf(movieId));
        getSupportLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);
        checkIfIsFavourite();
    }

    private void initializeLoaders() {
        checkLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {

                return new AsyncTaskLoader<Cursor>(getApplicationContext()) {
                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();
                        forceLoad();
                    }

                    @Override
                    public Cursor loadInBackground() {
                        Uri uri = FavouriteMoviesContract.FavouritesEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(movieId)).build();
                        return getContentResolver().query(uri, null, null, null, null, null);
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                if (data != null && data.getCount() > 0) {
                    btnAddFavourite.setText("Remove from favourites");
                    isFavourite = true;
                } else {
                    isFavourite = false;
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        };
        addLoader = new LoaderManager.LoaderCallbacks<Uri>() {
            @Override
            public Loader<Uri> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<Uri>(getApplicationContext()) {
                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();
                        forceLoad();
                    }

                    @Override
                    public Uri loadInBackground() {
                        ContentValues cv = new ContentValues();
                        cv.put(FavouriteMoviesContract.FavouritesEntry.COLUMN_MOVIE_ID, movieId);
                        return getContentResolver().insert(FavouriteMoviesContract.FavouritesEntry.CONTENT_URI, cv);
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<Uri> loader, Uri data) {
                if (data != null) {
                    Toast.makeText(getApplicationContext(), "Movie added successfully", Toast.LENGTH_LONG).show();
                    btnAddFavourite.setText("Remove from favourites");
                    isFavourite = true;
                }

            }

            @Override
            public void onLoaderReset(Loader<Uri> loader) {

            }
        };
        removeLoader = new LoaderManager.LoaderCallbacks<String>() {
            @Override
            public Loader<String> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<String>(getApplicationContext()) {
                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();
                        forceLoad();
                    }

                    @Override
                    public String loadInBackground() {
                        Uri uri = FavouriteMoviesContract.FavouritesEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(movieId)).build();
                        int rowDeleted = getContentResolver().delete(uri, null, null);
                        if (rowDeleted > 0) {
                            return "deletd " + rowDeleted + " row";
                        } else {
                            return null;
                        }
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<String> loader, String data) {
                if (data != null) {
                    Toast.makeText(getApplicationContext(), "Movie removed successfully", Toast.LENGTH_LONG).show();
                    btnAddFavourite.setText("Mark as favourite");
                    isFavourite = false;
                }

            }

            @Override
            public void onLoaderReset(Loader<String> loader) {

            }
        };
    }

    private void setListeners() {
        btnAddFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFavourite) {
                    addAsFavourite();
                } else {
                    showRemoveConfirmationDialog();
                }
            }
        });
    }

    private void showRemoveConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Remove Movie")
                .setMessage("Do you want to remove from favourites?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        getSupportLoaderManager().initLoader(LOADER_REMOVE_FROM_FAVOURITES, null, removeLoader);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void addAsFavourite() {
        getSupportLoaderManager().initLoader(LOADER_ADD_FAVOURITE, null, addLoader);
    }

    private boolean checkIfIsFavourite() {
        getSupportLoaderManager().initLoader(LOADER_CHECK_FAVOURITE, null, checkLoader);
        return false;
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
        movieReviews = movie.getReviews();
        videosIds = movie.getVideosIds();
        movieReviewsAdapter.setMoviesData(movieReviews);
        movieVideosAdapter.setMovieData(videosIds);

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
    public Loader onCreateLoader(final int id, Bundle args) {
        return new AsyncTaskLoader(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                forceLoad();
            }

            @Override
            public Movie loadInBackground() {
                URL movieDetailsRequestURL = NetworkUtils.buildUrlForDetails(String.valueOf(movieId));
                URL movieReviewsUrl = NetworkUtils.buildUrlForReviews(String.valueOf(movieId));
                URL movieVideosUrl = NetworkUtils.buildUrlForVideos(String.valueOf(movieId));


                try {
                    String jsonMovieDetailsResponse = NetworkUtils
                            .getResponseFromHttpUrl(movieDetailsRequestURL);
                    String reviews = NetworkUtils.getResponseFromHttpUrl(movieReviewsUrl);
                    String videos = NetworkUtils.getResponseFromHttpUrl(movieVideosUrl);
                    Movie movieSelected = JSONUtils
                            .getMovieDetailsFromJson(jsonMovieDetailsResponse, reviews, videos);
                    return movieSelected;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            }
        };
    }

    @Override
    public void onLoadFinished(Loader loader, Movie data) {
        displayMovieDetails(data);
    }
    @Override
    public void onLoaderReset(Loader loader) {
    }
    @Override
    public void goToYoutube(String videoId) {
        URL url = NetworkUtils.buildUrlForYoutube(videoId);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url.toString()));
        startActivity(i);
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
