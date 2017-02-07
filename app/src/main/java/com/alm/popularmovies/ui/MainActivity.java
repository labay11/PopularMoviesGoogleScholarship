package com.alm.popularmovies.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alm.popularmovies.MoviesAdapter;
import com.alm.popularmovies.R;
import com.alm.popularmovies.model.Movie;
import com.alm.popularmovies.utils.ApiUtils;
import com.alm.popularmovies.utils.EndlessRecyclerViewOnScrollListener;
import com.alm.popularmovies.utils.NetworkUtils;
import com.alm.popularmovies.utils.PreferenceUtils;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements MoviesAdapter.OnRecyclerItemClickListener {

    public static final int COLUMN_WIDTH_DP = 168;

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar, mLoadingView;
    private View mErrorView;

    private MoviesAdapter mAdapter;

    private EndlessRecyclerViewOnScrollListener mOnScrollListener;

    private MoviesAsyncTask mAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mLoadingView = (ProgressBar) findViewById(R.id.loading_view);
        mErrorView = findViewById(R.id.container_error);

        setupRecyclerView();

        loadMovies(1);
    }

    private void setupRecyclerView() {
        GridLayoutManager gridLayoutManager =
                new GridLayoutManager(this, getNumberOfColumns());
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mOnScrollListener = new EndlessRecyclerViewOnScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page) {
                loadMovies(page);
                mOnScrollListener.setLoading(true);
            }
        };
        mRecyclerView.addOnScrollListener(mOnScrollListener);

        mAdapter = new MoviesAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Calculates the desired number of columns that should show up in the {@link #mRecyclerView}.
     * Based on the experience I use the width of the screen (in dp) and divided by
     * {@link #COLUMN_WIDTH_DP} which is the approx. width that every column should have.
     *
     * @return number of columns
     */
    private int getNumberOfColumns() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int dpWidth = (int) (displayMetrics.widthPixels / displayMetrics.density);
        return dpWidth / COLUMN_WIDTH_DP;
    }

    private void loadMovies(int page) {
        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
            mAsyncTask = null;
        }

        if (!NetworkUtils.hasNetworkConnection(this)) {
            if (mAdapter.getItemCount() > 0) {
                // we get an error loading new page but we don't want
                // to delete all the previous results so just stop loading
                // results for a while
                mLoadingView.setVisibility(View.GONE);
                mOnScrollListener.setLoading(false);
                Toast.makeText(this, R.string.no_network, Toast.LENGTH_SHORT).show();
            } else {
                showError();
            }
            return;
        }

        mAsyncTask = new MoviesAsyncTask(this);
        mAsyncTask.execute(page);

        showLoading(page);
    }

    private void showContent() {
        mErrorView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        mOnScrollListener.setLoading(false);
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
     * @param view the button
     */
    public void onTryAgainClicked(View view) {
        reset();
        loadMovies(1);
    }

    @Override
    public void onRecyclerItemClick(Movie movie) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.EXTRA_MOVIE_ID, movie.getMovieId());
        startActivity(intent);
    }

    /**
     * Called from {@link MoviesAsyncTask#onPostExecute(ArrayList)} after the
     * movies have been fetched.
     *
     * @param movies list of movies to show in the {@link RecyclerView}.
     */
    public void onFinishLoading(ArrayList<Movie> movies) {
        if (movies != null) {
            mAdapter.addItems(movies);
            showContent();
        } else if (mAdapter.getItemCount() > 0) {
            // we get an error loading new page but we don't want
            // to delete all the previous results so just stop loading
            // results for a while
            mLoadingView.setVisibility(View.GONE);
            mOnScrollListener.setLoading(false);
            Toast.makeText(this, R.string.error_toast, Toast.LENGTH_SHORT).show();
        } else {
            reset(); // reset the recycler view items if any
            showError();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        int sortBySaved = PreferenceUtils.getSortByPreference(this);
        // change checked item based on user preferences
        if (sortBySaved == PreferenceUtils.SORT_BY_RATE) {
            menu.findItem(R.id.item_sort_by_rate).setChecked(true);
        } else {
            menu.findItem(R.id.item_sort_by_popularity).setChecked(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_sort_by_popularity:
                item.setChecked(true);
                PreferenceUtils.setSortByPreference(this, PreferenceUtils.SORT_BY_POPULARITY);
                reset();
                loadMovies(1);
                return true;

            case R.id.item_sort_by_rate:
                item.setChecked(true);
                PreferenceUtils.setSortByPreference(this, PreferenceUtils.SORT_BY_RATE);
                reset();
                loadMovies(1);
                return true;

            case R.id.item_feedback:
                sendFeedback();
                return true;

            case R.id.item_about:
                showAboutDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.about)
                .setMessage(R.string.about_message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void sendFeedback() {
        Intent intent = new Intent(Intent.ACTION_SENDTO,
                Uri.fromParts("mailto", "alm.programming.and@gmail.com", null));
        intent.putExtra(Intent.EXTRA_SUBJECT, "PopularMovies Feedback");
        startActivity(intent);
    }

    /**
     * AsyncTask to fetch the movies on a background thread.
     *
     * Connected with {@link MainActivity} with a {@link WeakReference} so,
     * in case the activity is destroyed before the background task finishes,
     * the app does not crash.
     */
    private static class MoviesAsyncTask extends AsyncTask<Integer, Void, ArrayList<Movie>> {

        private WeakReference<MainActivity> mReference;
        private int sort_by_type;

        public MoviesAsyncTask(MainActivity activity) {
            mReference = new WeakReference<>(activity);
            sort_by_type = PreferenceUtils.getSortByPreference(activity);
        }

        @Override
        protected ArrayList<Movie> doInBackground(Integer... integers) {
            int page = integers[0];
            try {
                Uri mUri;
                if (sort_by_type == PreferenceUtils.SORT_BY_RATE)
                    mUri = ApiUtils.buildTopRatedMoviesUrl(page);
                else
                    mUri = ApiUtils.buildPopularMoviesUrl(page);

                URL url = new URL(mUri.toString());
                String response = NetworkUtils.getResponseFromHttpUrl(url);
                return ApiUtils.parseMovieListResponse(response);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            super.onPostExecute(movies);
            if (mReference != null) {
                if (mReference.get() != null) {
                    mReference.get().onFinishLoading(movies);
                }
            }
        }
    }
}
