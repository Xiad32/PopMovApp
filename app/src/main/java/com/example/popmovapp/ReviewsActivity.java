package com.example.popmovapp;

import android.content.ContentValues;
import android.content.Intent;
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
import java.text.ParsePosition;
import java.util.ArrayList;

public class ReviewsActivity extends AppCompatActivity {

    private static final String TAG = "ReviewsActivity";
    private ArrayList<ContentValues>  mData;
    private ReviewsListAdapter mAdapter;
    private static RecyclerView reviewView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final RecyclerView reviewView = (RecyclerView) findViewById(R.id.reviewRV);
        reviewView.setLayoutManager(new LinearLayoutManager(this));

        Intent callerIntent = getIntent();

        String title = callerIntent.getStringExtra(DetailsMovie.MOVIE_NAME);
        getSupportActionBar().setTitle("Reviews "+title);

        String reviews_link = callerIntent.getStringExtra(DetailsMovie.REVIEWS_LINK);

        mData = new ArrayList<ContentValues>();
        mAdapter = new ReviewsListAdapter(getApplicationContext(), mData);
        reviewView.setAdapter(mAdapter);

        GetReviews getReviews = new GetReviews();
        getReviews.execute(reviews_link);

    }

    //Async thread to download movie details from the internet utilizing the APIUtils
    private class GetReviews extends AsyncTask<String, Void, ArrayList<ContentValues> > {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<ContentValues> doInBackground(String... parms) {
            Log.i(TAG, "doInBackground: " + "STarted Async Task GetReviews");
            try {
                return APIUtils.getReviewsFromLink(parms[0]);
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
                Toast.makeText(getApplicationContext(), "No Reviews Available For this movie", Toast.LENGTH_SHORT).show();
            if (mData == null)
                mData = results;
            else
                mData.addAll(results);
            mAdapter.updateData(mData);

            Log.i(TAG, "onPostExecute: " + "Completed internet data transaction");
        }
    }



}
