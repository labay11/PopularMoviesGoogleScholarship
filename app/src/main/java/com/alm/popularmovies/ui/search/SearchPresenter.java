package com.alm.popularmovies.ui.search;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alm.popularmovies.PopularMoviesApp;
import com.alm.popularmovies.api.TheMovieDbService;
import com.alm.popularmovies.api.model.Movie;
import com.alm.popularmovies.utils.ApiUtils;

import java.util.ArrayList;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by A. Labay on 12/03/17.
 * As part of the project PopularMovies.
 */
public class SearchPresenter implements ISearch.Presenter {

    private Context mContext;

    private ISearch.View mView;

    private String mLastQuery;

    private TheMovieDbService mService;

    private Subscription mSubscription = null;

    public SearchPresenter(Context context, ISearch.View view) {
        mContext = context;
        mView = view;

        mService = ((PopularMoviesApp) mContext.getApplicationContext())
                .getService();
    }

    @Override
    public void query(@NonNull final String q) {
        mService.search(ApiUtils.API_KEY,
                    q,
                    ApiUtils.getDefaultRegion(),
                    ApiUtils.getDefaultLanguage())
                .filter(movies -> movies != null && movies.results != null)
                .map(movies -> movies.results)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movies -> {
                            mView.showResults(movies);
                            mLastQuery = q;
                        },
                        throwable -> mView.showError(),
                        () -> mSubscription = null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null
                && savedInstanceState.containsKey("my_query")) {
            mLastQuery = savedInstanceState.getString("my_query");
            ArrayList<Movie> movies = savedInstanceState.getParcelableArrayList("my_results");
            mView.setQuery(mLastQuery);
            mView.showResults(movies);
        }
    }

    @Override
    public void saveState(Bundle outState) {
        if (!TextUtils.isEmpty(mLastQuery)) {
            outState.putParcelableArrayList("my_results", mView.getResults());
            outState.putString("my_query", mLastQuery);
        }
    }

    @Override
    public void onDestroy() {
        if (mSubscription != null)
            mSubscription.unsubscribe();
    }
}
