package com.example.android.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    MoviesAdapter moviesAdapter;
    ArrayList<MovieForGridItem> movieForGridItems;
    GridLayoutManager gridLayoutManager;
    private final static int NUMBER_OF_COLUMNS=2;
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
    }
}
