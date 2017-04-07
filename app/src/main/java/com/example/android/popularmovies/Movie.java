package com.example.android.popularmovies;


import android.graphics.Bitmap;

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
    private Bitmap movieImage;

    /**
     * This constructor is used to create an Arraylist of movies when we download them from the internet.
     * Then the adapter use the path and the Picasso Library to download the images and bind them to the views in the grid.
     *
     * @param id    movie's id
     * @param title movie's title
     * @param path  movie's imagePath
     */
    public Movie(int id, String title, String path) {
        this.movieId = id;
        this.posterPath = path;
        this.title = title;
    }

    /**
     * This constructor is used when we manage interaction with the database so we want to save movies as favourites, top rated or
     * most popular and show them when there is no connection. Doing so makes the app working offline.
     *
     * @param id        movie's id
     * @param title     movie's title
     * @param thumbnail movie's image
     */
    public Movie(int id, String title, Bitmap thumbnail) {
        this.movieId = id;
        this.title = title;
        this.movieImage = thumbnail;
    }

    /**
     * this constructor is used for DetailsActivity
     *
     * @param path        movie's imagePath
     * @param title       movie's title
     * @param voteAverage movie's voteAverage
     * @param releaseDate movie's releaseDate
     * @param overview    a short description of the movie
     * @param reviews     reviews of the movies
     * @param videosIds   ids of the youtube videos related to the movie
     */
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

    public Bitmap getMovieImage() {
        return movieImage;
    }
}
