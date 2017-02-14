package com.example.android.popularmovies.utilities;


import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
    private static final int POPULAR_MOVIES=0;
    private static final int TOP_RATED__MOVIES=1;
    private static final String POPULAR_SEARCH="popular";
    private static final String TOP_RATED_SEARCH="top_rated";
    private static final String BASE_URL="https://api.themoviedb.org/3";
    private static final String BASE_PATH="movie";
    private static final String QUERY_PARAMETER_API_KEY="api_key";
    private static final String API_KEY="your api-key";

    public URL buildUrl(int typeOfSearch){
        String searchPath;
        if(typeOfSearch==POPULAR_MOVIES){
            searchPath=POPULAR_SEARCH;
        }else {
            searchPath=TOP_RATED_SEARCH;
        }
        Uri builtUri=Uri.parse(BASE_URL).buildUpon()
                .appendPath(BASE_PATH)
                .appendPath(searchPath)
                .appendQueryParameter(QUERY_PARAMETER_API_KEY,API_KEY)
                .build();
        URL url = null;
        try {
            url=new URL(builtUri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
    return url;
}
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
