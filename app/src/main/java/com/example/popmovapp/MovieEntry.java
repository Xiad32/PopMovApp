package com.example.popmovapp;

import android.content.ContentValues;
import android.graphics.Movie;

import java.security.PublicKey;
import java.util.Date;

class MovieEntry {
    private int mID;
    private String mPosterURL;
    private String mTitle;
    private String mPlot;
    private float mRatings;
    private String mRelease;
    private boolean mIsFavorite;

    private static final String ID_KEY = "ID";
    private static final String POSTER_KEY = "POSTER";
    private static final String TITLE_KEY = "TITLE";
    private static final String PLOT_KEY = "PLOT";
    private static final String RATINGS_KEY = "RATINGS";
    private static final String RELEASE_KEY = "RELEASE";
    private static final String IS_FAVORITE_KEY = "ISFAVORITE";

    public MovieEntry(int ID, String PosterURL, String Title, String Plot, float Ratings,
                      String Release) {
        mID = ID;
        mPosterURL = PosterURL;
        mTitle = Title;
        mPlot = Plot;
        mRatings = Ratings;
        mRelease = Release;
        mIsFavorite = false;

    }
    public MovieEntry (ContentValues CV){
        mID = (int) CV.get(ID_KEY);
        mPosterURL = (String) CV.get(POSTER_KEY);
        mTitle = (String) CV.get(TITLE_KEY);
        mPlot = (String ) CV.get(PLOT_KEY);
        mRatings = (float) CV.get(RATINGS_KEY);
        mRelease = (String) CV.get(RELEASE_KEY);
        mIsFavorite = (Boolean) CV.get(IS_FAVORITE_KEY);
    }

    public int getID() {
        return mID;
    }

    public void setID(int ID) {
        this.mID = ID;
    }

    public String getPosterURL() {
        return mPosterURL;
    }

    public void setPosterURL(String posterURL) {
        this.mPosterURL = posterURL;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getPlot() {
        return mPlot;
    }

    public void setPlot(String plot) {
        this.mPlot = plot;
    }

    public float getRatings() {
        return mRatings;
    }

    public void setRatings(float ratings) {
        this.mRatings =ratings;
    }

    public String getRelease() {
        return mRelease;
    }

    public void setRelease(String release) {
        this.mRelease = release;
    }

    public ContentValues toCV() {
        ContentValues CV = new ContentValues();
        CV.put(ID_KEY, mID);
        CV.put(POSTER_KEY, mPosterURL);
        CV.put(TITLE_KEY, mTitle);
        CV.put(PLOT_KEY, mPlot);
        CV.put(RATINGS_KEY,mRatings);
        CV.put(RELEASE_KEY, mRelease);
        CV.put(IS_FAVORITE_KEY, mIsFavorite);
        return CV;
    }

    public boolean isFavorite() {
        return mIsFavorite;
    }

    public void setIsFavorite(boolean favorite){
        mIsFavorite = favorite;
    }
}
