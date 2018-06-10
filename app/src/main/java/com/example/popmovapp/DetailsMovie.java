package com.example.popmovapp;

import android.content.ContentValues;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailsMovie extends AppCompatActivity {

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

        Intent callingIntent = getIntent();
        //Bundle callingIntentData = callingIntent.getBundleExtra(MainActivity.BUNDLE_STRING_KEY);

        MovieEntry entry = new MovieEntry( (ContentValues)
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

    }



}
