package com.example.popmovapp;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import org.json.*;


public class APIUtils {
    private static String API_KEY = UnshareableKeys.API_KEY;
    private static String API_QUERY = "api_key";
    private static String AUTHORITY = "api.themoviedb.org";
    private static String UTUBE_AUTHORITY= "youtube.com";
    private static String UTUBE_WATCH = "watch";
    private static String UTUBE_VIDOE = "v";
    private static String SCHEME = "https";
    private static String QUERY_TYPE[] = {"movie", "discover",  "movie"};
    private static String QUERY_FILTER[] ={"popular", "movie", "top_rated"};
    private static String THREE = "3";
    private static String LANGUARE_FILTER_QUERY = "language";
    private static String LANGUAGE_FILTER ="en-US";
    private static String SORT_ORDER_QUERY = "sort_by";
    private static String SORT_ORDER[] = {"popularity.desc", "release_date.desc"};
    private static String TAG = "APIUtils";
    private static String IMAGE_AUTHORITY = "image.tmdb.org";
    private static String IMAGE_PATH1 = "t";
    private static String IMAGE_PATH2 = "p";
    private static String IMAGE_SIZE = "w185";
    private static String IMAGE_SIZE_QUERY = "size";
    private static String PAGE_QUERY = "page";
    private static String PAGE = "1";
    private static String RELEASE_DATE_FILTER = "primary_release_date.lte";
    private static String MOVIE = "movie";
    private static String VIDEO = "video";
    private static String REVIEW = "review";

    public static String TRAILER_URL_KEY = "trailer_url";
    public static String LINK_TYPE_VIDEO = "videos";
    public static String LINK_TYPE_REVIEWS = "reviews";
    public static final String REVIEWER_KEY = "cv_reviewer" ;
    public static final String REVIEW_KEY = "cv_review" ;

    public static ArrayList<ContentValues> getReviewsFromLink(String URL) throws JSONException, IOException {
        String review;
        String reviewer;
        ArrayList<ContentValues> results = new ArrayList<ContentValues>();

        String result = getResult(URL);
        JSONObject obj = new JSONObject(result);

        if (obj.has("status_code"))
            return null;    //Error code

        int count = obj.getJSONArray("results").length();
        for (int i = 0; i< count; i++) {
            reviewer = obj.getJSONArray("results").getJSONObject(i).getString("author");
            review = obj.getJSONArray("results").getJSONObject(i).getString("content");
            ContentValues thisOne = new ContentValues();
            thisOne.put(REVIEWER_KEY, reviewer);
            thisOne.put(REVIEW_KEY  , review);
            results.add(thisOne);

        }

        return results;

    }

    public static ArrayList<ContentValues> getTrailersFromLink(String URL) throws IOException, JSONException {
        final String SUPPORTED_SITE = "YouTube";
        String trailerLink;
        //String reviewer;
        ArrayList<ContentValues> results = new ArrayList<ContentValues>();

        String result = getResult(URL);
        JSONObject obj = new JSONObject(result);

        if (obj.has("status_code"))
            return null;    //Error code

        int count = obj.getJSONArray("results").length();
        for (int i = 0; i< count; i++) {
            trailerLink = obj.getJSONArray("results").getJSONObject(i).getString("key");
            if (SUPPORTED_SITE.equals(
                    obj.getJSONArray("results").getJSONObject(i).getString("site") )
                    ) {
                trailerLink = buildYouTubeURL(trailerLink);
            }
            else
                trailerLink = "Unsupported";
            ContentValues thisOne = new ContentValues();
            thisOne.put(TRAILER_URL_KEY, trailerLink);
            results.add(thisOne);
        }

        return results;
    }

    public enum SORT_BY
    {
        SORT_BY_POPULARITY,
        SORT_BY_RELEASE_DATE,
        SORT_BY_TOP_RATED,
        SORT_BY_FAVORITES,
    }

    public static ArrayList<MovieEntry> getMoviesFromServer(int pageToLoad, SORT_BY sort_by) throws IOException, JSONException {
        Log.i(TAG, "getMoviesFromServer: " + "started fetching data from the internet.");
        ArrayList<MovieEntry> list = new ArrayList<>();
        String response;
        //Create the URL to ping
        String URL = downloadListString(pageToLoad, sort_by);

        //Download the data
        response = getResult(URL);


        //parse the data
            //parseJSON:
        JSONObject obj = new JSONObject(response);
            //parse into MovieEntry
        list = parseMovieEntryFromJSON(obj);

        return list;
    }


