package com.alm.popularmovies;

import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alm.popularmovies.model.MovieDetails;
import com.alm.popularmovies.utils.ApiUtils;
import com.alm.popularmovies.utils.NetworkUtils;
import com.alm.popularmovies.utils.Utils;
import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;

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
    private ProgressBar mLoadingView;
    private View mContent, mErrorView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        setupToolbar();
        if (Utils.isPortrait(this))
            setupPosterSize();

        ivPoster = (ImageView) findViewById(R.id.iv_backdrop);
        titleView = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        tvDate = (TextView) findViewById(R.id.tv_date);
        tvRating = (TextView) findViewById(R.id.tv_rating);
        tvSummary = (TextView) findViewById(R.id.tv_summary);
        mLoadingView = (ProgressBar) findViewById(R.id.progress_bar);
        mContent = findViewById(R.id.content);
        mErrorView = findViewById(R.id.container_error);

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

    private void setupPosterSize() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        //float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        float dpHeight = displayMetrics.heightPixels;

        AppBarLayout appBar = (AppBarLayout) findViewById(R.id.appbar);
        appBar.setLayoutParams(new CoordinatorLayout
                .LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, (int) (dpHeight * 3.0 / 4.0)));
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

        showLoading();
    }

    public void onTryAgainClicked(View v) {
        loadDetails();
    }

    private void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
        mContent.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);
    }

    private void showContent() {
        mErrorView.setVisibility(View.GONE);
        Utils.crossfade(mContent, mLoadingView);
    }

    private void showError() {
        mErrorView.setVisibility(View.VISIBLE);
        mContent.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
    }

    public void onFinishLoading(MovieDetails details) {
        if (details == null) {
            showError();
            return;
        }

        mDetails = details;
        populateView();
        showContent();
    }

    private void populateView() {
        if (Utils.isPortrait(this))
            titleView.setTitle(mDetails.title);
        else
            ((TextView) findViewById(R.id.tv_title)).setText(mDetails.title);

        tvDate.setText(DateFormat.getDateInstance().format(mDetails.releaseDate));
        tvRating.setText(String.format("%.1f", mDetails.voteAverage));

        if (TextUtils.isEmpty(mDetails.synopsis) || mDetails.synopsis.equals("null"))
            tvSummary.setText(R.string.summary_not_available);
        else
            tvSummary.setText(mDetails.synopsis);

        if (!mDetails.hasImage()) {
            ivPoster.setVisibility(View.GONE);
            return;
        }

        String url = ApiUtils.getImageUrl(mDetails.posterPath, ApiUtils.IMAGE_SIZE_LARGE);
        Picasso.with(this)
                .load(url)
                .into(ivPoster,
                        PicassoPalette.with(url, ivPoster)
                                .intoCallBack(new PicassoPalette.CallBack() {
                                    @Override
                                    public void onPaletteLoaded(Palette palette) {
                                        if (palette != null && Utils.isPortrait(DetailsActivity.this)) {
                                            int textColor = palette.getLightMutedColor(Color.WHITE);
                                            titleView.setExpandedTitleColor(textColor);
                                            titleView.setCollapsedTitleTextColor(Color.WHITE);
                                        }
                                    }
                                }));
        /*Glide.with(this)
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
                .into(ivPoster);*/
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
