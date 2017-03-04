package com.alm.popularmovies.utils;

import android.net.Uri;
import android.util.Log;

import com.alm.popularmovies.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by A. Labay on 24/01/17.
 * As part of the project PopularMovies.
 */

public class ApiUtils {

    public static final String TAG = ApiUtils.class.getSimpleName();

    private static final Uri ENDPOINT = Uri.parse("https://api.themoviedb.org/3");

    public static final String API_KEY = BuildConfig.MOVIEDB_API_KEY;

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final SimpleDateFormat API_DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT);

    public static final String IMAGE_SIZE_SMALL = "w185";
    public static final String IMAGE_SIZE_NORMAL = "w342";
    public static final String IMAGE_SIZE_LARGE = "w500";
    public static final String IMAGE_SIZE_XLARGE = "w780";

    public static String getDefaultRegion() {
        return Locale.getDefault().getCountry();
    }

    public static String getDefaultLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public static Uri buildPopularMoviesUrl(int page) {
        return ENDPOINT.buildUpon()
                .appendEncodedPath("movie/popular")
                .appendQueryParameter("api_key", BuildConfig.MOVIEDB_API_KEY)
                .appendQueryParameter("region", Locale.getDefault().getCountry())
                .appendQueryParameter("language", Locale.getDefault().getLanguage())
                .appendQueryParameter("page", "" + page)
                .build();
    }

    public static Uri buildTopRatedMoviesUrl(int page) {
        return ENDPOINT.buildUpon()
                .appendEncodedPath("movie/top_rated")
                .appendQueryParameter("api_key", BuildConfig.MOVIEDB_API_KEY)
                .appendQueryParameter("region", Locale.getDefault().getCountry())
                .appendQueryParameter("language", Locale.getDefault().getLanguage())
                .appendQueryParameter("page", "" + page)
                .build();
    }

    public static Uri buildDetailsUrl(int movieId) {
        return ENDPOINT.buildUpon().appendEncodedPath("movie/" + movieId)
                .appendQueryParameter("api_key", BuildConfig.MOVIEDB_API_KEY)
                .appendQueryParameter("language", Locale.getDefault().getLanguage())
                .build();
    }

    public static Uri buildVideosUrl(int movieId) {
        return ENDPOINT.buildUpon()
                .appendEncodedPath("movie")
                .appendEncodedPath("" + movieId)
                .appendEncodedPath("videos")
                .appendQueryParameter("api_key", BuildConfig.MOVIEDB_API_KEY)
                .build();
    }

    public static Uri buildReviewsUrl(int movieId) {
        return ENDPOINT.buildUpon()
                .appendEncodedPath("movie")
                .appendEncodedPath("" + movieId)
                .appendEncodedPath("reviews")
                .appendQueryParameter("api_key", BuildConfig.MOVIEDB_API_KEY)
                .appendQueryParameter("page", "1")
                .build();
    }

    public static String getImageUrl(String imagePath, String width) {
        return "http://image.tmdb.org/t/p/" + width + "/" + imagePath;
    }

    /*public static ArrayList<Movie> parseMovieListResponse(String response) throws JSONException {
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
            movies.add(new Movie(
                    o.getInt("id"),
                    o.getString("title"),
                    o.getString("poster_path")
            ));
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

        return new Movie(
                root.getInt("id"),
                root.getString("original_title"),
                realeseDate,
                root.getString("poster_path"),
                (float) root.getDouble("vote_average"),
                root.getString("overview")
        );
    }

    public static List<Video> parseVideos(String response) throws JSONException {
        Log.i(TAG, "Parsing videos - " + response.substring(0, 15));

        JSONObject root = new JSONObject(response);

        if (root.has("status_message")) {
            // no results, log error and return null
            parseError(root);
            return null;
        }

        int movieId = root.getInt("id");
        JSONArray results = root.getJSONArray("results");
        List<Video> videos = new ArrayList<>(results.length());

        for (int i = 0; i < videos.size(); i++) {
            JSONObject o = results.getJSONObject(i);
            if (!o.getString("site").equals("YouTube"))
                continue;

            videos.add(new Video(
                    movieId,
                    o.getString("name"),
                    o.getString("key")
            ));
        }

        return videos;
    }

    public static List<Review> parseReviews(String response) throws JSONException {
        Log.i(TAG, "Parsing reviews - " + response.substring(0, 15));

        JSONObject root = new JSONObject(response);

        if (root.has("status_message")) {
            // no results, log error and return null
            parseError(root);
            return null;
        }

        int movieId = root.getInt("id");
        JSONArray results = root.getJSONArray("results");
        List<Review> reviews = new ArrayList<>(results.length());

        for (int i = 0; i < reviews.size(); i++) {
            JSONObject o = results.getJSONObject(i);

            reviews.add(new Review(
                    movieId,
                    o.getString("id"),
                    o.getString("author"),
                    o.getString("content"),
                    o.getString("url")
            ));
        }

        return reviews;
    }*/

    public static void parseError(JSONObject root) throws JSONException {
        String status_message = root.getString("status_message");
        int status_code = root.getInt("status_code");
        Log.e(TAG, String.format("Error getting movie list (%d): %s", status_code, status_message));
    }
}
