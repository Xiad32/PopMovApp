package com.example.popmovapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
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
    public static final String REVIEWS_LINK = "link_to_reviews" ;
    public static final String MOVIE_NAME = "movie_name";
    private static Intent launcherIntent = null;
    private static String movie_name;
    private static SQLiteDatabase database;
    private static FavMoviesDbHelper favMoviesDbHelper;
    private static FavMoviesDBUtils favMoviesDBUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_movie);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        ImageView thumbnailIV = (ImageView) findViewById(R.id.details_thumbnail);
        TextView  movieTitleTV = (TextView) findViewById(R.id.details_title);
        TextView  movieDateTV = (TextView) findViewById(R.id.details_release);
        TextView  movieRatingsTV = (TextView) findViewById(R.id.details_ratings);
        TextView  moviePlotTV  = (TextView) findViewById(R.id.details_plot);
        final ImageView favStar = (ImageView) findViewById(R.id.details_favStar);
        final ImageView notFavStar = (ImageView) findViewById(R.id.details_notFavStar);
        LinearLayoutCompat trailer = (LinearLayoutCompat) findViewById(R.id.trailerView);
        LinearLayoutCompat reviews = (LinearLayoutCompat) findViewById(R.id.reviewsView);

        favMoviesDbHelper = new FavMoviesDbHelper(this);
        database = favMoviesDbHelper.getWritableDatabase();

        favMoviesDBUtils = new FavMoviesDBUtils(database);



        Intent callingIntent = getIntent();
        //Bundle callingIntentData = callingIntent.getBundleExtra(MainActivity.BUNDLE_STRING_KEY);

        final MovieEntry entry = new MovieEntry( (ContentValues)
                callingIntent.getParcelableExtra(MainActivity.BUNDLE_STRING_KEY));

        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat display  = new SimpleDateFormat("EEE, MMM d, yyyy");
//        getSupportActionBar().setTitle(entry.getTitle());
        movieTitleTV.setText(entry.getTitle());
        movieDateTV.setText(
                display.format(
                        input.parse(entry.getRelease(), new ParsePosition(0))
                )
        );
        movie_name = entry.getTitle();
        moviePlotTV.setText(entry.getPlot());
        movieRatingsTV.setText(String.valueOf(entry.getRatings()));
        Picasso.get()
                .load(APIUtils.resolveImageURL(entry.getPosterURL()))
                .into(thumbnailIV);
        if(entry.isFavorite()) {
            favStar.setVisibility(View.VISIBLE);
            notFavStar.setVisibility(View.INVISIBLE);
        }
        else{
            favStar.setVisibility(View.INVISIBLE);
            notFavStar.setVisibility(View.VISIBLE);
        }


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

        favStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favStar.setVisibility(View.INVISIBLE);
                notFavStar.setVisibility(View.VISIBLE);
                favMoviesDBUtils.removeThisFromFavDB(entry);
                //TODO: remove from favorites
            }
        });

        notFavStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favStar.setVisibility(View.VISIBLE);
                notFavStar.setVisibility(View.INVISIBLE);
                //TODO: add to favorites
                favMoviesDBUtils.addThisToFavDB(entry);
            }
        });

    }


    private static Intent buildIntentFor(Context context, String type, int movieID) {
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

        if (type.equals(APIUtils.LINK_TYPE_VIDEO))
            return new Intent(Intent.ACTION_VIEW, webpage);
        else //Type == REVIEW
        {
            Intent launchIntent = new Intent(context, ReviewsActivity.class);
            launchIntent.putExtra(REVIEWS_LINK, webpage.toString());
            launchIntent.putExtra(MOVIE_NAME, movie_name);
            return launchIntent;
        }
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
            Intent intent = buildIntentFor(getApplicationContext(), parms[0].type, parms[0].id);
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
