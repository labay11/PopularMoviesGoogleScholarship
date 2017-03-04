package com.alm.popularmovies.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

/**
 * Created by A. Labay on 25/02/17.
 * As part of the project PopularMovies.
 */
public abstract class TaskLoader<T> extends AsyncTaskLoader<T> {

    private T mData;

    public TaskLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if (mData != null) {
            // Use cached data
            deliverResult(mData);
        } else {
            // We have no data, so kick off loading it
            forceLoad();
        }
    }

    @Override
    public void deliverResult(T data) {
        // We’ll save the data for later retrieval
        mData = data;
        // We can do any pre-processing we want here
        // Just remember this is on the UI thread so nothing lengthy!
        super.deliverResult(data);
    }


}
