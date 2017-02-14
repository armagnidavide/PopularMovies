package com.example.android.popularmovies;


import android.graphics.Bitmap;

public class MovieForGridItem {
    private int movieId;
    private Bitmap posterPath;
    public MovieForGridItem(int id,Bitmap path){
        movieId=id;
        posterPath=path;
    }

    public Bitmap getPosterPath() {
        return posterPath;
    }

    public int getMovieId() {
        return movieId;
    }
}
