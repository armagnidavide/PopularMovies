package com.example.android.popularmovies.utilities;


import android.content.Context;

import com.example.android.popularmovies.MovieForGridItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONUtils {
    public final static String POSTER_PATH="poster_path";
    public final static String ID="id";
    public final static String RESULTS="results";

public static ArrayList<MovieForGridItem> getBasicMoviesDataFromJson(Context context,String jsonResponse)
throws JSONException{
    ArrayList<MovieForGridItem> movieForGridItems=new ArrayList<>();
    JSONObject jsonObject=new JSONObject(jsonResponse);
    JSONArray movies=jsonObject.getJSONArray(RESULTS);
    for(int i=0;i<movies.length();i++){
        JSONObject movie=movies.getJSONObject(i);
        String posterPath=movie.getString(POSTER_PATH);
        int id=movie.getInt(ID);
        MovieForGridItem movieForGridItem=new MovieForGridItem(id,posterPath);
        movieForGridItems.add(movieForGridItem);
    }
    return movieForGridItems;
}
}
