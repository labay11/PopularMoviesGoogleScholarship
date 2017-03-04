package com.alm.popularmovies.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alm.popularmovies.adapters.MoviesAdapter;
import com.alm.popularmovies.R;
import com.alm.popularmovies.loaders.MovieListLoader;
import com.alm.popularmovies.api.model.Movie;
import com.alm.popularmovies.utils.EndlessRecyclerViewOnScrollListener;
import com.alm.popularmovies.utils.NetworkUtils;
import com.alm.popularmovies.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by A. Labay on 25/02/17.
 * As part of the project PopularMovies.
 */

public class MovieListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<List<Movie>>,
        MoviesAdapter.OnRecyclerItemClickListener {

    public static final String TAG = MovieListFragment.class.getSimpleName();

    public static final int START_PAGE = 1;
    private static final int LOADER_ID = 42;

    @BindView(R.id.rv_movies)
    public RecyclerView mRecyclerView;
    @BindView(R.id.progress_bar)
    public ProgressBar mProgressBar;
    @BindView(R.id.loading_view)
    public ProgressBar mLoadingView;
    @BindView(R.id.container_error)
    public View mErrorView;

    private int type = -1;

    private MoviesAdapter mAdapter;

    private EndlessRecyclerViewOnScrollListener mOnScrollListener;

    public static MovieListFragment create(int type) {
        MovieListFragment fragment = new MovieListFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Bundle args = getArguments();
        if (args != null && args.containsKey("type"))
            type = args.getInt("type", -1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        ButterKnife.bind(this, view);

        setupRecyclerView();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey("movies")) {
            ArrayList<Movie> movies = savedInstanceState.getParcelableArrayList("movies");
            int page = savedInstanceState.getInt("page");
            onLoadFinished(null, movies);
            mOnScrollListener.setCurrentPage(page);
        } else {
            loadMovies(START_PAGE);
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
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        // Notify adapter with appropriate notify methods
                        loadMovies(page);
                    }
                });
            }
        };
        mRecyclerView.addOnScrollListener(mOnScrollListener);

        mAdapter = new MoviesAdapter(getActivity(), this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void loadMovies(int page) {
        if (!NetworkUtils.hasNetworkConnection(getActivity())) {
            if (mAdapter.getItemCount() > 0) {
                // we get an error loading new page but we don't want
                // to delete all the previous results so just stop loading
                // results for a while
                mLoadingView.setVisibility(View.GONE);
                Toast.makeText(getActivity(), R.string.no_network, Toast.LENGTH_SHORT).show();
            } else {
                showError();
            }
            return;
        }

        Bundle bundle = new Bundle(1);
        bundle.putInt("page", page);

        getLoaderManager().restartLoader(LOADER_ID, bundle, this);

        showLoading(page);
    }

    private void showContent() {
        mErrorView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
    }

    private void showLoading(int page) {
        mErrorView.setVisibility(View.GONE);
        if (page == 1) {
            mRecyclerView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else
            mLoadingView.setVisibility(View.VISIBLE);
    }

    private void showError() {
        mErrorView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
    }

    private void reset() {
        mOnScrollListener.reset();
        mAdapter.clear();
        mRecyclerView.scrollToPosition(0);
    }

    /**
     * Executed when the try again button is clicked.
     */
    @OnClick(R.id.btn_try_again)
    public void onTryAgainClicked() {
        reset();
        loadMovies(START_PAGE);
    }

    @Override
    public void onRecyclerItemClick(int index, Movie movie) {
        Utils.navigateToDetails(getActivity(), movie);
    }

    @Override
    public void onToggleFavClick(int index, Movie movie) {
        // We are in general screen so we have to mark this movie as fav
        // TODO: implement this in some way if possible

        /*ContentValues values = new ContentValues(3);
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getMovieId());
        values.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPoster_path());

        getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);*/
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        return new MovieListLoader(getActivity(), args.getInt("page"), type);
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {
        if (movies != null) {
            mAdapter.addItems(movies);
            showContent();
        } else if (mAdapter.getItemCount() > 0) {
            // we get an error loading new page but we don't want
            // to delete all the previous results so just stop loading
            // results for a while
            mLoadingView.setVisibility(View.GONE);
            Toast.makeText(getActivity(), R.string.error_toast, Toast.LENGTH_SHORT).show();
        } else {
            reset(); // reset the recycler view items if any
            showError();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) { }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAdapter.getItemCount() > 0) {
            outState.putInt("page", mOnScrollListener.getCurrentPage());
            outState.putParcelableArrayList("movies", mAdapter.getItems());
        }
    }
}
