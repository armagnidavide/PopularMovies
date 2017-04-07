package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
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
import android.view.MenuItem;
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
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Movie>, MovieVideosAdapter.goToYoutubeClickListener {
    //basic url to download movie's image
    private final static String BASIC_URL = "https://image.tmdb.org/t/p";
    //4 loader Ids 1 to download movie's details, the 2nd to add this movie as a favourite,
    //the 3rd to check if a movie is a favourite one and the 4th to remove it from favourites
    private final static int DETAIL_LOADER_ID = 101;
    private final static int LOADER_ADD_FAVOURITE = 102;
    private final static int LOADER_CHECK_FAVOURITE = 103;
    private final static int LOADER_REMOVE_FROM_FAVOURITES = 104;
    //Views to display movie's details
    private ImageView poster;
    private TextView title;
    private TextView voteAverage;
    private TextView releaseDate;
    private TextView overview;

    //variables to store movie's details
    private int movieId;
    private String movieTitle;
    private byte[] movieThumbnail;
    private ArrayList<MovieReview> movieReviews;
    private ArrayList<String> videosIds;

    //font for the movie's title
    private Typeface courgette;

    //RecyclerViews and Adapters for Reviews and Trailers
    private RecyclerView reviewsRecyclerView;
    private RecyclerView videosRecyclerView;
    private MovieVideosAdapter movieVideosAdapter;
    private MovieReviewsAdapter movieReviewsAdapter;

    //Button to add or remove a movie from favourites
    private Button btnAddFavourite;

    private LoaderManager.LoaderCallbacks<Cursor> checkLoader;
    private LoaderManager.LoaderCallbacks<Uri> addLoader;
    private LoaderManager.LoaderCallbacks<String> removeLoader;

    private boolean isFavourite;
    //to store the kind of movies were displayed in the grid in MainActivity, doing so when we come back
    //the same grid of movies is presented. (most popular or top rated or favourites)
    private int action;

    /**
     * convert a bitmap into a byte array
     * @param bitmap the image that has to be converted
     * @return the bitmap converted to byte array
     */
    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setupWindowAnimations();
        getInfoFromMainActivity();
        initializeLoaders();
        initializations();
        setListeners();

    }

    /**
     * we want the same behaviour for the up button and the back button.So when they are pressed, onBackPressed() is called.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    /**
     * starts the MainActivity with an intent containing the same action's value that MainActivity was having before.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DetailsActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("action", action);
        startActivity(intent);
    }

    private void initializations() {
        courgette = Typeface.createFromAsset(getAssets(), "Courgette-Regular.ttf");
        btnAddFavourite = (Button) findViewById(R.id.btn_add_as_favourites);
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
        startDetailsLoader();
        checkIfIsFavourite();
    }

    private void getInfoFromMainActivity() {
        Intent intent = getIntent();
        movieId = intent.getIntExtra("clickedMovieId", 0);
        action = intent.getIntExtra("action", 0);
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
                    btnAddFavourite.setText(R.string.activity_details_btn_remove);
                    isFavourite = true;
                } else {
                    btnAddFavourite.setText(R.string.activity_details_btn_add);
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
                        cv.put(FavouriteMoviesContract.FavouritesEntry.COLUMN_MOVIE_TITLE, movieTitle);
                        cv.put(FavouriteMoviesContract.FavouritesEntry.COLUMN_MOVIE_THUMBNAIL, movieThumbnail);
                        return getContentResolver().insert(FavouriteMoviesContract.FavouritesEntry.CONTENT_URI, cv);
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<Uri> loader, Uri data) {
                if (data != null) {
                    Toast.makeText(getApplicationContext(), R.string.activity_details_toast_movie_added, Toast.LENGTH_LONG).show();
                    btnAddFavourite.setText(R.string.activity_details_btn_remove);
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
                    Toast.makeText(getApplicationContext(), R.string.activity_details_toast_movie_removed, Toast.LENGTH_LONG).show();
                    btnAddFavourite.setText(R.string.activity_details_btn_add);
                    isFavourite = false;
                }

            }

            @Override
            public void onLoaderReset(Loader<String> loader) {

            }
        };
    }

    /**
     * starts a loader to download movie's details
     */
    private void startDetailsLoader() {
        getSupportLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);
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

    /**
     * Shows a confirmation dialog when the user presses btnAddFavourite and the movie is already a favourite one
     */
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

    /**
     * Starts a loader to add this movie to favourites
     */
    private void addAsFavourite() {
        getSupportLoaderManager().initLoader(LOADER_ADD_FAVOURITE, null, addLoader);
    }
    /**
     * Starts a loader to acheck if this movie is a favourite one
     */
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
        movieTitle = movie.getTitle();
        title.setText(" "+movie.getTitle());
        voteAverage.setText(" "+String.valueOf(movie.getVoteAverage()));
        releaseDate.setText(" "+movie.getReleaseDate());
        overview.setText(movie.getOverview());
        movieReviews = movie.getReviews();
        videosIds = movie.getVideosIds();
        movieReviewsAdapter.setMoviesData(movieReviews);
        movieVideosAdapter.setMovieData(videosIds);
    }

    /**
     * show the movie's poster image inside the ImageView using the Picasso external-library
     * and store the image in a variable in case the user wants to add the movie to favourites in the db.
     */
    private void displayImageFromUrl(String posterPath) {
        String fixedSizeForPoster;
        fixedSizeForPoster = calculatePosterSize();
        String imageUrl = BASIC_URL + fixedSizeForPoster + posterPath;
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                movieThumbnail = getBitmapAsByteArray(bitmap);
                poster.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        poster.setTag(target);
        Picasso.with(getApplicationContext()).load(imageUrl).into(target);
    }

    /**
     * Calculates the size of the image to download
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
                    return JSONUtils.getMovieDetailsFromJson(jsonMovieDetailsResponse, reviews, videos);
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

    /**
     * When a video item is pressed the youtube site is opened to play the video
     * @param videoId the key that identify the video on youtube
     */
    @Override
    public void goToYoutube(String videoId) {
        URL url = NetworkUtils.buildUrlForYoutube(videoId);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url.toString()));
        startActivity(i);
    }
}
