package com.example.android.popularmovies;

import android.app.ActivityOptions;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.transition.Slide;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.dataPersistence.FavouriteMoviesContract;
import com.example.android.popularmovies.utilities.DesignUtils;
import com.example.android.popularmovies.utilities.JSONUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.GridItemClickListener, LoaderManager.LoaderCallbacks<ArrayList<Movie>> {
    //Two parameters for 2 types of searches in
    private final static String POPULAR_SEARCH = "popular";
    private final static String TOP_RATED_SEARCH = "top_rated";
    //action is  used to store what kind or movies are displayed in the grid: most popular, top rated or favourites.
    private int action = 0;
    private final static int ACTION_SEARCH_POPULAR = 2;
    private final static int ACTION_SEARCH_TOP_RATED = 3;
    private final static int ACTION_SEARCH_FAVOURITES = 4;
    //Ids for Loaders
    private static final int LOADER_RATED_POPULAR = 101;
    private static final int LOADER_FAVOURITES_FROM_DB = 104;
    private static final int LOADER_ADD_MOST_POPULAR_TO_DB = 105;
    private static final int LOADER_ADD_TOP_RATED_TO_DB = 106;
    private static final int LOADER_MOST_POPULAR_FROM_DB = 107;
    private static final int LOADER_TOP_RATED_FROM_DB = 108;
    //moviesWithImage stores a list of movies that have to be saved into the SQLite db
    ArrayList<Movie> moviesWithImage;
    //Respectively RecyclerView,the adapter,the data(3 different ArrayLists),the LayoutManager
    private RecyclerView mRecyclerView;
    private MoviesAdapter moviesAdapter;
    private ArrayList<Movie> mostPopularMoviesForGridItems;
    private ArrayList<Movie> topRatedMoviesForGridItems;
    private ArrayList<Movie> favouriteMoviesForGridItems;
    private GridLayoutManager gridLayoutManager;
    //the ProgressBar that appears during loading time
    private ProgressBar progressBar;

    //loaders for retrieving favourite movies from the database
    private LoaderManager.LoaderCallbacks<ArrayList<Movie>> favouritesFromDb;
    //loaders for retrieving most popular movies from the database
    private LoaderManager.LoaderCallbacks<ArrayList<Movie>> mostPopularFromDb;
    //loaders for retrieving top rated movies from the database
    private LoaderManager.LoaderCallbacks<ArrayList<Movie>> topRatedFromDb;
    //loaders for inserting most popular movies to the database
    private LoaderManager.LoaderCallbacks<ArrayList<Uri>> addMostPopularToDb;
    //loaders for inserting top rated movies to the database
    private LoaderManager.LoaderCallbacks<ArrayList<Uri>> addTopRatedToDb;

    //display a message if there are no movies to display in the grid
    private TextView txtVwNoMoviesSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupWindowAnimations();
        checkPreviousState(savedInstanceState);
        initializations();
        initializeLoader();
        displayMovieGrid();
    }

    /**
     * It saves the action's value to display the same kind of movies when the Activity is recreated.
     * @param outState bundle containing information about the previous state
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("action", action);
    }


    /**
     * Sets a slide-exit-transition and an explode-reenter-transition
     */
    private void setupWindowAnimations() {
        if (Build.VERSION.SDK_INT >= 21) {
            Slide slide = new Slide();
            slide.setSlideEdge(Gravity.START);
            slide.setDuration(500);
            getWindow().setExitTransition(slide);
            Explode explode = new Explode();
            explode.setDuration(500);
            getWindow().setReenterTransition(explode);
        }
    }

    /**
     * checks the previous state of action's value and set action with the same value.
     * This occurs when the Activity is recreated ore opened with an intent from DetailActivity
     * @param savedInstanceState bundle containing information about the previous state of the Activity
     */
    private void checkPreviousState(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null && intent.getIntExtra("action", 0) != 0) {
            action = intent.getIntExtra("action", 0);
        }
        if (savedInstanceState != null) {
            action = savedInstanceState.getInt("action");
        }
    }

    /**
     * Initializes all the Activity needs
     */
    private void initializations() {
        mostPopularMoviesForGridItems = new ArrayList<>();
        favouriteMoviesForGridItems = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        gridLayoutManager = new GridLayoutManager(this, calculateNumberOfColumns());
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        moviesAdapter = new MoviesAdapter(mostPopularMoviesForGridItems, this, NetworkUtils.checkNetworkConnection(this));
        mRecyclerView.setAdapter(moviesAdapter);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.setVisibility(View.INVISIBLE);
        txtVwNoMoviesSaved = (TextView) findViewById(R.id.txtVw_add_favourites_message);
        //when the app starts most popular movies are displayed
        if (action == 0) {
            action = ACTION_SEARCH_POPULAR;
        }
        // Force invalidation of the menu to cause onPrepareOptionMenu to be called
        invalidateOptionsMenu();
    }

    /**
     * Initializes all the loaders
     */
    private void initializeLoader() {
        mostPopularFromDb = new LoaderManager.LoaderCallbacks<ArrayList<Movie>>() {
            @Override
            public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<ArrayList<Movie>>(getApplicationContext()) {
                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();
                        forceLoad();
                    }

                    @Override
                    public ArrayList<Movie> loadInBackground() {
                        ArrayList<Movie> mostPopularMovies = new ArrayList<>();
                        Cursor cursor = getContentResolver().query(FavouriteMoviesContract.MostPopularEntry.CONTENT_URI,
                                null,
                                null,
                                null,
                                null);
                        if (cursor.getCount() <= 0) {
                            showHint(getString(R.string.activity_main_hint_most_popular_movies));
                        } else {
                            hideHint();
                            while (cursor.moveToNext()) {
                                int movieId = cursor.getInt(cursor.getColumnIndex(FavouriteMoviesContract.MostPopularEntry.COLUMN_MOVIE_ID));
                                String title = cursor.getString(cursor.getColumnIndex(FavouriteMoviesContract.MostPopularEntry.COLUMN_MOVIE_TITLE));
                                byte[] movieThumbnail = cursor.getBlob(cursor.getColumnIndex(FavouriteMoviesContract.MostPopularEntry.COLUMN_MOVIE_THUMBNAIL));
                                Bitmap movieThumbnailBitmap = BitmapFactory.decodeByteArray(movieThumbnail, 0, movieThumbnail.length);
                                mostPopularMovies.add(new Movie(movieId, title, movieThumbnailBitmap));

                            }
                        }
                        cursor.close();
                        return mostPopularMovies;
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> movies) {
                mostPopularMoviesForGridItems=movies;
                moviesAdapter.setMoviesData(movies, NetworkUtils.checkNetworkConnection(getApplicationContext()));
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

            }
        };
        topRatedFromDb = new LoaderManager.LoaderCallbacks<ArrayList<Movie>>() {
            @Override
            public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<ArrayList<Movie>>(getApplicationContext()) {
                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();
                        forceLoad();
                    }

                    @Override
                    public ArrayList<Movie> loadInBackground() {
                        ArrayList<Movie> topRatedMovies = new ArrayList<>();
                        Cursor cursor = getContentResolver().query(FavouriteMoviesContract.TopRatedEntry.CONTENT_URI,
                                null,
                                null,
                                null,
                                null);
                        if (cursor.getCount() <= 0) {

                            showHint(getString(R.string.activity_main_hint_top_rated_movies));

                        } else {
                            hideHint();
                            while (cursor.moveToNext()) {
                                int movieId = cursor.getInt(cursor.getColumnIndex(FavouriteMoviesContract.TopRatedEntry.COLUMN_MOVIE_ID));
                                String title = cursor.getString(cursor.getColumnIndex(FavouriteMoviesContract.TopRatedEntry.COLUMN_MOVIE_TITLE));
                                byte[] movieThumbnail = cursor.getBlob(cursor.getColumnIndex(FavouriteMoviesContract.TopRatedEntry.COLUMN_MOVIE_THUMBNAIL));
                                Bitmap movieThumbnailBitmap = BitmapFactory.decodeByteArray(movieThumbnail, 0, movieThumbnail.length);
                                topRatedMovies.add(new Movie(movieId, title, movieThumbnailBitmap));

                            }
                            cursor.close();
                        }
                        return topRatedMovies;
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> movies) {
                topRatedMoviesForGridItems=movies;
                moviesAdapter.setMoviesData(movies, NetworkUtils.checkNetworkConnection(getApplicationContext()));
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

            }
        };
        addTopRatedToDb = new LoaderManager.LoaderCallbacks<ArrayList<Uri>>() {
            @Override
            public Loader<ArrayList<Uri>> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<ArrayList<Uri>>(getApplicationContext()) {
                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();
                        forceLoad();
                    }

                    @Override
                    public ArrayList<Uri> loadInBackground() {
                        getContentResolver().delete(FavouriteMoviesContract.TopRatedEntry.CONTENT_URI, null, null);
                        ArrayList<Uri> insertedMovies = new ArrayList<>();
                        for (int i = 0; i < topRatedMoviesForGridItems.size(); i++) {
                            Movie movie = topRatedMoviesForGridItems.get(i);
                            int movieId = movie.getMovieId();
                            String title = movie.getTitle();
                            String posterPath = movie.getPosterPath();
                            String basicUrl = "https://image.tmdb.org/t/p";
                            String imageUrl = basicUrl + calculatePosterSize() + posterPath;
                            try {
                                URL url = new URL(imageUrl);
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                connection.setDoInput(true);
                                connection.connect();
                                InputStream input = connection.getInputStream();
                                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                                moviesWithImage.add(new Movie(movieId, title, myBitmap));
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                myBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] byteArray = stream.toByteArray();
                                ContentValues cv = new ContentValues();
                                cv.put(FavouriteMoviesContract.TopRatedEntry.COLUMN_MOVIE_ID, movieId);
                                cv.put(FavouriteMoviesContract.TopRatedEntry.COLUMN_MOVIE_TITLE, title);
                                cv.put(FavouriteMoviesContract.TopRatedEntry.COLUMN_MOVIE_THUMBNAIL, byteArray);
                                insertedMovies.add(getContentResolver().insert(FavouriteMoviesContract.TopRatedEntry.CONTENT_URI, cv));

                            } catch (IOException e) {
                                // Log exception
                                return null;
                            }

                        }
                        return insertedMovies;
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<ArrayList<Uri>> loader, ArrayList<Uri> data) {

            }

            @Override
            public void onLoaderReset(Loader<ArrayList<Uri>> loader) {

            }
        };
        addMostPopularToDb = new LoaderManager.LoaderCallbacks<ArrayList<Uri>>() {
            @Override
            public Loader<ArrayList<Uri>> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<ArrayList<Uri>>(getApplicationContext()) {
                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();
                        forceLoad();
                    }

                    @Override
                    public ArrayList<Uri> loadInBackground() {
                        getContentResolver().delete(FavouriteMoviesContract.MostPopularEntry.CONTENT_URI, null, null);
                        ArrayList<Uri> insertedMovies = new ArrayList<>();
                        for (int i = 0; i < mostPopularMoviesForGridItems.size(); i++) {
                            Movie movie = mostPopularMoviesForGridItems.get(i);
                            int movieId = movie.getMovieId();
                            String title = movie.getTitle();
                            String posterPath = movie.getPosterPath();
                            String basicUrl = "https://image.tmdb.org/t/p";
                            String imageUrl = basicUrl + calculatePosterSize() + posterPath;
                            try {
                                URL url = new URL(imageUrl);
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                connection.setDoInput(true);
                                connection.connect();
                                InputStream input = connection.getInputStream();
                                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                                moviesWithImage.add(new Movie(movieId, title, myBitmap));
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                myBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] byteArray = stream.toByteArray();
                                ContentValues cv = new ContentValues();
                                cv.put(FavouriteMoviesContract.TopRatedEntry.COLUMN_MOVIE_ID, movieId);
                                cv.put(FavouriteMoviesContract.TopRatedEntry.COLUMN_MOVIE_TITLE, title);
                                cv.put(FavouriteMoviesContract.TopRatedEntry.COLUMN_MOVIE_THUMBNAIL, byteArray);
                                insertedMovies.add(getContentResolver().insert(FavouriteMoviesContract.MostPopularEntry.CONTENT_URI, cv));

                            } catch (IOException e) {
                                // Log exception
                                return null;
                            }
                        }
                        return insertedMovies;
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<ArrayList<Uri>> loader, ArrayList<Uri> data) {

            }

            @Override
            public void onLoaderReset(Loader<ArrayList<Uri>> loader) {

            }
        };
        favouritesFromDb = new LoaderManager.LoaderCallbacks<ArrayList<Movie>>() {
            @Override
            public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {

                return new AsyncTaskLoader<ArrayList<Movie>>(getApplicationContext()) {
                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();
                        forceLoad();
                    }

                    @Override
                    public ArrayList<Movie> loadInBackground() {
                        Cursor cursor = getContentResolver().query(FavouriteMoviesContract.FavouritesEntry.CONTENT_URI, null, null,
                                null, null);
                        if (cursor.getCount() > 0) {
                            hideHint();
                            ArrayList<Movie> favouriteMovies = new ArrayList<>();
                            while (cursor.moveToNext()) {
                                int movieId = cursor.getInt(cursor.getColumnIndex(FavouriteMoviesContract.FavouritesEntry.COLUMN_MOVIE_ID));
                                String title = cursor.getString(cursor.getColumnIndex(FavouriteMoviesContract.FavouritesEntry.COLUMN_MOVIE_TITLE));
                                byte[] movieThumbnail = cursor.getBlob(cursor.getColumnIndex(FavouriteMoviesContract.FavouritesEntry.COLUMN_MOVIE_THUMBNAIL));
                                Bitmap movieThumbnailBitmap = BitmapFactory.decodeByteArray(movieThumbnail, 0, movieThumbnail.length);
                                favouriteMovies.add(new Movie(movieId, title, movieThumbnailBitmap));


                            }
                            cursor.close();
                            return favouriteMovies;
                        } else {
                            showHint(getString(R.string.activity_main_hint_favourite_movies));
                            return null;
                        }


                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> movies) {
                progressBar.setVisibility(View.INVISIBLE);
                favouriteMoviesForGridItems = movies;
                moviesAdapter.setMoviesData(movies, false);

            }

            @Override
            public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

            }
        };
    }

    /**
     * A TextView is shown instead of the RecyclerView if there are no movies to display
     * @param message the message that is shown
     */
    private void showHint(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setVisibility(View.INVISIBLE);
                txtVwNoMoviesSaved.setVisibility(View.VISIBLE);
                txtVwNoMoviesSaved.setText(message);
            }
        });

    }
    /**
     * The RecyclerView is shown and the hint-TextView is hided because there are movies to display
     */
    private void hideHint() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setVisibility(View.VISIBLE);
                txtVwNoMoviesSaved.setVisibility(View.INVISIBLE);
            }
        });

    }

    /**
     * Displays the movies inside the grid
     */
    private void displayMovieGrid() {
        if (NetworkUtils.checkNetworkConnection(this)) {
            switch (action) {
                case ACTION_SEARCH_POPULAR:
                    loadMovieData(POPULAR_SEARCH);
                    break;
                case ACTION_SEARCH_TOP_RATED:
                    loadMovieData(TOP_RATED_SEARCH);
                    break;
                case ACTION_SEARCH_FAVOURITES:
                    getFavouritesFromDatabase();
                    break;
            }
        } else {
            switch (action) {
                case ACTION_SEARCH_POPULAR:
                    getMostPopularMoviesFromDatabase();
                    break;
                case ACTION_SEARCH_TOP_RATED:
                    getTopRatedMoviesFromDatabase();
                    break;
                case ACTION_SEARCH_FAVOURITES:
                    getFavouritesFromDatabase();
                    break;
            }
        }
    }

    /**
     * Calculate the number of columns for the GridLayoutManager
     * @return the number of columns
     */
    private int calculateNumberOfColumns() {
        float density = this.getResources().getDisplayMetrics().density;
        Point size = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(size);
        float width = DesignUtils.calculateScreenWidth(size, density);
        float height = DesignUtils.calculateScreenHeight(size, density);
        int orientation = DesignUtils.getScreenOrientation(width, height);
        return DesignUtils.calculateNumberOfColumns(orientation, width);

    }

    /**
     * Show an error message if the user try to click on a mocie when there is no connection.
     * This instead of going to DetailsActivity
     */
    private void showErrorMessage() {
        Toast.makeText(this, R.string.activity_main_no_connection, Toast.LENGTH_SHORT).show();
    }

    /**
     * starts the loader to download movies from the internet.
     * @param typeOfSearch kind of search, most popular or top rated movies
     */
    private void loadMovieData(String typeOfSearch) {
        mRecyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        Bundle bundle = new Bundle();
        bundle.putString("search", typeOfSearch);
        getSupportLoaderManager().restartLoader(LOADER_RATED_POPULAR, bundle, this);
    }

    /**
     * Send the clicked Movie's id to DetailsActivity
     *
     * @param position position of the adapter
     */
    @Override
    public void goToMovieDetails(int position) {
        if (NetworkUtils.checkNetworkConnection(this)) {
            Movie clickedMovie = null;
            if (action == ACTION_SEARCH_FAVOURITES) {
                clickedMovie = favouriteMoviesForGridItems.get(position);
            } else if (action == ACTION_SEARCH_POPULAR) {
                clickedMovie = mostPopularMoviesForGridItems.get(position);
            } else if (action == ACTION_SEARCH_TOP_RATED) {
                clickedMovie = topRatedMoviesForGridItems.get(position);
            }
            int clickedMovieId = clickedMovie.getMovieId();
            startDetailsActivity(clickedMovieId);
            finish();
        } else {
            showErrorMessage();
        }
    }

    /**
     * Create the intent to open DetailsActivity and if SDK>21
     * transitions are used .
     */
    private void startDetailsActivity(int clickedMovieId) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra("clickedMovieId", clickedMovieId);
        intent.putExtra("action", action);

            if (Build.VERSION.SDK_INT >= 21) {
                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this)
                        .toBundle();
                startActivity(intent, bundle);
            } else {
                startActivity(intent);
            }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    /**
     * Starts a search for the most-popular, top-rated or favourites movies,
     * from the internet if there is connection otherwise from the database.
     * depending on the clicked option.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.popular_search:
                invalidateOptionsMenu();
                action = ACTION_SEARCH_POPULAR;
                if (NetworkUtils.checkNetworkConnection(this)) {
                    loadMovieData(POPULAR_SEARCH);
                    txtVwNoMoviesSaved.setVisibility(View.GONE);
                } else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    getMostPopularMoviesFromDatabase();
                }
                return true;
            case R.id.top_rated_search:
                invalidateOptionsMenu();
                action = ACTION_SEARCH_TOP_RATED;
                if (NetworkUtils.checkNetworkConnection(this)) {
                    loadMovieData(TOP_RATED_SEARCH);
                    txtVwNoMoviesSaved.setVisibility(View.GONE);
                } else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    getTopRatedMoviesFromDatabase();
                }
                return true;
            case R.id.favourites_search:
                invalidateOptionsMenu();
                action = ACTION_SEARCH_FAVOURITES;
                mRecyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                getFavouritesFromDatabase();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * Start the loader to retrieve top-rated movies from the database
     */
    private void getTopRatedMoviesFromDatabase() {
        getSupportLoaderManager().restartLoader(LOADER_TOP_RATED_FROM_DB, null, topRatedFromDb);
    }
    /**
     * Start the loader to retrieve most popular movies from the detabase
     */
    private void getMostPopularMoviesFromDatabase() {
        getSupportLoaderManager().restartLoader(LOADER_MOST_POPULAR_FROM_DB, null, mostPopularFromDb);
    }
    /**
     * Start the loader to retrieve favourite movies from the database
     */
    private void getFavouritesFromDatabase() {
        getSupportLoaderManager().restartLoader(LOADER_FAVOURITES_FROM_DB, null, favouritesFromDb);
    }


    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, final Bundle args) {
        if (id == LOADER_RATED_POPULAR) {
            return new AsyncTaskLoader<ArrayList<Movie>>(this) {
                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    forceLoad();
                }

                @Override
                public ArrayList<Movie> loadInBackground() {
                    if (args.size() == 0) {
                        return null;
                    }

                    String typeOfSearch = args.getString("search");
                    URL movieRequestURL = NetworkUtils.buildUrl(typeOfSearch);
                    moviesWithImage = new ArrayList<>();

                    try {
                        String jsonMovieResponse = NetworkUtils
                                .getResponseFromHttpUrl(movieRequestURL);
                       return JSONUtils
                                .getBasicMoviesDataFromJson(jsonMovieResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> movies) {
        progressBar.setVisibility(View.INVISIBLE);
        moviesAdapter.setMoviesData(movies, NetworkUtils.checkNetworkConnection(this));
        if (action == ACTION_SEARCH_POPULAR) {
            mostPopularMoviesForGridItems = movies;
            getSupportLoaderManager().restartLoader(LOADER_ADD_MOST_POPULAR_TO_DB, null, addMostPopularToDb);
        } else if (action == ACTION_SEARCH_TOP_RATED) {
            topRatedMoviesForGridItems = movies;
            getSupportLoaderManager().restartLoader(LOADER_ADD_TOP_RATED_TO_DB, null, addTopRatedToDb);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
    }

    /**
     * return the movie's poster size to download the movie's poster image
     */
    private String calculatePosterSize() {
        float density = getResources().getDisplayMetrics().density;
        return DesignUtils.calculatePosterSizeForGrid(density);
    }

    /**
     * changes color and textSize of a menuItem in the ActionBar to show which one was pressed last, so to show which kind of movies we
     * are displaying in the grid
     * @param action the kind of movies the user has searched
     */
    private void styleMenuButton(int action) {
        switch (action) {
            case ACTION_SEARCH_POPULAR:
                makeItOrange(R.id.popular_search);
                makeItWhite(R.id.top_rated_search);
                makeItWhite(R.id.favourites_search);
                break;
            case ACTION_SEARCH_TOP_RATED:
                makeItOrange(R.id.top_rated_search);
                makeItWhite(R.id.popular_search);
                makeItWhite(R.id.favourites_search);
                break;
            case ACTION_SEARCH_FAVOURITES:
                makeItOrange(R.id.favourites_search);
                makeItWhite(R.id.top_rated_search);
                makeItWhite(R.id.popular_search);
                break;
        }
    }

    /**
     * changes the textColor of a menuItem to orange and set the it's textSize to 20
     * @param menuItemId the id of the menu item clicked
     */
    private void makeItOrange(int menuItemId) {
        View view = findViewById(menuItemId);
        if (view != null && view instanceof TextView) {
            ((TextView) view).setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
            ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, 20); // Increase font size
        }
    }
    /**
     * changes the textColor of a menuItem to orange and set the it's textSize to 18
     * @param menuItemId the id of the menu item clicked
     */
    private void makeItWhite(int menuItemId) {
        View view = findViewById(menuItemId);
        if (view != null && view instanceof TextView) {
            ((TextView) view).setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16); // Increase font size
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean result = super.onPrepareOptionsMenu(menu);
        styleMenuButton(action);
        return result;
    }


}
