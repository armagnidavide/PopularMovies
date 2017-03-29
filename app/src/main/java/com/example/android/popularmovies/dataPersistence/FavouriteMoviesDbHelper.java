package com.example.android.popularmovies.dataPersistence;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import  com.example.android.popularmovies.dataPersistence.FavouriteMoviesContract.FavouritesEntry;

public class FavouriteMoviesDbHelper extends SQLiteOpenHelper{

    // The name of the database
    private static final String DATABASE_NAME = "favouriteMovies.db";

    // If you change the database schema, you must increment the database version

    private static final int VERSION = 1;

    public FavouriteMoviesDbHelper(Context context){super(context,DATABASE_NAME,null,VERSION);}
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE "  + FavouritesEntry.TABLE_NAME + " (" +
                FavouritesEntry._ID    + " INTEGER PRIMARY KEY, " +
                FavouritesEntry.COLUMN_MOVIE_ID    + " INTEGER "+ "  ) " +
                ";";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + FavouritesEntry.TABLE_NAME);
        onCreate(db);
    }
}
