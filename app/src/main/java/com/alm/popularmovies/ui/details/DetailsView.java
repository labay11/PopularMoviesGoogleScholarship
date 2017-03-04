package com.alm.popularmovies.ui.details;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alm.popularmovies.R;
import com.alm.popularmovies.adapters.BaseRecyclerAdapter;
import com.alm.popularmovies.adapters.ReviewsAdapter;
import com.alm.popularmovies.adapters.VideosAdapter;
import com.alm.popularmovies.utils.ApiUtils;
import com.alm.popularmovies.api.model.Movie;
import com.alm.popularmovies.api.model.Review;
import com.alm.popularmovies.api.model.Video;
import com.alm.popularmovies.utils.Utils;
import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by A. Labay on 26/02/17.
 * As part of the project PopularMovies.
 */

public class DetailsView extends AppCompatActivity implements IDetailsMVP.View {

    public static final String EXTRA_MOVIE = "extra_movie";

    @BindView(R.id.pb_videos)
    public ProgressBar mVideosPb;
    @BindView(R.id.rv_videos)
    public RecyclerView mVideosRv;
    @BindView(R.id.tv_error_videos)
    public TextView mVideosErrorTv;

    @BindView(R.id.pb_reviews)
    public ProgressBar mReviewsPb;
    @BindView(R.id.rv_reviews)
    public RecyclerView mReviewsRv;
    @BindView(R.id.tv_error_reviews)
    public TextView mReviewsErrorTv;

    @BindView(R.id.appbar)
    public View mAppBar;
    @BindView(R.id.iv_backdrop)
    public ImageView mIvPoster;
    @BindView(R.id.tv_date)
    public TextView mDateTv;
    @BindView(R.id.rating_bar)
    public RatingBar mRatingBar;
    @BindView(R.id.tv_summary)
    public TextView mSummaryTv;
    @BindView(R.id.fab)
    public FloatingActionButton fab;
    @BindView(R.id.tv_title)
    public TextView mTitleTv;

    @BindColor(R.color.colorPrimary)
    public int mPrimaryColor;

    public CollapsingToolbarLayout mTitleView;

    private IDetailsMVP.Presenter mPresenter;

    private VideosAdapter mVideosAdapter;
    private ReviewsAdapter mReviewsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        setupToolbar();
        if (Utils.isPortrait(this))
            setupPosterSize();

        ButterKnife.bind(this);

