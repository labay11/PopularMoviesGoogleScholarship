package com.alm.popularmovies.utils;

import android.content.Context;
import android.content.res.Configuration;

/**
 * Created by A. Labay on 01/02/17.
 * As part of the project PopularMovies.
 */

public class Utils {

    public static boolean isPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }
}
