package com.example.popmovapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavMoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favlist.db";

    private static final int DATABASE_VERSION = 1;

    public FavMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVOVIES_TABLE = "CREATE TABLE " + FavMoviesContract.FavlistEntry.TABLE_NAME + " (" +
                FavMoviesContract.FavlistEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavMoviesContract.FavlistEntry.COLUMN_POSTER_KEY + " TEXT NOT NULL, " +
                FavMoviesContract.FavlistEntry.COLUMN_RATINGS_KEY + " INTEGER NOT NULL, " +
                FavMoviesContract.FavlistEntry.COLUMN_ID + " INTEGER NOT NULL, " +
                FavMoviesContract.FavlistEntry.COLUMN_TITLE_KEY + " TEXT NOT NULL, " +
                FavMoviesContract.FavlistEntry.COLUMN_PLOT_KEY + " TEXT, " +
                FavMoviesContract.FavlistEntry.COLUMN_RELEASE_KEY + " TEXT NOT NULL" +
                "); ";

        db.execSQL(SQL_CREATE_FAVOVIES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavMoviesContract.FavlistEntry.TABLE_NAME);
        onCreate(db);
    }


}
