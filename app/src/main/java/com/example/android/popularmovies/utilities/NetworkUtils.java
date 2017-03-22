package com.example.android.popularmovies.utilities;


import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
    /**
     * value for querying themoviedb API
     */
    private static final String POPULAR_SEARCH = "popular";
    private static final String TOP_RATED_SEARCH = "top_rated";
    private static final String BASE_URL = "https://api.themoviedb.org/3";
    private static final String BASE_YOUTUBE_URL = "https://www.youtube.com/watch";
    private static final String BASE_PATH = "movie";
    private static final String QUERY_PARAMETER_YOUTUBE_KEY = "v";
    private static final String QUERY_PARAMETER_API_KEY = "api_key";
    private static final String QUERY_PARAMETER_LANGUAGE = "language";
    private static final String ENGLISH = "en-US";
    private static final String REVIEWS_PATH = "reviews";
    private static final String VIDEOS_PATH = "videos";
    private static final String API_KEY = "your-api-key";

    /**
     * Build and returns the Url for the search in MainActivity
     */
    public static URL buildUrl(String typeOfSearch) {
        String searchPath;
        if (typeOfSearch == POPULAR_SEARCH) {
            searchPath = POPULAR_SEARCH;
        } else {
            searchPath = TOP_RATED_SEARCH;
        }
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(BASE_PATH)
                .appendPath(searchPath)
                .appendQueryParameter(QUERY_PARAMETER_API_KEY, API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Build and returns the URL for the search in DetailsActivity
     */
    public static URL buildUrlForDetails(String movieId) {

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(BASE_PATH)
                .appendPath(movieId)
                .appendQueryParameter(QUERY_PARAMETER_API_KEY, API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Build and returns the URL to requests reviews for a specific movie in DetailsActivity
     */
    public static URL buildUrlForReviews(String movieId) {

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(BASE_PATH)
                .appendPath(movieId)
                .appendPath(REVIEWS_PATH)
                .appendQueryParameter(QUERY_PARAMETER_API_KEY, API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Build and returns the URL to requests videos' ids for a specific movie in DetailsActivity
     */
    public static URL buildUrlForVideos(String movieId) {

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(BASE_PATH)
                .appendPath(movieId)
                .appendPath(VIDEOS_PATH)
                .appendQueryParameter(QUERY_PARAMETER_API_KEY, API_KEY)
                .appendQueryParameter(QUERY_PARAMETER_LANGUAGE, ENGLISH)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Build and returns the URL for a Youtube Video
     */
    public static URL buildUrlForYoutube(String videoKey) {

        Uri builtUri = Uri.parse(BASE_YOUTUBE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAMETER_YOUTUBE_KEY, videoKey)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Return the response from an URL in String format.
     */
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
