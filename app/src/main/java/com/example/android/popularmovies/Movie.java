package com.example.android.popularmovies;


import com.example.android.popularmovies.utilities.MovieReview;

import java.util.ArrayList;

public class Movie {
    private int movieId;
    private String posterPath;
    private  String title;
    private  double voteAverage;
    private  String releaseDate;
    private  String overview;
    private ArrayList<MovieReview> reviews;

    public Movie(int id, String path){
        movieId=id;
        posterPath=path;
    }
    public Movie(String path,String title,double voteAverage,String releaseDate,String overview,ArrayList<MovieReview> reviews){
        posterPath=path;
        this.title = title;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.overview = overview;
        this.reviews=reviews;
    }


    public String getPosterPath() {
        return posterPath;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getTitle() {
        return title;
    }


    public double getVoteAverage() {
        return voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getOverview() {
        return overview;
    }

    public ArrayList<MovieReview> getReviews() {
        return reviews;
    }
}
