package com.example.popmovapp;

import android.content.ContentValues;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DetailsMovie extends AppCompatActivity {

    private static final String TAG = "DetailsMovie";
    private static Intent launcherIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_movie);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView thumbnailIV = (ImageView) findViewById(R.id.details_thumbnail);
        TextView  movieTitleTV = (TextView) findViewById(R.id.details_title);
        TextView  movieDateTV = (TextView) findViewById(R.id.details_release);
        TextView  movieRatingsTV = (TextView) findViewById(R.id.details_ratings);
        TextView  moviePlotTV  = (TextView) findViewById(R.id.details_plot);
        LinearLayoutCompat trailer = (LinearLayoutCompat) findViewById(R.id.trailerView);
        LinearLayoutCompat reviews = (LinearLayoutCompat) findViewById(R.id.reviewsView);


        Intent callingIntent = getIntent();
        //Bundle callingIntentData = callingIntent.getBundleExtra(MainActivity.BUNDLE_STRING_KEY);

        final MovieEntry entry = new MovieEntry( (ContentValues)
                callingIntent.getParcelableExtra(MainActivity.BUNDLE_STRING_KEY));

        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat display  = new SimpleDateFormat("EEE, MMM d, yyyy");
        getSupportActionBar().setTitle(entry.getTitle());
        movieTitleTV.setText(entry.getTitle());
        movieDateTV.setText(
                display.format(
                        input.parse(entry.getRelease(), new ParsePosition(0))
                )
        );
        moviePlotTV.setText(entry.getPlot());
        movieRatingsTV.setText(String.valueOf(entry.getRatings()));
        Picasso.get()
                .load(APIUtils.resolveImageURL(entry.getPosterURL()))
                .into(thumbnailIV);




        trailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent launchTrailerViewer = buildIntentFor("video", entry.getID());

                AsyncParms asyncParms = new AsyncParms(APIUtils.LINK_TYPE_VIDEO, entry.getID());
                GetLink getLink = new GetLink();
                getLink.execute(asyncParms);

            }
        });
        reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncParms asyncParms = new AsyncParms(APIUtils.LINK_TYPE_REVIEWS, entry.getID());
                GetLink getLink = new GetLink();
                getLink.execute(asyncParms);
            }
        });

    }


    private static Intent buildIntentFor(String type, int movieID){
        Uri webpage = null;
        try {
                webpage = Uri.parse(APIUtils.resolveURLWithType(movieID, type));
            } catch (IOException e1) {
            e1.printStackTrace();
            } catch (JSONException e) {
            e.printStackTrace();
        }

        if (webpage == null)
            return null;

        return new Intent(Intent.ACTION_VIEW, webpage);
    }

    //Async thread to download movie details from the internet utilizing the APIUtils
    private class GetLink extends AsyncTask<AsyncParms, Void, Intent>  {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Intent doInBackground(AsyncParms... parms) {
            Log.i(TAG, "doInBackground: " + "STarted Async Task GetLink");
            Intent intent = buildIntentFor(parms[0].type, parms[0].id);
            return intent;
        }

        @Override
        protected void onPostExecute(Intent intent) {
            super.onPostExecute(intent);
            if (intent == null) {
                Toast.makeText(getApplicationContext(), "No Trailer Available For this movie", Toast.LENGTH_SHORT).show();
                return;
            }
            if (intent.resolveActivity(getPackageManager()) != null)
                startActivity(intent);

            Log.i(TAG, "onPostExecute: " + "Completed internet data transaction");
        }
    }

    private static class AsyncParms{
        public static int id;
        public static String type;

        public AsyncParms(String mType, int movieID)
        {
            id = movieID;
            type = mType;
        }
    }

}
