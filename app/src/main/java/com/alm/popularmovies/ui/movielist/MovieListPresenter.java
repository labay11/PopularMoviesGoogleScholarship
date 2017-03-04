package com.alm.popularmovies.ui.movielist;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.alm.popularmovies.PopularMoviesApp;
import com.alm.popularmovies.R;
import com.alm.popularmovies.utils.ApiUtils;
import com.alm.popularmovies.api.TheMovieDbService;
import com.alm.popularmovies.api.model.Movie;
import com.alm.popularmovies.api.model.Movies;
import com.alm.popularmovies.utils.NetworkUtils;
import com.alm.popularmovies.utils.PreferenceUtils;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by A. Labay on 27/02/17.
 * As part of the project PopularMovies.
 */

public class MovieListPresenter implements IMovieList.Presenter {

    public static final String TAG = MovieListPresenter.class.getSimpleName();

    public static final int START_PAGE = 1;

    private Context mContext;
    private IMovieList.View mView;
    private int mType;
    private TheMovieDbService mService;
    private int mPage = START_PAGE;
    private Subscription mSubscription = null;

    public MovieListPresenter(Context context,
                              IMovieList.View view,
                              int type) {
        mContext = context;
        mView = view;
        mType = type;

        mService = ((PopularMoviesApp) mContext.getApplicationContext())
                .getService();
    }

    @Override
    public boolean restoreState(@Nullable Bundle state) {
        if (state != null && state.containsKey("movies")) {
            ArrayList<Movie> movies = state.getParcelableArrayList("movies");
            if (movies != null && !movies.isEmpty()) {
                mPage = state.getInt("page");
                mView.addMovies(movies);
                mView.setPage(mPage);
                return true;
            }
        }

        return false;
    }

    @Override
    public void saveState(Bundle state) {
        if (mView.hasMovies()) {
            state.putParcelableArrayList("movies", mView.getMovies());
            state.putInt("page", mPage);
        }
    }

    @Override
    public void onDestroy() {
        if (mSubscription != null)
            mSubscription.unsubscribe();
    }

    @Override
    public void load(int page) {
        if (!NetworkUtils.hasNetworkConnection(mContext)) {
            if (mView.hasMovies()) {
                // we get an error loading new page but we don't want
                // to delete all the previous results so just stop loading
                // results for a while
                mView.hideLoading();
                Toast.makeText(mContext, R.string.no_network, Toast.LENGTH_SHORT).show();
            } else {
                mView.showError();
            }
            return;
        }
        Log.i(TAG, "load() -> " + page);

        /*Bundle bundle = new Bundle(1);
        bundle.putInt("page", page);*/
        mPage = page;

        Observable<Movies> observable;
        if (mType == PreferenceUtils.SCREEN_RATE)
            observable = createTopRatedMoviesCallback();
        else
            observable = createPopularMoviesCallback();

        mSubscription = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Movies>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        Log.i(TAG, "observable -> onStart()");
                        mView.showLoading(mPage);
                    }

                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "observable -> onCompleted()");
                        mView.hideLoading();
                        mSubscription = null;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.w(TAG, "Error loading data.", e);
                        mView.showError();
                    }

                    @Override
                    public void onNext(Movies movies) {
                        Log.i(TAG, "observable -> onNext() -> " + movies);
                        if (movies != null) {
                            mView.addMovies(movies.results);
                        } else if (mView.hasMovies()) {
                            // we get an error loading new page but we don't want
                            // to delete all the previous results so just stop loading
                            // results for a while
                            mView.hideLoading();
                            Toast.makeText(mContext, R.string.error_toast, Toast.LENGTH_SHORT).show();
                        } else {
                            onError(null);
                        }
                    }
                });
    }

    private Observable<Movies> createPopularMoviesCallback() {
        return mService.getPopularMovies(ApiUtils.API_KEY,
                ApiUtils.getDefaultRegion(),
                ApiUtils.getDefaultLanguage(),
                mPage);
    }

    private Observable<Movies> createTopRatedMoviesCallback() {
        return mService.getTopRatedMovies(ApiUtils.API_KEY,
                ApiUtils.getDefaultRegion(),
                ApiUtils.getDefaultLanguage(),
                mPage);
    }
}
