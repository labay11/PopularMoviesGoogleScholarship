package com.alm.popularmovies.utils;

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

    public static final String ENDPOINT = "https://api.themoviedb.org/3/";

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

    public static String getImageUrl(String imagePath, String width) {
        return "http://image.tmdb.org/t/p/" + width + "/" + imagePath;
    }

    public static void parseError(JSONObject root) throws JSONException {
        String status_message = root.getString("status_message");
        int status_code = root.getInt("status_code");
        Log.e(TAG, String.format("Error getting movie list (%d): %s", status_code, status_message));
    }
}
