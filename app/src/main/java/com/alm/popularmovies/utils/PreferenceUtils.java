package com.alm.popularmovies.utils;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by A. Labay on 31/01/17.
 * As part of the project PopularMovies.
 */

public class PreferenceUtils {

    private static final String LAST_VISIBLE_SCREEN = "last_visible_screen";
    public static final int SCREEN_POPULAR = 0,
                            SCREEN_RATE = 1,
                            SCREEN_FAVORITES = 2;

    public static int getLastVIsibleScreen(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                    .getInt(LAST_VISIBLE_SCREEN, SCREEN_POPULAR);
    }

    synchronized public static void setLastVisibleScreen(Context context, int screen) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(LAST_VISIBLE_SCREEN, screen)
                .apply();
    }
}
