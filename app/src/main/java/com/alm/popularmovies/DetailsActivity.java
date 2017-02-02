package com.alm.popularmovies;

import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alm.popularmovies.model.MovieDetails;
import com.alm.popularmovies.utils.ApiUtils;
import com.alm.popularmovies.utils.NetworkUtils;
import com.alm.popularmovies.utils.Utils;
import com.bumptech.glide.Glide;
import com.github.florent37.glidepalette.BitmapPalette;
import com.github.florent37.glidepalette.GlidePalette;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.text.DateFormat;

/**
 * Created by A. Labay on 01/02/17.
 * As part of the project PopularMovies.
 */

public class DetailsActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE_ID = "extra_movie_id";

    private int movieId = -1;

    private MovieDetails mDetails = null;

    private DetailsAsyncTask mTask;

    private ImageView ivPoster;
    private CollapsingToolbarLayout titleView;
    private TextView tvDate, tvRating, tvSummary;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        setupToolbar();

        ivPoster = (ImageView) findViewById(R.id.iv_backdrop);
        titleView = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        tvDate = (TextView) findViewById(R.id.tv_date);
        tvRating = (TextView) findViewById(R.id.tv_rating);
        tvSummary = (TextView) findViewById(R.id.tv_summary);

        if (savedInstanceState != null) {
            movieId = savedInstanceState.getInt("saved_movie_id", -1);
            mDetails = savedInstanceState.getParcelable("saved_movie");
        }

        if (movieId == -1) {
            movieId = getIntent().getIntExtra(EXTRA_MOVIE_ID, -1);
        }

        if (mDetails != null) {
            onFinishLoading(mDetails);
        } else if (movieId != -1) {
            loadDetails();
        } else {
            Toast.makeText(this, R.string.error_toast, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
    }

    private void loadDetails() {
        if (mTask != null) {
            mTask.cancel(true);
            mTask = null;
        }

        if (!NetworkUtils.hasNetworkConnection(this)) {
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mTask = new DetailsAsyncTask(this);
        mTask.execute(movieId);
    }

    public void onFinishLoading(MovieDetails details) {
        if (details == null) {
            Toast.makeText(this, R.string.error_toast, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mDetails = details;

        if (Utils.isPortrait(this))
            titleView.setTitle(details.title);
        else
            ((TextView) findViewById(R.id.tv_title)).setText(details.title);

        tvDate.setText(DateFormat.getDateInstance().format(details.releaseDate));
        tvRating.setText(String.format("%.1f", details.voteAverage));
        tvSummary.setText(details.synopsis);

        String url = ApiUtils.getImageUrl(details.imagePath, ApiUtils.IMAGE_SIZE_LARGE);
        Glide.with(this)
                .load(url)
                .listener(GlidePalette.with(url)
                        .intoCallBack(new BitmapPalette.CallBack() {
                            @Override
                            public void onPaletteLoaded(@Nullable Palette palette) {
                                if (palette != null && Utils.isPortrait(DetailsActivity.this)) {
                                    int textColor = palette.getLightVibrantColor(Color.WHITE);
                                    titleView.setExpandedTitleColor(textColor);
                                    titleView.setCollapsedTitleTextColor(Color.WHITE);
                                }
                            }
                        })
                )
                .into(ivPoster);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mDetails != null)
            outState.putParcelable("saved_movie", mDetails);
        outState.putInt("saved_movie_id", movieId);
    }

    private static class DetailsAsyncTask extends AsyncTask<Integer, Void, MovieDetails> {

        private WeakReference<DetailsActivity> mReference;

        public DetailsAsyncTask(DetailsActivity activity) {
            mReference = new WeakReference<>(activity);
        }

        @Override
        protected MovieDetails doInBackground(Integer... integers) {
            int movie_id = integers[0];
            try {
                Uri mUri = ApiUtils.buildDetailsUrl(movie_id);
                URL url = new URL(mUri.toString());
                String response = NetworkUtils.getResponseFromHttpUrl(url);
                return ApiUtils.parseMovieDetails(response);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(MovieDetails movies) {
            super.onPostExecute(movies);
            if (mReference != null) {
                if (mReference.get() != null) {
                    mReference.get().onFinishLoading(movies);
                }
            }
        }
    }
}
