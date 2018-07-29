package com.example.popmovapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Scanner;

import static com.example.popmovapp.FavMoviesContract.FavlistEntry.TABLE_NAME;

public class FavMoviesContentProvider extends ContentProvider {

    private FavMoviesDbHelper favMoviesDbHelper;

    private static final UriMatcher mUriMatcher = buildUriMatcher();

    public static final int MOVIES = 100;


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not Used");
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = favMoviesDbHelper.getWritableDatabase();
        Uri resultUri;
        int code = mUriMatcher.match(uri);

        switch(code){
            case MOVIES:
                long id = db.insert(TABLE_NAME, null, values);
                if ( id > 0 ) {
                    resultUri = ContentUris.withAppendedId(FavMoviesCPContract.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert movie as favorite entry into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri location: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return resultUri;
    }

    @Override
    public boolean onCreate() {
        Context mContext = getContext();
        favMoviesDbHelper = new FavMoviesDbHelper(mContext);
        return true;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = favMoviesDbHelper.getWritableDatabase();

        int code = mUriMatcher.match(uri);

        int removedFromFavs;

        switch (code) {
            case MOVIES:
                removedFromFavs = db.delete(TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (removedFromFavs != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return removedFromFavs;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = favMoviesDbHelper.getReadableDatabase();

        int code = mUriMatcher.match(uri);
        Cursor resultCursor;

        switch (code) {
            case MOVIES:
                resultCursor =  db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            // Default exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        resultCursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the desired Cursor
        return resultCursor;
    }

    public static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        /*
          All paths added to the UriMatcher have a corresponding int.
          For each kind of uri you may want to access, add the corresponding match with addURI.
          The two calls below add matches for the task directory and a single item by ID.
         */
        uriMatcher.addURI(FavMoviesCPContract.CONTENT_AUTHORITY,
                FavMoviesCPContract.PATH_FAVS, MOVIES);

        return uriMatcher;
    }
}
