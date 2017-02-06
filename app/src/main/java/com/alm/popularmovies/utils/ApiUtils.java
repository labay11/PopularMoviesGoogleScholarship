package com.alm.popularmovies.utils;

import android.net.Uri;
import android.util.Log;

import com.alm.popularmovies.BuildConfig;
import com.alm.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by A. Labay on 24/01/17.
 * As part of the project PopularMovies.
 */

public class ApiUtils {

    public static final String TAG = ApiUtils.class.getSimpleName();

    private static final Uri ENDPOINT = Uri.parse("https://api.themoviedb.org/3");

    private static final SimpleDateFormat API_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static final String IMAGE_SIZE_SMALL = "w185";
    public static final String IMAGE_SIZE_NORMAL = "w342";
    public static final String IMAGE_SIZE_LARGE = "w500";
    public static final String IMAGE_SIZE_XLARGE = "w780";

    public static Uri buildPopularMoviesUrl(int page) {
        return ENDPOINT.buildUpon().appendEncodedPath("movie/popular")
                .appendQueryParameter("api_key", BuildConfig.MOVIEDB_API_KEY)
                .appendQueryParameter("region", Locale.getDefault().getCountry())
                .appendQueryParameter("language", Locale.getDefault().getLanguage())
                .appendQueryParameter("page", "" + page).build();
    }

    public static Uri buildTopRatedMoviesUrl(int page) {
        return ENDPOINT.buildUpon().appendEncodedPath("movie/top_rated")
                .appendQueryParameter("api_key", BuildConfig.MOVIEDB_API_KEY)
                .appendQueryParameter("region", Locale.getDefault().getCountry())
                .appendQueryParameter("language", Locale.getDefault().getLanguage())
                .appendQueryParameter("page", "" + page).build();
    }

    public static Uri buildDetailsUrl(int movieId) {
        return ENDPOINT.buildUpon().appendEncodedPath("movie/" + movieId)
                .appendQueryParameter("api_key", BuildConfig.MOVIEDB_API_KEY)
                .appendQueryParameter("language", Locale.getDefault().getLanguage()).build();
    }

    public static String getImageUrl(String imagePath, String width) {
        return "http://image.tmdb.org/t/p/" + width + "/" + imagePath;
    }

    public static ArrayList<Movie> parseMovieListResponse(String response) throws JSONException {
        Log.i(TAG, "Parsing movies - " + response.substring(0, 15));

        JSONObject root = new JSONObject(response);
        JSONArray results = root.getJSONArray("results");

        if (results == null) {
            // no results, log error and return null
            parseError(root);
            return null;
        }

        ArrayList<Movie> movies = new ArrayList<>(results.length());
        for (int i = 0; i < results.length(); i++) {
            JSONObject o = results.getJSONObject(i);
            movies.add(new Movie(o.getInt("id"), o.getString("title"), o.getString("poster_path")));
        }

        return movies;
    }

    public static Movie parseMovieDetails(String response) throws JSONException {
        Log.i(TAG, "Parsing details - " + response.substring(0, 15));

        JSONObject root = new JSONObject(response);

        if (root.has("status_message")) {
            // no results, log error and return null
            parseError(root);
            return null;
        }

        Date realeseDate = null;
        try {
            realeseDate = API_DATE_FORMAT.parse(root.getString("release_date"));
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date - " + root.getString("release_date"), e);
        }

        return new Movie(root.getInt("id"),
                root.getString("original_title"),
                realeseDate,
                root.getString("poster_path"),
                root.getDouble("vote_average"),
                root.getString("overview"));
    }

    private static void parseError(JSONObject root) throws JSONException {
        String status_message = root.getString("status_message");
        int status_code = root.getInt("status_code");
        Log.e(TAG, String.format("Error getting movie list (%d): %s", status_code, status_message));
    }
}
