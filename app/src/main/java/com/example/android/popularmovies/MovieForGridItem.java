package com.example.android.popularmovies;


public class MovieForGridItem {
    private int movieId;
    private String posterPath;
    public MovieForGridItem(int id,String path){
        movieId=id;
        posterPath=path;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public int getMovieId() {
        return movieId;
    }
}
