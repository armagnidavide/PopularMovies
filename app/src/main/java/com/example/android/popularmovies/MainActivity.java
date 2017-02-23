package com.example.android.popularmovies;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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

import com.example.android.popularmovies.utilities.DesignUtils;
import com.example.android.popularmovies.utilities.JSONUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.GridItemClickListener {
    //Two parameters for 2 types of searches
    private final static String POPULAR_SEARCH = "popular";
    private final static String TOP_RATED_SEARCH = "top_rated";
    //Values for the retry button when there is a lack of connection,
    //in this way it knows what to do if the connection comes back.
    private final static int ACTION_START_DETAILS_ACTIVITY = 0;
    private final static int ACTION_SEARCH_POPULAR = 1;
    private final static int ACTION_SEARCH_TOP_RATED = 2;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupWindowAnimations();
        initializations();
        setListeners();
        displayMovieGrid();
    }

    /**
     * Displays a grid of movies posters if there is connection
     */
    private void displayMovieGrid() {
        if (checkNetworkConnection()) {
            loadMovieData(POPULAR_SEARCH);
        } else {
            showErrorMessage();
            action = ACTION_SEARCH_POPULAR;
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
     * @param typeOfSearch type of search to do
     */
    private void loadMovieData(String typeOfSearch) {
        errorNoConnection.setVisibility(View.INVISIBLE);
        btnTryAgain.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        new FetchMovieTask().execute(typeOfSearch);
    }

    /**
     * Show clicked Movie's details inside DetailsActivity
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
            } else {
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
                } else {
                    action = ACTION_SEARCH_POPULAR;
                    showErrorMessage();
                }
                return true;
            case R.id.top_rated__search:
                if (checkNetworkConnection()) {
                    loadMovieData(TOP_RATED_SEARCH);
                } else {
                    action = ACTION_SEARCH_TOP_RATED;
                    showErrorMessage();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * Check if the device is connected to a network.
     * @return
     */
    private boolean checkNetworkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    private class FetchMovieTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        /**
         *Start an AsyncTask to download movies' data in background
         */
        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String typeOfSearch = params[0];
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

        /**
         * Hide the ProgressBar and changes the data in the adapter
         * @param movies new movies' data
         */
        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            progressBar.setVisibility(View.INVISIBLE);
            movieForGridItems = movies;
            moviesAdapter.setMoviesData(movies);
        }
    }


}
