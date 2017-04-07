package com.example.android.popularmovies.dataPersistence;


import android.net.Uri;
import android.provider.BaseColumns;

public class FavouriteMoviesContract {
    public final static String CONTENT_AUTHORITY = "com.example.android.popularmovies";
    public final static Uri BASIC_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public final static String FAVOURITES_PATH = "favourites";
    public final static String MOST_POPULAR_PATH = "mostpopular";
    public final static String TOP_RATED_PATH = "toprated";

    public static final class FavouritesEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASIC_CONTENT_URI.buildUpon().appendPath(FAVOURITES_PATH).build();
        public static final String TABLE_NAME = "favourites";
        public static final String COLUMN_MOVIE_ID = "movieid";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_MOVIE_THUMBNAIL = "thumbnail";
    }

    public static final class MostPopularEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASIC_CONTENT_URI.buildUpon().appendPath(MOST_POPULAR_PATH).build();
        public static final String TABLE_NAME = "mostpopular";
        public static final String COLUMN_MOVIE_ID = "movieid";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_MOVIE_THUMBNAIL = "thumbnail";
    }

    public static final class TopRatedEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASIC_CONTENT_URI.buildUpon().appendPath(TOP_RATED_PATH).build();
        public static final String TABLE_NAME = "toprated";
        public static final String COLUMN_MOVIE_ID = "movieid";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_MOVIE_THUMBNAIL = "thumbnail";
    }

}
