package com.example.popmovapp;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.popmovapp.APIUtils.TRAILER_URL_KEY;

public class TrailersActivity extends AppCompatActivity
    implements TrailersListAdapter.TrailerClickListener{

    private static final String TAG = "ReviewsActivity";
    private ArrayList<ContentValues> mData;
    private TrailersListAdapter mAdapter;
    private static RecyclerView reviewView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final RecyclerView trailersView = (RecyclerView) findViewById(R.id.trailersRV);
        trailersView.setLayoutManager(new LinearLayoutManager(this));

        Intent callerIntent = getIntent();

        String title = callerIntent.getStringExtra(DetailsMovie.MOVIE_NAME);
        getSupportActionBar().setTitle("Trailers "+title);

        String trailers_link = callerIntent.getStringExtra(DetailsMovie.TRAILERS_LINK);

        mData = new ArrayList<ContentValues>();
        mAdapter = new TrailersListAdapter(getApplicationContext(), mData, this);
        trailersView.setAdapter(mAdapter);

        GetTrailers getTrailers = new GetTrailers();
        getTrailers.execute(trailers_link);

    }

    @Override
    public void onTrailerClick(int position) {
        Intent launchVideo = new Intent(Intent.ACTION_VIEW,
                Uri.parse(mData.get(position).getAsString(TRAILER_URL_KEY)));
        if (launchVideo.resolveActivity(getPackageManager()) != null)
            startActivity(launchVideo);
    }

    //Async thread to download movie details from the internet utilizing the APIUtils
    private class GetTrailers extends AsyncTask<String, Void, ArrayList<ContentValues> > {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<ContentValues> doInBackground(String... parms) {
            Log.i(TAG, "doInBackground: " + "STarted Async Task GetReviews");
            try {
                return APIUtils.getTrailersFromLink(parms[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<ContentValues> results) {
            super.onPostExecute(results);
            if (results == null) {
                return;
            }
            if (results.size() == 0)
                Toast.makeText(getApplicationContext(), "No Trailers Available For this movie", Toast.LENGTH_SHORT).show();
            if (mData == null)
                mData = results;
            else
                mData.addAll(results);
            mAdapter.updateData(mData);

            Log.i(TAG, "onPostExecute: " + "Completed internet data transaction");
        }
    }

}
