package com.alm.popularmovies.utils;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.alm.popularmovies.BuildConfig;
import com.alm.popularmovies.model.Movie;
import com.alm.popularmovies.model.MovieDetails;

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

    public static final Uri ENDPOINT = Uri.parse("https://api.themoviedb.org/3");

    public static final SimpleDateFormat API_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static final String IMAGE_SIZE_NORMAL = "w185";
    public static final String IMAGE_SIZE_LARGE = "w342";

    public static Uri buildPopularMoviesUrl(String page) {
        return ENDPOINT.buildUpon().appendEncodedPath("movie/popular")
                .appendQueryParameter("api_key", BuildConfig.MOVIEDB_API_KEY)
                .appendQueryParameter("region", Locale.getDefault().getCountry())
                .appendQueryParameter("language", Locale.getDefault().getLanguage())
                .appendQueryParameter("page", TextUtils.isEmpty(page) ? "1" : page).build();
    }

    public static Uri buildTopRatedMoviesUrl(String page) {
        return ENDPOINT.buildUpon().appendEncodedPath("movie/top_rated")
                .appendQueryParameter("api_key", BuildConfig.MOVIEDB_API_KEY)
                .appendQueryParameter("region", Locale.getDefault().getCountry())
                .appendQueryParameter("language", Locale.getDefault().getLanguage())
                .appendQueryParameter("page", TextUtils.isEmpty(page) ? "1" : page).build();
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
        Log.i(TAG, "Parsing movies - " + response.substring(50));

        JSONObject root = new JSONObject(response);
        JSONArray results = root.getJSONArray("results");

        ArrayList<Movie> movies = new ArrayList<>(results.length());
        for (int i = 0; i < results.length(); i++) {
            JSONObject o = results.getJSONObject(i);
            movies.add(new Movie(o.getInt("id"), o.getString("title"), o.getString("poster_path")));
        }

        return movies;
    }

    public static MovieDetails parseMovieDetails(String response) throws JSONException {
        Log.i(TAG, "Parsing details - " + response.substring(50));

        JSONObject root = new JSONObject(response);

        Date realeseDate = null;
        try {
            realeseDate = API_DATE_FORMAT.parse(root.getString("release_date"));
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date - " + root.getString("release_date"), e);
        }

        return new MovieDetails(root.getInt("id"),
                root.getString("title"),
                realeseDate,
                root.getString("backdrop_path"),
                root.getDouble("vote_average"),
                root.getString("overview"));
    }
}
