package com.example.android.popularmovies.utilities;


import com.example.android.popularmovies.Movie;
import com.example.android.popularmovies.MovieReview;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONUtils {
    /*
    JSON objects' names
     */
    private final static String POSTER_PATH = "poster_path";
    private final static String ID = "id";
    private final static String RESULTS = "results";
    private final static String TITLE = "original_title";
    private final static String VOTE_AVERAGE = "vote_average";
    private final static String RELEASE_DATE = "release_date";
    private final static String OVERVIEW = "overview";

    /**
     * Create an ArrayList of Movie objects from the jsonResponse and returns it
     */
    public static ArrayList<Movie> getBasicMoviesDataFromJson(String jsonResponse)
            throws JSONException {
        ArrayList<Movie> movieForGridItems = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray movies = jsonObject.getJSONArray(RESULTS);
        for (int i = 0; i < movies.length(); i++) {
            JSONObject movie = movies.getJSONObject(i);
            String posterPath = movie.getString(POSTER_PATH);
            int id = movie.getInt(ID);
            Movie movieForGridItem = new Movie(id, posterPath);
            movieForGridItems.add(movieForGridItem);
        }
        return movieForGridItems;
    }

    /**
     * Create an Movie from the jsonResponse and returns it
     */
    public static Movie getBasicMovieDataFromJson(String jsonResponse)
            throws JSONException {
        JSONObject movieDetails = new JSONObject(jsonResponse);
        String posterPath = movieDetails.getString(POSTER_PATH);
            int id = movieDetails.getInt(ID);
        return new Movie(id, posterPath);
    }

    /**
     * Create a Movie object with all the details from the jsonResponse and returns it
     */
    public static Movie getMovieDetailsFromJson(String jsonDetailsResponse, String jsonReviewsResponse, String jsonVideoResponse) throws JSONException {
        JSONObject movieDetails = new JSONObject(jsonDetailsResponse);
        String posterPath = movieDetails.getString(POSTER_PATH);
        String title = movieDetails.getString(TITLE);
        double vote_average = ((Number) movieDetails.get(VOTE_AVERAGE)).doubleValue();
        String releaseDate = movieDetails.getString(RELEASE_DATE);
        String overview = movieDetails.getString(OVERVIEW);
        ArrayList<MovieReview> reviews = getMovieReviewsFromJson(jsonReviewsResponse);
        ArrayList<String> videos = getMovieVideosFromJson(jsonVideoResponse);
        return new Movie(posterPath, title, vote_average, releaseDate, overview, reviews, videos);
    }

    /**
     * Returns an ArrayList of Strings with the movie's reviews
     */
    public static ArrayList<MovieReview> getMovieReviewsFromJson(String jsonResponse) throws JSONException {
        JSONObject movieReviews = new JSONObject(jsonResponse);
        JSONArray reviewsJson = movieReviews.getJSONArray(RESULTS);
        ArrayList<MovieReview> reviews = new ArrayList<>();
        for (int i = 0; i < reviewsJson.length(); i++) {
            JSONObject singleReview = reviewsJson.getJSONObject(i);
            reviews.add(new MovieReview(singleReview.getString("author").trim(), singleReview.getString("content").trim()));
        }
        return reviews;
    }
    /**
     * Returns an ArrayList of Strings with the movie's videos' keys.
     */
    public static ArrayList<String> getMovieVideosFromJson(String jsonResponse) throws JSONException {
        JSONObject movieVideos = new JSONObject(jsonResponse);
        JSONArray videosJson = movieVideos.getJSONArray(RESULTS);
        ArrayList<String> videos = new ArrayList<>();
        for (int i = 0; i < videosJson.length(); i++) {
            JSONObject singleVideo = videosJson.getJSONObject(i);
            videos.add(singleVideo.getString("key"));
        }
        return videos;
    }
}
