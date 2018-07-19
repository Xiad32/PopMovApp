package com.example.popmovapp;

import android.provider.BaseColumns;

public class FavMoviesContract {

    public static final class FavlistEntry implements BaseColumns {
        public static final String TABLE_NAME = "FavMovies";
        public static final String COLUMN_ID = "api_id";
        public static final String COLUMN_TITLE_KEY = "title";
        public static final String COLUMN_POSTER_KEY = "poster";
        public static final String COLUMN_RATINGS_KEY = "ratings";
        public static final String COLUMN_RELEASE_KEY = "release_date";
        public static final String COLUMN_PLOT_KEY = "plot";
    }

}