package com.example.popmovapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class FavMoviesDBUtils {

    private SQLiteDatabase db;

    public FavMoviesDBUtils(SQLiteDatabase database){
        db = database;
    }


    public boolean isThisMovieAFav(MovieEntry movie){
        String movieID = String.valueOf(movie.getID());
        Cursor cursor =  db.query(
                FavMoviesContract.FavlistEntry.TABLE_NAME,
                new String[] {FavMoviesContract.FavlistEntry.COLUMN_ID},
                FavMoviesContract.FavlistEntry.COLUMN_ID+" = ?",
                new String[] {movieID},
                null,
                null,
                null
        );
        boolean fav = (cursor.getCount() == 1 );
        cursor.close();
        return (fav);

    }

    public void addThisToFavDB(MovieEntry newFavMovie){

        ContentValues cv = new ContentValues();
        cv.put(FavMoviesContract.FavlistEntry.COLUMN_ID, newFavMovie.getID());
        cv.put(FavMoviesContract.FavlistEntry.COLUMN_POSTER_KEY, newFavMovie.getPosterURL());
        cv.put(FavMoviesContract.FavlistEntry.COLUMN_RATINGS_KEY, newFavMovie.getRatings());
        cv.put(FavMoviesContract.FavlistEntry.COLUMN_TITLE_KEY, newFavMovie.getTitle());
        cv.put(FavMoviesContract.FavlistEntry.COLUMN_RELEASE_KEY, newFavMovie.getRelease());

        db.beginTransaction();
        db.insert(FavMoviesContract.FavlistEntry.TABLE_NAME, null, cv);
        db.setTransactionSuccessful();
        db.endTransaction();

    }

    public void removeThisFromFavDB(MovieEntry movieToRemove){
        String movieID = String.valueOf(movieToRemove.getID());
        Cursor cursor =  db.query(
                FavMoviesContract.FavlistEntry.TABLE_NAME,
                new String[] {FavMoviesContract.FavlistEntry.COLUMN_ID},
                FavMoviesContract.FavlistEntry.COLUMN_ID+" = ?",
                new String[] {movieID},
                null,
                null,
                null
        );

        if (cursor.getCount() == 0) {
            Log.i(TAG, "removeThisFromFavDB: " + "No items matching that ID.");
            cursor.close();
            return;
        }
        else if (cursor.getCount() > 1){
            Log.i(TAG, "removeThisFromFavDB: "+"More than one movie match this ID");
        }

        while(cursor.moveToNext()){
            int deleted = db.delete(
                    FavMoviesContract.FavlistEntry.TABLE_NAME,
                    FavMoviesContract.FavlistEntry.COLUMN_ID+" = ?",
                    new String[] {cursor.getString(
                            cursor.getColumnIndex(FavMoviesContract.FavlistEntry.COLUMN_ID))}
            );
            Log.i(TAG, "removeThisFromFavDB: "+"deleted"+String.valueOf(deleted)+
                  " entries with ID: "+ String.valueOf(movieID));
        }

        cursor.close();

    }

    public ArrayList<MovieEntry> getAllFavMovies(){
        ArrayList<MovieEntry> allFavs = new ArrayList<MovieEntry>();
        Cursor cursor =  db.query(
                FavMoviesContract.FavlistEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        Log.i(TAG, "getAllFavMovies: "+"DB has "+String.valueOf(cursor.getCount()) );

        if (cursor.getCount() != 0){
            while(cursor.moveToNext()){
                float ratings = Float.valueOf(
                        cursor.getString(cursor.getColumnIndex(FavMoviesContract.FavlistEntry.COLUMN_RATINGS_KEY)));
                MovieEntry thisOne = new MovieEntry(
                        cursor.getInt(cursor.getColumnIndex(FavMoviesContract.FavlistEntry.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(FavMoviesContract.FavlistEntry.COLUMN_POSTER_KEY)),
                        cursor.getString(cursor.getColumnIndex(FavMoviesContract.FavlistEntry.COLUMN_TITLE_KEY)),
                        cursor.getString(cursor.getColumnIndex(FavMoviesContract.FavlistEntry.COLUMN_PLOT_KEY)),
                        ratings,
                        cursor.getString(cursor.getColumnIndex(FavMoviesContract.FavlistEntry.COLUMN_RELEASE_KEY))
                        );
                Log.i(TAG, "getAllFavMovies: "+"Found Movie in DB of Title: "+
                        String.valueOf(
                                cursor.getString(cursor.getColumnIndex(FavMoviesContract.FavlistEntry.COLUMN_TITLE_KEY))));

                allFavs.add(thisOne);
            }

        }
        cursor.close();
        return allFavs;
    }

}
