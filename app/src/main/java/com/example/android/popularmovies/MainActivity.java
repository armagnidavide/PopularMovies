package com.example.android.popularmovies;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.dataPersistence.FavouriteMoviesContract;
import com.example.android.popularmovies.utilities.DesignUtils;
import com.example.android.popularmovies.utilities.JSONUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.GridItemClickListener, LoaderManager.LoaderCallbacks<ArrayList<Movie>> {
    //Two parameters for 2 types of searches
    private final static String POPULAR_SEARCH = "popular";
    private final static String TOP_RATED_SEARCH = "top_rated";
    private final static String FAVOURITES_SEARCH = "favourites";
    //Values for the retry button when there is a lack of connection,
    //in this way it knows what to do if the connection comes back.
    private final static int ACTION_START_DETAILS_ACTIVITY = 1;
    private final static int ACTION_SEARCH_POPULAR = 2;
    private final static int ACTION_SEARCH_TOP_RATED = 3;
    private final static int ACTION_SEARCH_FAVOURITES=4;
    //Respectively RecyclerView,the adapter,the data,the LayoutManager
    private RecyclerView mRecyclerView;
    private MoviesAdapter moviesAdapter;
    private ArrayList<Movie> movieForGridItems;
    private GridLayoutManager gridLayoutManager;
    private TextView errorNoConnection;
    private ProgressBar progressBar;
    private int clickedMovieId;//stores the id of the movie clicked
    private Button btnTryAgain;
    private Toast mToast;
    private int action;
    private static final int LOADER_RATED_POPULAR = 101;
    private static final int LOADER_FAVOURITES_IDS = 102;
    private static final int LOADER_FAVOURITES_THUMBNAILS = 103;

    private LoaderManager.LoaderCallbacks<Cursor> favourites_loader;
    private LoaderManager.LoaderCallbacks<ArrayList<Movie>> favourites_thumbnails_loader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupWindowAnimations();
        checkPreviousState(savedInstanceState);
        initializations();
        initializeLoader();
        setListeners();
        displayMovieGrid();
    }

    private void checkPreviousState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            action=savedInstanceState.getInt("action");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("action",action);

    }

    private void initializeLoader() {
        favourites_thumbnails_loader = new LoaderManager.LoaderCallbacks<ArrayList<Movie>>() {
            @Override
            public Loader<ArrayList<Movie>> onCreateLoader(int id, final Bundle args) {
                return new AsyncTaskLoader<ArrayList<Movie>>(getApplicationContext()) {
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
                        int[] movieIds = args.getIntArray("movieIds");
                        try {
                            ArrayList<Movie> favouriteMovies = new ArrayList<>();
                            for (int i = 0; i < movieIds.length; i++) {
                                URL movieDetailsRequestURL = NetworkUtils.buildUrlForDetails(String.valueOf(movieIds[i]));
                                String jsonMovieDetailsResponse = NetworkUtils
                                        .getResponseFromHttpUrl(movieDetailsRequestURL);
                                Movie favouriteMovie = JSONUtils
                                        .getBasicMovieDataFromJson(jsonMovieDetailsResponse);
                                favouriteMovies.add(favouriteMovie);

                            }
                            return favouriteMovies;


                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> movies) {
                progressBar.setVisibility(View.INVISIBLE);
                movieForGridItems = movies;
                moviesAdapter.setMoviesData(movies);
            }

            @Override
            public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

            }
        };

        favourites_loader = new LoaderManager.LoaderCallbacks<Cursor>() {
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
                        return getContentResolver().query(FavouriteMoviesContract.FavouritesEntry.CONTENT_URI,
                                null,
                                null,
                                null,
                                null);
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                if (data.getCount() > 0) {
                    int[] movieIds = new int[data.getCount()];
                    data.moveToFirst();
                    for (int i = 0; i < data.getCount(); i++) {
                        data.moveToPosition(i);
                        movieIds[i] = data.getInt(data.getColumnIndex(FavouriteMoviesContract.FavouritesEntry.COLUMN_MOVIE_ID));
                    }
                    Bundle bundle = new Bundle();
                    bundle.putIntArray("movieIds", movieIds);
                    getSupportLoaderManager().restartLoader(LOADER_FAVOURITES_THUMBNAILS, bundle, favourites_thumbnails_loader);
                } else {
                    Toast.makeText(getApplicationContext(), "COUNT =0", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        };


    }


    /**
     * Displays a grid of movies posters if there is connection
     */
    private void displayMovieGrid() {
        if (checkNetworkConnection()) {
            switch (action){
                case ACTION_SEARCH_POPULAR:
                    loadMovieData(POPULAR_SEARCH);
                    break;
                case ACTION_SEARCH_TOP_RATED:
                    loadMovieData(TOP_RATED_SEARCH);
                    break;
                case ACTION_SEARCH_FAVOURITES:
                    getSupportLoaderManager().restartLoader(LOADER_FAVOURITES_IDS,null,favourites_loader);
                    break;
            }
        } else {
            showErrorMessage();
        }
    }

    /**
     * sets all listeners for this Activity
     */
    private void setListeners() {
        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryAgain();
            }
        });
    }

    /**
     * Initializes all the Activity needs
     */
    private void initializations() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        gridLayoutManager = new GridLayoutManager(this, calculateNumberOfColumns());
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        movieForGridItems = new ArrayList<>();
        moviesAdapter = new MoviesAdapter(movieForGridItems, this);
        mRecyclerView.setAdapter(moviesAdapter);
        errorNoConnection = (TextView) findViewById(R.id.txtVw_main_error_message);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.setVisibility(View.INVISIBLE);
        errorNoConnection.setVisibility(View.INVISIBLE);
        btnTryAgain = (Button) findViewById(R.id.btn_main_try_again);
        btnTryAgain.setVisibility(View.GONE);
        if(action!=ACTION_SEARCH_TOP_RATED&&action!=ACTION_START_DETAILS_ACTIVITY&&action!=ACTION_SEARCH_FAVOURITES){
            action=ACTION_SEARCH_POPULAR;
        }
    }

    /**
     * Sets a slide-exit-transition and an explode-reenter-transition
     */
    private void setupWindowAnimations() {
        if (Build.VERSION.SDK_INT >= 21) {
            Slide slide = new Slide();
            slide.setSlideEdge(Gravity.LEFT);
            slide.setDuration(500);
            getWindow().setExitTransition(slide);
            Explode explode = new Explode();
            explode.setDuration(500);
            getWindow().setReenterTransition(explode);
        }
    }

    /**
     * Calculate the number of columns necessary for the GridLayoutManager
     *
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
     * Show te error message and the retry button
     */
    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        errorNoConnection.setVisibility(View.VISIBLE);
        btnTryAgain.setVisibility(View.VISIBLE);
    }

    /**
     * Start a new FetchTask to download movies' data from themoviedb API
     * searches for popular movies or top-rated movies
     *
     * @param typeOfSearch type of search to do
     */
    private void loadMovieData(String typeOfSearch) {
        errorNoConnection.setVisibility(View.INVISIBLE);
        btnTryAgain.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        Bundle bundle = new Bundle();
        bundle.putString("search", typeOfSearch);
        getSupportLoaderManager().restartLoader(LOADER_RATED_POPULAR, bundle, this);
        //new FetchMovieTask().execute(typeOfSearch);
    }

    /**
     * Show clicked Movie's details inside DetailsActivity
     *
     * @param position
     */
    @Override
    public void goToMovieDetails(int position) {
        Movie clickedMovie = movieForGridItems.get(position);
        clickedMovieId = clickedMovie.getMovieId();
        startDetailsActivity();
    }

    /**
     * Create the intent to open DetailsActivity and if SDK>21
     * transitions are used .
     */
    private void startDetailsActivity() {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra("clickedMovieId", clickedMovieId);
        if (checkNetworkConnection()) {
            if (Build.VERSION.SDK_INT >= 21) {
                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this)
                        .toBundle();
                startActivity(intent, bundle);
            } else {
                startActivity(intent);
            }
        } else {
            action = ACTION_START_DETAILS_ACTIVITY;
            showErrorMessage();
        }
    }

    /**
     * Call one of the possibles methods in function of the action value
     * or show a toast with an error message
     */
    private void tryAgain() {
        if (checkNetworkConnection()) {
            if (action == ACTION_START_DETAILS_ACTIVITY) {
                startDetailsActivity();
            } else if (action == ACTION_SEARCH_POPULAR) {
                loadMovieData(POPULAR_SEARCH);
            }  else if (action == ACTION_SEARCH_FAVOURITES) {
                getSupportLoaderManager().restartLoader(LOADER_FAVOURITES_IDS,null,favourites_loader);
            }else {
                loadMovieData(TOP_RATED_SEARCH);
            }
        } else {
            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(getApplicationContext(), "Still no connection,check your network state and try again", Toast.LENGTH_SHORT);
            mToast.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    /**
     * Start a search for the most-popular or the top-rated movies
     * depending on the clicked option.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.popular_search:
                if (checkNetworkConnection()) {
                    loadMovieData(POPULAR_SEARCH);
                    action = ACTION_SEARCH_POPULAR;
                } else {
                    action = ACTION_SEARCH_POPULAR;
                    showErrorMessage();
                }
                return true;
            case R.id.top_rated_search:
                if (checkNetworkConnection()) {
                    loadMovieData(TOP_RATED_SEARCH);
                    action = ACTION_SEARCH_TOP_RATED;
                } else {
                    action = ACTION_SEARCH_TOP_RATED;
                    showErrorMessage();
                }
                return true;
            case R.id.favourites_search:
                if (checkNetworkConnection()) {
                    getSupportLoaderManager().restartLoader(LOADER_FAVOURITES_IDS, null, favourites_loader);
                    action = ACTION_SEARCH_FAVOURITES;
                } else {
                    action = ACTION_SEARCH_FAVOURITES;
                    showErrorMessage();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * Check if the device is connected to a network.
     *
     * @return
     */
    private boolean checkNetworkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
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

                    try {
                        String jsonMovieResponse = NetworkUtils
                                .getResponseFromHttpUrl(movieRequestURL);
                        ArrayList<Movie> movieForGridItems = JSONUtils
                                .getBasicMoviesDataFromJson(jsonMovieResponse);
                        return movieForGridItems;

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
        movieForGridItems = movies;
        moviesAdapter.setMoviesData(movies);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

    }


}
