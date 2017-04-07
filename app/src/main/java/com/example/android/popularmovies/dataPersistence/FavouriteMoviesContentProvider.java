package com.example.android.popularmovies.dataPersistence;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import static com.example.android.popularmovies.dataPersistence.FavouriteMoviesContract.FavouritesEntry.TABLE_NAME;

public class FavouriteMoviesContentProvider extends ContentProvider {

    public static final int MOST_POPULAR = 98;
    public static final int TOP_RATED = 99;
    public static final int FAVOURITES = 100;
    public static final int FAVOURITES_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FavouriteMoviesContract.CONTENT_AUTHORITY, FavouriteMoviesContract.MOST_POPULAR_PATH, MOST_POPULAR);
        uriMatcher.addURI(FavouriteMoviesContract.CONTENT_AUTHORITY, FavouriteMoviesContract.TOP_RATED_PATH, TOP_RATED);
        uriMatcher.addURI(FavouriteMoviesContract.CONTENT_AUTHORITY, FavouriteMoviesContract.FAVOURITES_PATH, FAVOURITES);
        uriMatcher.addURI(FavouriteMoviesContract.CONTENT_AUTHORITY, FavouriteMoviesContract.FAVOURITES_PATH + "/#", FAVOURITES_ID);

        return uriMatcher;
    }

    private FavouriteMoviesDbHelper mFavouriteMoviesDbHelper;

    @Override
    public boolean onCreate() {

        Context context = getContext();
        mFavouriteMoviesDbHelper = new FavouriteMoviesDbHelper(context);
        return true;
    }
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mFavouriteMoviesDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case FAVOURITES:
                long id = db.insert(TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(FavouriteMoviesContract.FavouritesEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case MOST_POPULAR:
                long mostPopularId = db.insert(FavouriteMoviesContract.MostPopularEntry.TABLE_NAME, null, values);
                if ( mostPopularId > 0 ) {
                    returnUri = ContentUris.withAppendedId(FavouriteMoviesContract.MostPopularEntry.CONTENT_URI, mostPopularId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case TOP_RATED:
                long topRatedId = db.insert(FavouriteMoviesContract.TopRatedEntry.TABLE_NAME, null, values);
                if ( topRatedId > 0 ) {
                    returnUri = ContentUris.withAppendedId(FavouriteMoviesContract.TopRatedEntry.CONTENT_URI, topRatedId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mFavouriteMoviesDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        switch (match) {
            case FAVOURITES:
                retCursor =  db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FAVOURITES_ID:
                selection="movieid=?";
                String path = uri.getPath();
                String idStr = path.substring(path.lastIndexOf('/') + 1);
                selectionArgs=new String[]{idStr};
                retCursor =  db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MOST_POPULAR:
                retCursor =  db.query(FavouriteMoviesContract.MostPopularEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case TOP_RATED:
                retCursor =  db.query(FavouriteMoviesContract.TopRatedEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db =  mFavouriteMoviesDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int moviesDeleted=0;

        switch (match) {
            case FAVOURITES_ID:
                selection="movieid=?";
                String path = uri.getPath();
                String idStr = path.substring(path.lastIndexOf('/') + 1);
                selectionArgs=new String[]{idStr};
                moviesDeleted = db.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case MOST_POPULAR:
                moviesDeleted = db.delete(FavouriteMoviesContract.MostPopularEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TOP_RATED:
                moviesDeleted = db.delete(FavouriteMoviesContract.TopRatedEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (moviesDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return moviesDeleted;
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

}

