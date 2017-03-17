package com.alm.popularmovies.ui;

import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alm.popularmovies.R;
import com.alm.popularmovies.adapters.BaseRecyclerAdapter;
import com.alm.popularmovies.adapters.MoviesAdapter;
import com.alm.popularmovies.api.model.Movie;
import com.alm.popularmovies.provider.MovieContract;
import com.alm.popularmovies.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by A. Labay on 25/02/17.
 * As part of the project PopularMovies.
 */

public class FavoritesFragment extends Fragment implements
        BaseRecyclerAdapter.OnItemClickListener<Movie> {

    public static final String TAG = FavoritesFragment.class.getSimpleName();

    private MoviesAdapter mAdapter;

    @BindView(R.id.progress_bar)
    public View mProgressView;
    @BindView(R.id.tv_error)
    public TextView mErrorView;
    @BindView(R.id.rv_fav)
    public RecyclerView mRecyclerView;

    private Subscription mSubscription;

    public static FavoritesFragment create() {
        return new FavoritesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        ButterKnife.bind(this, view);

        setupRV();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        load();

        getActivity().getContentResolver()
                .registerContentObserver(MovieContract.MovieEntry.CONTENT_URI,
                        false,
                        sContentObserver);
    }

    private final Handler mHandler = new Handler();
    private final ContentObserver sContentObserver = new ContentObserver(mHandler) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            load();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSubscription != null)
            mSubscription.unsubscribe();

        getActivity().getContentResolver()
                .unregisterContentObserver(sContentObserver);
    }

    private void setupRV() {
        GridLayoutManager gridLayoutManager =
                new GridLayoutManager(getContext(), Utils.getNumberOfColumns(getActivity()));
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new MoviesAdapter(getActivity(), this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void showData() {
        Utils.crossfade(mRecyclerView, mProgressView);
        mErrorView.setVisibility(View.GONE);
    }

    private void showError() {
        mRecyclerView.setVisibility(View.GONE);
        mProgressView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        mProgressView.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(View itemView, int pos, Movie item) {
        Utils.navigateToDetails(getActivity(), item);
    }

    private void load() {
        if (mSubscription != null)
            return;

        showLoading();

        mSubscription = Observable.create((Observable.OnSubscribe<Cursor>) subscriber -> {
                    subscriber.onNext(getActivity().getContentResolver()
                            .query(MovieContract.MovieEntry.CONTENT_URI,
                                    MovieContract.MovieEntry.PROJECTION,
                                    null,
                                    null,
                                    MovieContract.MovieEntry.COLUMN_TITLE));
                    subscriber.onCompleted();
                })
                .filter(cursor -> cursor != null && cursor.moveToFirst())
                .map(cursor -> {
                    List<Movie> movies = new ArrayList<>(cursor.getCount());
                    do {
                        movies.add(new Movie(
                                cursor.getLong(0),
                                cursor.getInt(1),
                                cursor.getString(2),
                                cursor.getString(3),
                                cursor.getString(4),
                                cursor.getDouble(5),
                                cursor.getLong(6),
                                cursor.getString(7)
                        ));
                    } while (cursor.moveToNext());

                    cursor.close();

                    return movies;
                })
                .filter(movies -> !movies.isEmpty())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movies -> {
                        mAdapter.set(movies);
                        showData();
                    }, throwable -> {
                        Log.i(TAG, "Error loading favorites", throwable);
                        showError();
                    }, () -> mSubscription = null);
    }
}