    //The actual download function
    private static String getResult(String urlString) throws IOException{

        //check urlString form
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //get String Response:
        //Create & Open HTTP connection
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream inputStream = urlConnection.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            if (scanner.hasNext())
            {
                return scanner.next();
            }
            else
            {
                return null;
            }
        } finally {
            //Once complete or failed disconnect
            urlConnection.disconnect();
        }


    }


    //Creates the URL to download the data based on the page to download
    // and the sort by option
    private static String downloadListString(int PageToLoad, SORT_BY sort_by){
        String FUNC_NAME = "downloadListString";
        Uri.Builder uri = new Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(THREE).appendPath(QUERY_TYPE[sort_by.ordinal()])
                .appendPath(QUERY_FILTER[sort_by.ordinal()])
                .appendQueryParameter(LANGUARE_FILTER_QUERY, LANGUAGE_FILTER)
                .appendQueryParameter(API_QUERY, API_KEY)
                .appendQueryParameter(PAGE_QUERY, String.valueOf(PageToLoad));

        if (sort_by == SORT_BY.SORT_BY_RELEASE_DATE){
            uri =   uri.appendQueryParameter(RELEASE_DATE_FILTER, getStringToday());
        }
        if (sort_by != SORT_BY.SORT_BY_TOP_RATED)
            uri =   uri.appendQueryParameter(SORT_ORDER_QUERY, SORT_ORDER[sort_by.ordinal()]);

        String URL = uri.build().toString();
        Log.i(TAG + FUNC_NAME, ": resolved query URL: " + URL);

        return URL;

    }

    //Returns the date of today by fomating the date received from the API
    //to a better format for the GUI display
    private static String getStringToday() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = Calendar.getInstance().getTime();
        return formatter.format(date);
        //return date.toString();
    }

    //Takes the string received from the website and converts it into
    // the correct HTTP URL
    public static String resolveImageURL(String relativePath){
        String FUNC_NAME = "resolveImageURL";
        Uri uri = new Uri.Builder().scheme(SCHEME)
                .authority(IMAGE_AUTHORITY)
                .appendPath(IMAGE_PATH1)
                .appendPath(IMAGE_PATH2)
                .appendPath(IMAGE_SIZE)
                .appendPath(relativePath.substring(1))
                .build();

        String URL = uri.toString();
        Log.i(TAG + FUNC_NAME, ": resolved image URL: " + URL);

        return URL;

    }

    //Load JSON into memory as MovieEntry array
    public static ArrayList<MovieEntry> parseMovieEntryFromJSON(JSONObject obj) throws JSONException {
        ArrayList<MovieEntry> list = new ArrayList<MovieEntry>();
        MovieEntry carrier = null;
        JSONArray arrayResult = obj.getJSONArray("results");
        for (int i=0; i<arrayResult.length(); i++)
        {
            JSONObject thisObj = arrayResult.getJSONObject(i);
            carrier = new MovieEntry(
                    thisObj.getInt("id"),
                    thisObj.getString("poster_path"),
                    thisObj.getString("title"),
                    thisObj.getString("overview"),
                    Float.valueOf(thisObj.getString("vote_average")),
                    thisObj.getString("release_date")
            );
            list.add(carrier);

        }

        return list;

    }

    public static String videoURLFromID (int movieID) throws IOException, JSONException {
        String result = resolveURLWithType(movieID, VIDEO);
        JSONObject obj = new JSONObject(result);
        //parse into MovieEntry
        String key = obj.getJSONArray("results").getJSONObject(0).getString("key");
        String site = obj.getJSONArray("results").getJSONObject(0).getString("site");

        if (site.equals("YouTube"))
            return buildYouTubeURL(key);
        else
            return null;
    }
    public static String reviewsURLFromID (int movieID) throws IOException, JSONException{
        String result =  resolveURLWithType(movieID, REVIEW);
        JSONObject obj = new JSONObject(result);
        //parse into MovieEntry
        String key = obj.getJSONArray("results").getJSONObject(0).getString("key");
        return null;
    }

    public static String resolveURLWithType (int movieID, String type) throws IOException, JSONException {
        String URL;
        String FUNC_NAME = "videoURLFromID";
        Uri.Builder uri = new Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(THREE)
                .appendPath(MOVIE)
                .appendPath(String.valueOf(movieID))
                .appendPath(type)
                .appendQueryParameter(API_QUERY, API_KEY)
                .appendQueryParameter(LANGUARE_FILTER_QUERY, LANGUAGE_FILTER);
        if(type.equals(LINK_TYPE_REVIEWS) )
            uri = uri.appendQueryParameter(PAGE, "1");

        URL = uri.build().toString();
        Log.i(TAG + FUNC_NAME, ": resolved "+type+" URL: "+URL);

        //if(type.equals(LINK_TYPE_REVIEWS))
            return URL;
        /*
        String result = getResult(URL);

        JSONObject obj = new JSONObject(result);

        if (obj.has("status_code"))
            return null;    //Error code

        String key = obj.getJSONArray("results").getJSONObject(0).getString("key");
        String site = obj.getJSONArray("results").getJSONObject(0).getString("site");

        if (site.equals("YouTube"))
            return buildYouTubeURL(key);
        else
            return null;
            */

    }

    private static String buildYouTubeURL(String key) {
        Uri uri = new Uri.Builder().scheme(SCHEME)
                .authority(UTUBE_AUTHORITY)
                .appendPath(UTUBE_WATCH)
                .appendQueryParameter(UTUBE_VIDOE, key)
                .build();
        return uri.toString();
    }
}
