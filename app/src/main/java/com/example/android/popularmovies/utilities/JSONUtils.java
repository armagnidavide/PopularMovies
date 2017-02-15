package com.example.android.popularmovies.utilities;


import android.content.Context;

import com.example.android.popularmovies.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONUtils {
    public final static String POSTER_PATH="poster_path";
    public final static String ID="id";
    public final static String RESULTS="results";
    public final static String TITLE="original_title";
    public final static String VOTE_AVERAGE="vote_average";
    public final static String RELEASE_DATE="release_date";
    public final static String OVERVIEW="overview";

public static ArrayList<Movie> getBasicMoviesDataFromJson(Context context, String jsonResponse)
throws JSONException{
    ArrayList<Movie> movieForGridItems=new ArrayList<>();
    JSONObject jsonObject=new JSONObject(jsonResponse);
    JSONArray movies=jsonObject.getJSONArray(RESULTS);
    for(int i=0;i<movies.length();i++){
        JSONObject movie=movies.getJSONObject(i);
        String posterPath=movie.getString(POSTER_PATH);
        int id=movie.getInt(ID);
        Movie movieForGridItem=new Movie(id,posterPath);
        movieForGridItems.add(movieForGridItem);
    }
    return movieForGridItems;
}
    public static Movie getMovieDetailsFromJson(Context context,String jsonResponse)throws JSONException{
        JSONObject movieDetails=new JSONObject(jsonResponse);
        String posterPath=movieDetails.getString(POSTER_PATH);
        String title=movieDetails.getString(TITLE);
        double vote_average = ((Number)movieDetails.get(VOTE_AVERAGE)).doubleValue();
        String releaseDate=movieDetails.getString(RELEASE_DATE);
        String overview=movieDetails.getString(OVERVIEW);
        return new Movie(posterPath,title,vote_average,releaseDate,overview);
    }
}
