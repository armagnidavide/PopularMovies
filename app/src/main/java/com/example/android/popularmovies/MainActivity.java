package com.example.android.popularmovies;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
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

import com.example.android.popularmovies.utilities.JSONUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.GridItemClickListener {
    private final static int NUMBER_OF_COLUMNS = 4;
    private final static String POPULAR_SEARCH = "popular";
    private final static String TOP_RATED_SEARCH = "top_rated";
    RecyclerView mRecyclerView;
    MoviesAdapter moviesAdapter;
    ArrayList<Movie> movieForGridItems;
    GridLayoutManager gridLayoutManager;
    private TextView errorNoConnection;
    private ProgressBar progressBar;
    private int clickedMovieId;
    private Button btnTryAgain;
    private Toast mToast;
    private int action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupWindowAnimations();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        gridLayoutManager = new GridLayoutManager(this, NUMBER_OF_COLUMNS);
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
        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryAgain();
            }
        });
        if (checkNetworkConnection()) {
            loadMovieData(POPULAR_SEARCH);
        } else {
            showErrorMessage();
            action = 1;

        }
    }
    private void setupWindowAnimations() {
        if(Build.VERSION.SDK_INT>=21){
            Slide slide = new Slide();
            slide.setSlideEdge(Gravity.LEFT);
            slide.setDuration(500);
            getWindow().setExitTransition(slide);
            Explode explode=new Explode();
            explode.setDuration(500);
            getWindow().setReenterTransition(explode);
        }}


    public void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        errorNoConnection.setVisibility(View.VISIBLE);
        btnTryAgain.setVisibility(View.VISIBLE);
    }


    private void loadMovieData(String typeOfSearch) {
        errorNoConnection.setVisibility(View.INVISIBLE);
        btnTryAgain.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        new FetchMovieTask().execute(typeOfSearch);
    }

    @Override
    public void goToMovieDetails(int position) {
        Movie clickedMovie = movieForGridItems.get(position);
        clickedMovieId = clickedMovie.getMovieId();
        startDetailsActivity();
    }

    void startDetailsActivity(){
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra("clickedMovieId", clickedMovieId);
        if (checkNetworkConnection()) {
            if(Build.VERSION.SDK_INT>=21){
                Bundle bundle= ActivityOptions.makeSceneTransitionAnimation(this)
                        .toBundle();
                startActivity(intent,bundle);
            }else{
                startActivity(intent);}
        } else {
            action = 0;
            showErrorMessage();
        }
    }

    private void tryAgain() {
        if (checkNetworkConnection()) {
            if (action == 0) {
               startDetailsActivity();
            } else if (action == 1) {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.popular_search:
                if (checkNetworkConnection()) {
                    loadMovieData(POPULAR_SEARCH);
                } else {
                    action = 1;
                    showErrorMessage();
                }
                return true;
            case R.id.top_rated__search:
                if (checkNetworkConnection()) {
                    loadMovieData(TOP_RATED_SEARCH);
                } else {
                    action = 2;
                    showErrorMessage();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean checkNetworkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

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
                        .getBasicMoviesDataFromJson(MainActivity.this, jsonMovieResponse);
                return movieForGridItems;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            progressBar.setVisibility(View.INVISIBLE);
            movieForGridItems = movies;
            moviesAdapter.setMoviesData(movies);
        }
    }


}
