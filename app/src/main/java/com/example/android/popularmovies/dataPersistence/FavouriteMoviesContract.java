package com.example.android.popularmovies.dataPersistence;


import android.net.Uri;
import android.provider.BaseColumns;

public class FavouriteMoviesContract {
    public final static String CONTENT_AUTHORITY = "com.example.android.popularmovies";
    public final static Uri BASIC_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public final static String FAVOURITES_PATH = "favourites";

    public static final class FavouritesEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASIC_CONTENT_URI.buildUpon().appendPath(FAVOURITES_PATH).build();
        public static final String TABLE_NAME = "favourites";
        public static final String COLUMN_MOVIE_ID = "movieid";
    }

    public static final class PopularMoviesEntry {
    }

    public static final class TopRatedEntry {
    }

}
