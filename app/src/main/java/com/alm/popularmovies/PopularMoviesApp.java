package com.alm.popularmovies;

import android.app.Application;

import com.alm.popularmovies.api.TheMovieDbService;
import com.alm.popularmovies.api.TheMovieDbServiceImpl;

/**
 * Created by A. Labay on 27/02/17.
 * As part of the project PopularMovies.
 */

public class PopularMoviesApp extends Application {

    private TheMovieDbService mService;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public synchronized TheMovieDbService getService() {
        if (mService == null) {
            mService = TheMovieDbServiceImpl.create();
        }

        return mService;
    }
}
