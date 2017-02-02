package com.alm.popularmovies;

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

import com.alm.popularmovies.model.Movie;
import com.alm.popularmovies.utils.ApiUtils;
import com.alm.popularmovies.utils.NetworkUtils;
import com.alm.popularmovies.utils.PreferenceUtils;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.OnRecyclerItemClickListener {

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private View mErrorView;

    private MoviesAdapter mAdapter;

    private MoviesAsyncTask mAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mErrorView = findViewById(R.id.container_error);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int dpWidth = (int) (displayMetrics.widthPixels / displayMetrics.density);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, dpWidth / 168);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new MoviesAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        loadMovies(PreferenceUtils.getSortByPreference(this));
    }

    private void loadMovies(int sortBy) {
        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
            mAsyncTask = null;
        }

        if (!NetworkUtils.hasNetworkConnection(this)) {
            showError();
            return;
        }

        mAsyncTask = new MoviesAsyncTask(this);
        mAsyncTask.execute(sortBy);

        showLoading();
    }

    private void showRecyclerView() {
        mErrorView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    private void showLoading() {
        mErrorView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    private void showError() {
        mErrorView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * Executed when the try again button is clicked.
     * @param view the button
     */
    public void onTryAgainClicked(View view) {
        loadMovies(PreferenceUtils.getSortByPreference(this));
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
            mAdapter.setItems(movies);
            showRecyclerView();
        } else {
            mAdapter.clear(); // we don't want those items any more
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
                loadMovies(PreferenceUtils.SORT_BY_POPULARITY);
                return true;

            case R.id.item_sort_by_rate:
                item.setChecked(true);
                PreferenceUtils.setSortByPreference(this, PreferenceUtils.SORT_BY_RATE);
                loadMovies(PreferenceUtils.SORT_BY_RATE);
                return true;

            case R.id.item_feedback:
                sendFeedback();
                return true;

            case R.id.item_about:
                showAboutDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.about)
                .setMessage(R.string.about_message)
                .setNeutralButton(android.R.string.ok, null)
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

        public MoviesAsyncTask(MainActivity activity) {
            mReference = new WeakReference<>(activity);
        }

        @Override
        protected ArrayList<Movie> doInBackground(Integer... integers) {
            int sort_by_type = integers[0];
            try {
                Uri mUri;
                if (sort_by_type == PreferenceUtils.SORT_BY_RATE)
                    mUri = ApiUtils.buildTopRatedMoviesUrl(null);
                else
                    mUri = ApiUtils.buildPopularMoviesUrl(null);

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
