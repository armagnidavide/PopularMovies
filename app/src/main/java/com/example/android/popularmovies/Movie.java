package com.example.android.popularmovies;


public class Movie {
    private int movieId;
    private String posterPath;
    private  String title;
    private  double voteAverage;
    private  String releaseDate;
    private  String overview;

    public Movie(int id, String path){
        movieId=id;
        posterPath=path;
    }
    public Movie(String path,String title,double voteAverage,String releaseDate,String overview){
        posterPath=path;
        this.title = title;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.overview = overview;
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

    public void setTitle(String title) {
        this.title = title;
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
}
