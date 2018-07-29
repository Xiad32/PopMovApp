package com.example.popmovapp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class FavMoviesDBUtils {

    private SQLiteDatabase db;
    private static Context mContext;

    public FavMoviesDBUtils(/*SQLiteDatabase database*/ Context context){
        //db = database;
        mContext = context;
    }


    public boolean isThisMovieAFav(MovieEntry movie){
        //COMPLETED: replace with CP code:
        String movieID = String.valueOf(movie.getID());
        Cursor cursor =  mContext.getContentResolver().query(
                FavMoviesCPContract.CONTENT_URI,
                null,
                FavMoviesCPContract.COLUMNS[FavMoviesCPContract.COLUMN_INDEX_ID]
                + " = ?",
                new String[] {movieID},
                null
        );
        boolean fav = (cursor.getCount() == 1 );
        cursor.close();
        return (fav);

    }

    public boolean addThisToFavDB(MovieEntry newFavMovie){

        ContentValues cv = new ContentValues();
        cv.put(FavMoviesContract.FavlistEntry.COLUMN_ID, newFavMovie.getID());
        cv.put(FavMoviesContract.FavlistEntry.COLUMN_POSTER_KEY, newFavMovie.getPosterURL());
        cv.put(FavMoviesContract.FavlistEntry.COLUMN_RATINGS_KEY, newFavMovie.getRatings());
        cv.put(FavMoviesContract.FavlistEntry.COLUMN_TITLE_KEY, newFavMovie.getTitle());
        cv.put(FavMoviesContract.FavlistEntry.COLUMN_RELEASE_KEY, newFavMovie.getRelease());

        //COMPLETED:: replace this with code for content providers:
        Uri uri = mContext.getContentResolver().insert(
                FavMoviesCPContract.CONTENT_URI,
                cv);
//        db.beginTransaction();
//        db.insert(FavMoviesContract.FavlistEntry.TABLE_NAME, null, cv);
//        db.setTransactionSuccessful();
//        db.endTransaction();

        return uri != null ;

    }

    public boolean removeThisFromFavDB(MovieEntry movieToRemove){
        String movieID = String.valueOf(movieToRemove.getID());

        //COMPLETED: replace with code to remove an entry from CP:
        int numDeleted =  mContext.getContentResolver().delete(
                FavMoviesCPContract.CONTENT_URI,
                FavMoviesCPContract.COLUMNS[FavMoviesCPContract.COLUMN_INDEX_ID] +
                " = ?",
                new String[] {movieID}
        );

        if (numDeleted == 0) {
            Log.i(TAG, "removeThisFromFavDB: " + "No items matching that ID.");
        }
        else if (numDeleted > 1){
            Log.i(TAG, "removeThisFromFavDB: "+"More than one movie match this ID!");
        }

//        while(cursor.moveToNext()){
//            int deleted = db.delete(
//                    FavMoviesContract.FavlistEntry.TABLE_NAME,
//                    FavMoviesContract.FavlistEntry.COLUMN_ID+" = ?",
//                    new String[] {cursor.getString(
//                            cursor.getColumnIndex(FavMoviesContract.FavlistEntry.COLUMN_ID))}
//            );
//            Log.i(TAG, "removeThisFromFavDB: "+"deleted"+String.valueOf(deleted)+
//                  " entries with ID: "+ String.valueOf(movieID));
//        }

//        cursor.close();
        return numDeleted == 1;

    }

    public ArrayList<MovieEntry> getAllFavMovies(Context context){
        // replace with a content provider resolver for all:
        ArrayList<MovieEntry> allFavs = new ArrayList<MovieEntry>();
        /*Cursor cursor =  db.query(
                FavMoviesContract.FavlistEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );*/



        Cursor cursor = context.getContentResolver().query(
                FavMoviesCPContract.CONTENT_URI,
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
