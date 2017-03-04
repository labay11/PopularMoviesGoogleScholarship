package com.alm.popularmovies.ui;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by A. Labay on 01/02/17.
 * As part of the project PopularMovies.
 */
public class DetailsActivity extends AppCompatActivity {

    /*public static final String EXTRA_MOVIE = "extra_movie";

    private int movieId = -1;

    private Movie mMovie = null;

    private boolean mIsFav = false;

    private DetailsAsyncTask mTask;

    @BindView(R.id.iv_backdrop)
    public ImageView mIvPoster;
    public CollapsingToolbarLayout mTitleView;
    @BindView(R.id.tv_date)
    public TextView mDateTv;
    @BindView(R.id.rating_bar)
    public RatingBar mRatingBar;
    @BindView(R.id.tv_summary)
    public TextView mSummaryTv;
    @BindView(R.id.progress_bar)
    public ProgressBar mLoadingView;
    @BindView(R.id.content)
    public View mContent;
    @BindView(R.id.container_error)
    public View mErrorView;
    @BindView(R.id.fab)
    public FloatingActionButton fab;

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

        if (savedInstanceState != null && savedInstanceState.containsKey("saved_movie_id")) {
            movieId = savedInstanceState.getInt("saved_movie_id", -1);
            mMovie = savedInstanceState.getParcelable("saved_movie");
        }

        if (movieId == -1) {
            movieId = getIntent().getIntExtra(EXTRA_MOVIE_ID, -1);
        }

        if (mMovie != null) {
            onFinishLoading(mMovie);
        } else if (movieId != -1) {
            loadDetails();
        } else {
            Toast.makeText(this, R.string.error_toast, Toast.LENGTH_SHORT).show();
            finish();
        }

        setupFab();
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

    private void setupFab() {
        mIsFav = Utils.isMovieFav(getContentResolver(), movieId);

        fab.setImageResource(mIsFav ? R.drawable.ic_favorite : R.drawable.ic_not_favorite);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFav();
            }
        });
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

    private void toggleFav() {
        if (mIsFav) {
            int count = getContentResolver()
                    .delete(MovieContract.MovieEntry.CONTENT_URI,
                            MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?",
                            new String[]{movieId+""});

            if (count > 0) {
                mIsFav = false;
                fab.setImageResource(R.drawable.ic_not_favorite);
            } else {
                Toast.makeText(this,
                        getString(R.string.failed_remove_fav),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            ContentValues values = new ContentValues(2);
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, mMovie.getMovieId());
            values.put(MovieContract.MovieEntry.COLUMN_TITLE, mMovie.getTitle());
            values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, mMovie.getPoster_path());

            Uri uri = getContentResolver().insert(
                    MovieContract.MovieEntry.CONTENT_URI, values);

            if (uri != null) {
                mIsFav = true;
                fab.setImageResource(R.drawable.ic_favorite);
            } else {
                Toast.makeText(this,
                        getString(R.string.failed_remove_fav),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Executed when the try again button is clicked.
     * @param v the button
     **/
    /*@OnClick(R.id.btn_try_again)
    public void onTryAgainClicked(View v) {
        loadDetails();
    }

    private void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
        mContent.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);
    }

    private void showContent() {
        mErrorView.setVisibility(View.GONE);
        Utils.crossfade(mContent, mLoadingView);

        fab.setScaleX(0.0f);
        fab.setScaleY(0.0f);
        fab.setVisibility(View.VISIBLE);
        fab.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(Utils.ANIMATION_DURATION).start();
    }

    private void showError() {
        mErrorView.setVisibility(View.VISIBLE);
        mContent.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);
    }

    /**
     * Called from {@link DetailsAsyncTask#onPostExecute(Movie)} after the
     * movies have been fetched.
     *
     * @param details details of the movie.
     **/
    /*public void onFinishLoading(Movie details) {
        if (details == null) {
            showError();
            return;
        }

        mMovie = details;
        populateViews();
        showContent();
    }

    private void populateViews() {
        if (Utils.isPortrait(this))
            mTitleView.setTitle(mMovie.getTitle());
        else
            ((TextView) findViewById(R.id.tv_title)).setText(mMovie.getTitle());

        mDateTv.setText(DateFormat.getDateInstance().format(mMovie.getReleaseDate()));
        mRatingBar.setRating(mMovie.getVoteAverage() / 2f);

        if (mMovie.hasSynopsis())
            mSummaryTv.setText(R.string.summary_not_available);
        else
            mSummaryTv.setText(mMovie.getSynopsis());

        if (!mMovie.hasImage()) {
            mIvPoster.setVisibility(View.GONE);
            return;
        }

        String url = ApiUtils.getImageUrl(mMovie.getPoster_path(), ApiUtils.IMAGE_SIZE_LARGE);
        Picasso.with(this)
                .load(url)
                .into(mIvPoster,
                        PicassoPalette.with(url, mIvPoster)
                                .intoCallBack(new PicassoPalette.CallBack() {
                                    @Override
                                    public void onPaletteLoaded(Palette palette) {
                                        if (palette != null && Utils.isPortrait(DetailsActivity.this)) {
                                            int textColor = palette.getLightVibrantColor(Color.WHITE);
                                            mTitleView.setExpandedTitleColor(textColor);
                                            mTitleView.setCollapsedTitleTextColor(Color.WHITE);
                                        }
                                    }
                                }));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMovie != null)
            outState.putParcelable("saved_movie", mMovie);
        outState.putInt("saved_movie_id", movieId);
    }

    private static class DetailsAsyncTask extends AsyncTask<Integer, Void, Movie> {

        private WeakReference<DetailsActivity> mReference;

        public DetailsAsyncTask(DetailsActivity activity) {
            mReference = new WeakReference<>(activity);
        }

        @Override
        protected Movie doInBackground(Integer... integers) {
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
        protected void onPostExecute(Movie movies) {
            super.onPostExecute(movies);
            if (mReference != null) {
                if (mReference.get() != null) {
                    mReference.get().onFinishLoading(movies);
                }
            }
        }
    }*/
}
