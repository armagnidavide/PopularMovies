package com.example.android.popularmovies.dataPersistence;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies.dataPersistence.FavouriteMoviesContract.FavouritesEntry;

public class FavouriteMoviesDbHelper extends SQLiteOpenHelper{

    // The name of the database
    private static final String DATABASE_NAME = "favouriteMovies.db";

    // If you change the database schema, you must increment the database version

    private static final int VERSION = 1;

    public FavouriteMoviesDbHelper(Context context){super(context,DATABASE_NAME,null,VERSION);}
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE_FAVOURITES = "CREATE TABLE "  + FavouritesEntry.TABLE_NAME + " (" +
                FavouritesEntry._ID    + " INTEGER PRIMARY KEY, " +
                FavouritesEntry.COLUMN_MOVIE_ID    + " INTEGER, "+
                FavouritesEntry.COLUMN_MOVIE_TITLE    + " TEXT, "+
                FavouritesEntry.COLUMN_MOVIE_THUMBNAIL    + " BLOB "+
                "  ) " +
                ";";
        final String CREATE_TABLE_MOST_POPULAR = "CREATE TABLE "  + FavouriteMoviesContract.MostPopularEntry.TABLE_NAME + " (" +
                FavouriteMoviesContract.MostPopularEntry._ID    + " INTEGER PRIMARY KEY, " +
                FavouriteMoviesContract.MostPopularEntry.COLUMN_MOVIE_ID    + " INTEGER, "+
                FavouriteMoviesContract.MostPopularEntry.COLUMN_MOVIE_TITLE    + " TEXT, "+
                FavouriteMoviesContract.MostPopularEntry.COLUMN_MOVIE_THUMBNAIL    + " BLOB "+
                "  ) " +
                ";";
        final String CREATE_TABLE_TOP_RATED = "CREATE TABLE "  + FavouriteMoviesContract.TopRatedEntry.TABLE_NAME + " (" +
                FavouriteMoviesContract.TopRatedEntry._ID    + " INTEGER PRIMARY KEY, " +
                FavouriteMoviesContract.TopRatedEntry.COLUMN_MOVIE_ID    + " INTEGER, "+
                FavouriteMoviesContract.TopRatedEntry.COLUMN_MOVIE_TITLE    + " TEXT, "+
                FavouriteMoviesContract.TopRatedEntry.COLUMN_MOVIE_THUMBNAIL    + " BLOB "+
                "  ) " +
                ";";

        db.execSQL(CREATE_TABLE_FAVOURITES);
        db.execSQL(CREATE_TABLE_MOST_POPULAR);
        db.execSQL(CREATE_TABLE_TOP_RATED);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + FavouritesEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FavouriteMoviesContract.MostPopularEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FavouriteMoviesContract.TopRatedEntry.TABLE_NAME);
        onCreate(db);
    }
}
