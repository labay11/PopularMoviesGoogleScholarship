package com.alm.popularmovies.ui.movielist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.alm.popularmovies.R;
import com.alm.popularmovies.adapters.MoviesAdapter;
import com.alm.popularmovies.api.model.Movie;
import com.alm.popularmovies.utils.EndlessRecyclerViewOnScrollListener;
import com.alm.popularmovies.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by A. Labay on 27/02/17.
 * As part of the project PopularMovies.
 */

public class MovieListView extends Fragment
        implements IMovieList.View,
        MoviesAdapter.OnRecyclerItemClickListener {

    private int mType = -1;

    @BindView(R.id.rv_movies)
    public RecyclerView mRecyclerView;
    @BindView(R.id.progress_bar)
    public ProgressBar mProgressBar;
    @BindView(R.id.loading_view)
    public ProgressBar mLoadingBar;
    @BindView(R.id.container_error)
    public View mErrorView;

    private MoviesAdapter mAdapter;
    private EndlessRecyclerViewOnScrollListener mOnScrollListener;

    private MovieListPresenter mPresenter;

    public static MovieListView create(int type) {
        MovieListView frag = new MovieListView();
        Bundle args = new Bundle();
        args.putInt("my_type", type);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null &&
                args.containsKey("my_type")) {
            mType = args.getInt("my_type", -1);
        }

        if (mType < 0) {
            throw new UnsupportedOperationException("Invalid type for MovieListFragment: " + mType);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        ButterKnife.bind(this, view);
        setupRecyclerView();

        mPresenter = new MovieListPresenter(getActivity(), this, mType);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!mPresenter.restoreState(savedInstanceState)) {
            mPresenter.load(MovieListPresenter.START_PAGE);
        }
    }

    private void setupRecyclerView() {
        GridLayoutManager gridLayoutManager =
                new GridLayoutManager(getActivity(), Utils.getNumberOfColumns(getActivity()));
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mOnScrollListener = new EndlessRecyclerViewOnScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(final int page, int totalItemsCount, RecyclerView view) {
                // Delay before notifying the adapter since the scroll listeners
                // can be called while RecyclerView data cannot be changed.
                /*view.post(new Runnable() {
                    @Override
                    public void run() {
                        // Notify adapter with appropriate notify methods
                        mPresenter.load(page);
                    }
                });*/
                mPresenter.load(page);
            }
        };
        mRecyclerView.addOnScrollListener(mOnScrollListener);

        mAdapter = new MoviesAdapter(getActivity(), this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPresenter.saveState(outState);
    }

    @OnClick(R.id.btn_try_again)
    public void onTryAgainClicked() {
        mPresenter.load(MovieListPresenter.START_PAGE);
    }

    @Override
    public void onRecyclerItemClick(int index, Movie movie) {
        Utils.navigateToDetails(getActivity(), movie);
    }

    @Override
    public void onToggleFavClick(int index, Movie movie) {
        // do nothing here for now because we haven't implemented the fav button
    }

    @Override
    public void addMovies(List<Movie> movies) {
        if (movies != null && !movies.isEmpty()) {
            mAdapter.addItems(movies);
            mRecyclerView.setVisibility(View.VISIBLE);
            mErrorView.setVisibility(View.GONE);
            hideLoading();
        } else if (hasMovies()) {
            hideLoading();
        } else {
            showError();
        }
    }

    @Override
    public void showError() {
        mErrorView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mLoadingBar.setVisibility(View.GONE);
    }

    @Override
    public void showLoading(int page) {
        if (page == MovieListPresenter.START_PAGE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mLoadingBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mLoadingBar.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
        mErrorView.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
        mLoadingBar.setVisibility(View.GONE);
    }

    @Override
    public ArrayList<Movie> getMovies() {
        return mAdapter.getItems();
    }

    @Override
    public boolean hasMovies() {
        return mAdapter.getItemCount() > 0;
    }

    @Override
    public void setPage(int page) {
        mOnScrollListener.setCurrentPage(page);
    }
}
