package com.alm.popularmovies.utils;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by A. Labay on 31/01/17.
 * As part of the project PopularMovies.
 */

public class PreferenceUtils {

    public static final String SORT_BY = "sort_by";
    public static final int SORT_BY_POPULARITY = 0,
                            SORT_BY_RATE = 1;

    public static int getSortByPreference(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                    .getInt(SORT_BY, SORT_BY_POPULARITY);
    }

    public static void setSortByPreference(Context context, int sortBy) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(SORT_BY, sortBy)
                .apply();
    }
}
