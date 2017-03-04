package com.alm.popularmovies.loaders;

import android.content.Context;
import android.net.Uri;

import com.alm.popularmovies.utils.ApiUtils;
import com.alm.popularmovies.api.model.Movie;
import com.alm.popularmovies.utils.NetworkUtils;
import com.alm.popularmovies.utils.PreferenceUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by A. Labay on 25/02/17.
 * As part of the project PopularMovies.
 */

public class MovieListLoader extends TaskLoader<List<Movie>> {

    private int mSortByType, page;

    public MovieListLoader(Context context, int page, int type) {
        super(context);
        this.page = page;
        this.mSortByType = type;
    }

    @Override
    public List<Movie> loadInBackground() {
        if (mSortByType == -1)
            return null;

        try {
            Uri mUri;
            if (mSortByType == PreferenceUtils.SCREEN_RATE)
                mUri = ApiUtils.buildTopRatedMoviesUrl(page);
            else
                mUri = ApiUtils.buildPopularMoviesUrl(page);

            URL url = new URL(mUri.toString());
            String response = NetworkUtils.getResponseFromHttpUrl(url);
            return null; //ApiUtils.parseMovieListResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
