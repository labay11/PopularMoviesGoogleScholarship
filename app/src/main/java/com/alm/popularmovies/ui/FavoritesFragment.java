package com.alm.popularmovies.ui;

import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alm.popularmovies.R;
import com.alm.popularmovies.adapters.MoviesAdapter;
import com.alm.popularmovies.api.model.Movie;
import com.alm.popularmovies.loaders.FavoritesLoader;
import com.alm.popularmovies.provider.MovieContract;
import com.alm.popularmovies.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by A. Labay on 25/02/17.
 * As part of the project PopularMovies.
 */

public class FavoritesFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<List<Movie>>,
        MoviesAdapter.OnRecyclerItemClickListener {

    private static final int LOADER_ID = 22;

    private MoviesAdapter mAdapter;

    @BindView(R.id.progress_bar)
    public View mProgressView;
    @BindView(R.id.tv_error)
    public TextView mErrorView;
    @BindView(R.id.rv_fav)
    public RecyclerView mRecyclerView;

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
        getLoaderManager().initLoader(LOADER_ID, null, this);

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
            getLoaderManager().restartLoader(LOADER_ID, null, FavoritesFragment.this);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
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
    public void onRecyclerItemClick(int index, Movie movie) {
        Utils.navigateToDetails(getActivity(), movie);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        showLoading();
        return new FavoritesLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        if (data != null && !data.isEmpty()) {
            mAdapter.setItems(data);
            showData();
        } else {
            showError();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) { }
}
