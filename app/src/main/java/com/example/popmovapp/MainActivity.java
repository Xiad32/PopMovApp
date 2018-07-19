package com.example.popmovapp;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.FaceDetector;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
    implements MovieListAdapter.MovieClickListener
{
    private static final String MOVIE_LIST_IN_BUNDLE = "movie_list";
    private ArrayList<MovieEntry> mData;
    public static String BUNDLE_STRING_KEY = "MovieToDisplay";
    private static String TAG = "MainActivity";
    private static int NUMBER_OF_COLUMNS = 3;
    private static int PAGES_LOADED = 1;
    private MovieListAdapter mAdapter;
    private FavMoviesDbHelper favMoviesDbHelper;
    private APIUtils.SORT_BY sort_by;
    private ProgressBar progressBar;
    private FavMoviesDBUtils favMoviesDBUtils;
    private SQLiteDatabase database;

    @Override
    protected void onResume() {
        super.onResume();
        PAGES_LOADED=1;
        Log.i(TAG, "onResume: "+"Reset Pages to 1.");
        resetData(favMoviesDBUtils.getAllFavMovies());
    }

    @Override
    public void onMovieClick(int position) {
        Intent callDetailsActivity = new Intent(this, DetailsMovie.class);
        mData.get(position).setIsFavorite(favMoviesDBUtils.isThisMovieAFav(mData.get(position)));
        callDetailsActivity.putExtra(BUNDLE_STRING_KEY, mData.get(position).toCV());
        startActivity(callDetailsActivity);
    }

    private enum MENU_LIST{
        MENU_OPTIONS
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RecyclerView gridView = (RecyclerView) findViewById(R.id.movie_items_gv);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar_loading);

        gridView.setLayoutManager(new GridLayoutManager(this, NUMBER_OF_COLUMNS));

        mAdapter = new MovieListAdapter(getApplicationContext(), this,
                mData);
        gridView.setAdapter(mAdapter);

        favMoviesDbHelper = new FavMoviesDbHelper(this);
        database = favMoviesDbHelper.getWritableDatabase();

        favMoviesDBUtils = new FavMoviesDBUtils(database);

        resetData(favMoviesDBUtils.getAllFavMovies());



        //Get move movie details if scrolled all the way to the end of the list
        gridView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY,
                                       int oldScrollX, int oldScrollY) {
                if(!gridView.canScrollVertically(1)){
                    RefreshData task = new RefreshData();
                    AsyncTaskParms asyncTaskParms = new AsyncTaskParms(sort_by, PAGES_LOADED++);
                    task.execute(asyncTaskParms);
                }

            }
        });

        // Start a thread to download movie details
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sort_by = APIUtils.SORT_BY
                .valueOf(sharedPreferences
                          .getString(getResources().getString(R.string.order_key),
                                     getResources().getString(R.string.order_default))) ;
        RefreshData task = new RefreshData();
        AsyncTaskParms asyncTaskParms = new AsyncTaskParms(sort_by, PAGES_LOADED);
        task.execute(asyncTaskParms);
        PAGES_LOADED++;



    }

    private void resetData(ArrayList<MovieEntry> allFavMovies) {
        if (mData == null)
            mData = allFavMovies;
        else
            mData.addAll(allFavMovies);
        mAdapter.updateData(mData);
    }
    /* For testing
    private ArrayList<MovieEntry> getSigleDatum() {
        ArrayList<MovieEntry> datum = new ArrayList<>();
        datum.add(
                new MovieEntry(1,"/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
                "Title", "Plot", Float.valueOf("1"), "Today"));
        return datum;
    }
    */

        //Async thread to download movie details from the internet utilizing the APIUtils
    private class RefreshData extends AsyncTask<AsyncTaskParms, Void, ArrayList<MovieEntry> >{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Show the progress bar while downloading
            progressBar.setVisibility(View.VISIBLE);
            Log.i(TAG, "onPreExecute: "+"Loading Page "+PAGES_LOADED);
        }

        @Override
        protected ArrayList<MovieEntry> doInBackground(AsyncTaskParms... parms) {
            try {
                Log.i(TAG, "doInBackground: " + "STarted Async Task Refresh Data");
                ArrayList<MovieEntry> results = APIUtils.getMoviesFromServer(parms[0].mPageToLoad,
                                                                            parms[0].mSort_by);
                return results;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<MovieEntry> movieEntries) {
            super.onPostExecute(movieEntries);
            //Hide the progress bar and update the recyclerview
            resetData(movieEntries);
            progressBar.setVisibility(View.INVISIBLE);

            Log.i(TAG, "onPostExecute: " + "Completed internet data transaction");

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_main_options) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }

    private static class AsyncTaskParms{
        public static int mPageToLoad;
        public static APIUtils.SORT_BY mSort_by;

        public AsyncTaskParms(APIUtils.SORT_BY sort_by, int PageToLoad)
        {
            mSort_by = sort_by;
            mPageToLoad = PageToLoad;
        }
    }





}
