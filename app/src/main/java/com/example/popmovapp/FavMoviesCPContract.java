package com.example.popmovapp;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.example.popmovapp.FavMoviesContract.FavlistEntry.*;

public class FavMoviesCPContract extends FavMoviesContract
        implements BaseColumns {

    //CP definitions:
    public static final String CONTENT_AUTHORITY = "com.example.popmovapp.favorites";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_FAVS = "favorite_movies";
    public static final Uri CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVS).build();
    public static final String CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVS;
    public static final String CONTENT_MOVIE_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVS;

    // DB Definitions:
    public static final String[] COLUMNS =
            {_ID,   COLUMN_ID,          COLUMN_TITLE_KEY,
                    COLUMN_POSTER_KEY,  COLUMN_RATINGS_KEY,
                    COLUMN_RELEASE_KEY, COLUMN_PLOT_KEY    };
    public static final int COLUMN_INDEX_ID = 0;
    public static final int COLUMN_INDEX_TITLE_KEY= 1;
    public static final int COLUMN_INDEX_POSTER_KEY= 2;
    public static final int COLUMN_INDEX_RATINGS_KEY= 3;
    public static final int COLUMN_INDEX_RELEASE_KEY= 4;
    public static final int COLUMN_INDEX_PLOT_KEY = 5;

    public static final int DATABASE_VERSION = 1;

    //public helper methods:
    public static Uri buildMovieUriWithId(long id) {
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    public static Uri buildMovieUriWithAPIId(int id){
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }



}