        // only exists when vertical
        mTitleView = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);

        if (savedInstanceState == null || !savedInstanceState.containsKey(EXTRA_MOVIE)) {
            Intent intent = getIntent();
            if (intent == null || !intent.hasExtra(EXTRA_MOVIE)) {
                Toast.makeText(this, R.string.error_toast, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            Movie movie = intent.getParcelableExtra(EXTRA_MOVIE);
            savedInstanceState = new Bundle();
            savedInstanceState.putParcelable(EXTRA_MOVIE, movie);
        }

        setupRecyclerViewVideos();
        setupRecyclerViewReviews();

        mPresenter = new DetailsPresenter(this, this);
        mPresenter.restoreState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPresenter.saveState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
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

    private void setupRecyclerViewVideos() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mVideosRv.setLayoutManager(manager);
        mVideosRv.setHasFixedSize(true);

        mVideosAdapter = new VideosAdapter(this, mVideoOnItemClickListener);
        mVideosRv.setAdapter(mVideosAdapter);
    }

    private void setupRecyclerViewReviews() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mReviewsRv.setLayoutManager(manager);
        mReviewsRv.setHasFixedSize(true);

        mReviewsAdapter = new ReviewsAdapter(this, mReviewOnItemClickListener);
        mReviewsRv.setAdapter(mReviewsAdapter);
    }

    private final VideosAdapter.OnItemClickListener<Video> mVideoOnItemClickListener =
            new BaseRecyclerAdapter.OnItemClickListener<Video>() {
                @Override
                public void onItemClick(View itemView, int pos, Video item) {
                    mPresenter.onVideoClicked(item);
                }
            };

    private final ReviewsAdapter.OnItemClickListener<Review> mReviewOnItemClickListener =
            new BaseRecyclerAdapter.OnItemClickListener<Review>() {
                @Override
                public void onItemClick(View itemView, int pos, Review item) {
                    mPresenter.onReviewClicked(item);
                }
            };

    @OnClick(R.id.fab)
    public void fabClicked() {
        mPresenter.onFavClicked();
    }

    @Override
    public void showContent(Movie movie) {
        /*if (Utils.isPortrait(this))
            mTitleView.setTitle(movie.title);
        else
            ((TextView) findViewById(R.id.tv_title)).setText(movie.title);*/
        mTitleTv.setText(movie.title);

        if (movie.release_date != null)
            mDateTv.setText(DateFormat.getDateInstance().format(movie.release_date));

        Log.i("DetailsView", "vote -> " + movie.vote_average);
        mRatingBar.setRating((float) (movie.vote_average / 2.0));

        if (!movie.hasOverview())
            mSummaryTv.setText(R.string.summary_not_available);
        else
            mSummaryTv.setText(movie.overview);

        if (!movie.hasImage()) {
            mIvPoster.setVisibility(View.GONE);
            return;
        }

        revealFab();

        String url = ApiUtils.getImageUrl(movie.poster_path, ApiUtils.IMAGE_SIZE_LARGE);
        Picasso.with(this)
                .load(url)
                .into(mIvPoster, PicassoPalette.with(url, mIvPoster)
                        .use(PicassoPalette.Profile.MUTED_DARK)
                            .intoCallBack(new PicassoPalette.CallBack() {
                                @Override
                                public void onPaletteLoaded(Palette palette) {
                                    if (palette != null) {
                                        int textColor = palette.getLightVibrantColor(Color.WHITE);
                                        mTitleTv.setTextColor(textColor);
                                        int backColor = palette.getDarkMutedColor(mPrimaryColor);
                                        if (Utils.isPortrait(DetailsView.this))
                                            mTitleView.setContentScrimColor(backColor);
                                        else
                                            mAppBar.setBackgroundColor(backColor);

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                                            Utils.setStatusBarColor(DetailsView.this, backColor);
                                    }
                                }
                            }));
    }

    @Override
    public void toggleFav(boolean isFav) {
        fab.setImageResource(isFav ? R.drawable.ic_favorite : R.drawable.ic_not_favorite);
    }

    private void revealFab() {
        fab.setScaleX(0.0f);
        fab.setScaleY(0.0f);
        fab.setVisibility(View.VISIBLE);
        fab.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(Utils.ANIMATION_DURATION)
                .start();
    }

    @Override
    public void showLoadingVideos() {
        mVideosRv.setVisibility(View.GONE);
        mVideosErrorTv.setVisibility(View.GONE);
        mVideosPb.setVisibility(View.VISIBLE);
    }

    @Override
    public void showVideos(List<Video> videos) {
        if (videos == null || videos.isEmpty()) {
            mVideosErrorTv.setVisibility(View.VISIBLE);
            mVideosPb.setVisibility(View.GONE);
        } else {
            mVideosAdapter.set(videos);
            mVideosRv.setVisibility(View.VISIBLE);
            mVideosPb.setVisibility(View.GONE);
        }
    }

    @Override
    public void showLoadingReviews() {
        mReviewsRv.setVisibility(View.GONE);
        mReviewsErrorTv.setVisibility(View.GONE);
        mReviewsPb.setVisibility(View.VISIBLE);
    }

    @Override
    public void showReviews(List<Review> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            mReviewsErrorTv.setVisibility(View.VISIBLE);
            mReviewsPb.setVisibility(View.GONE);
        } else {
            mReviewsAdapter.set(reviews);
            mReviewsRv.setVisibility(View.VISIBLE);
            mReviewsPb.setVisibility(View.GONE);
        }
    }
}
