package com.example.android.popularmovies.utilities;


import com.example.android.popularmovies.Movie;

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
     * Create a Movie object with all the details from the jsonResponse and returns it
     */
    public static Movie getMovieDetailsFromJson(String jsonDetailsResponse,String jsonReviewsResponse) throws JSONException {
        JSONObject movieDetails = new JSONObject(jsonDetailsResponse);
        String posterPath = movieDetails.getString(POSTER_PATH);
        String title = movieDetails.getString(TITLE);
        double vote_average = ((Number) movieDetails.get(VOTE_AVERAGE)).doubleValue();
        String releaseDate = movieDetails.getString(RELEASE_DATE);
        String overview = movieDetails.getString(OVERVIEW);
        ArrayList<MovieReview> reviews=getMovieReviewsFromJson(jsonReviewsResponse);
        return new Movie(posterPath, title, vote_average, releaseDate, overview,reviews);
    }
    /**
     * Returns a String array with the movie's reviews
     */
    public static ArrayList<MovieReview> getMovieReviewsFromJson(String jsonResponse) throws JSONException{
        JSONObject movieReviews=new JSONObject(jsonResponse);
        JSONArray reviewsJson= movieReviews.getJSONArray(RESULTS);
        ArrayList<MovieReview> reviews=new ArrayList<>();
        for (int i = 0; i < reviewsJson.length(); i++) {
            JSONObject singleReview=reviewsJson.getJSONObject(i);
            reviews.add(new MovieReview(singleReview.getString("author").trim(),singleReview.getString("content").trim()));
        }
       //Log.e("rrr",reviews.size()+""+reviewsJson.length()+reviews.get(0)+reviews.get(1));

        return reviews;
    }
}
