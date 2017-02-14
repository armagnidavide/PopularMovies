package com.example.android.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.android.popularmovies.utilities.JSONUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    MoviesAdapter moviesAdapter;
    ArrayList<MovieForGridItem> movieForGridItems;
    GridLayoutManager gridLayoutManager;
    private final static int NUMBER_OF_COLUMNS=4;
    private final static String POPULAR_SEARCH="popular";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        gridLayoutManager=new GridLayoutManager(this,NUMBER_OF_COLUMNS);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        movieForGridItems=new ArrayList<>();

        moviesAdapter=new MoviesAdapter(movieForGridItems);
        mRecyclerView.setAdapter(moviesAdapter);
        loadMovieData();
    }

    private void loadMovieData() {
        new FetchMovieTask().execute(POPULAR_SEARCH);
    }

    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<MovieForGridItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<MovieForGridItem> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String typeOfSearch = params[0];
            URL movieRequestURL = NetworkUtils.buildUrl(typeOfSearch);

            try {
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestURL);
                ArrayList<MovieForGridItem> movieForGridItems = JSONUtils
                        .getBasicMoviesDataFromJson(MainActivity.this, jsonMovieResponse);
                return movieForGridItems;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<MovieForGridItem> movieForGridItems) {
                moviesAdapter.setMoviesData(movieForGridItems);
        }
    }
}
