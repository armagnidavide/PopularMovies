package com.example.android.popularmovies;


import java.util.ArrayList;
/*
Instances of this class are used for 2 purposes :
 1) to display just the thumbnails in a grid in MainActivity--->constructor with 2 parameters
 2) to display movie's details in DetailsActivity---> constructor with 7 parameters
 */
public class Movie {
    private int movieId;
    private String posterPath;
    private String title;
    private double voteAverage;
    private String releaseDate;
    private String overview;
    private ArrayList<MovieReview> reviews;
    private ArrayList<String> videosIds;

    public Movie(int id, String path) {
        movieId = id;
        posterPath = path;
    }

    public Movie(String path, String title, double voteAverage, String releaseDate, String overview, ArrayList<MovieReview> reviews, ArrayList<String> videosIds) {
        posterPath = path;
        this.title = title;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.overview = overview;
        this.reviews = reviews;
        this.videosIds = videosIds;
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

    public ArrayList<String> getVideosIds() {
        return videosIds;
    }
}
