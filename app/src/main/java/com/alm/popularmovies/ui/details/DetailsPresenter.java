package com.alm.popularmovies.ui.details;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.alm.popularmovies.PopularMoviesApp;
import com.alm.popularmovies.R;
import com.alm.popularmovies.api.TheMovieDbService;
import com.alm.popularmovies.api.model.Movie;
import com.alm.popularmovies.api.model.Review;
import com.alm.popularmovies.api.model.Reviews;
import com.alm.popularmovies.api.model.Video;
import com.alm.popularmovies.api.model.Videos;
import com.alm.popularmovies.utils.ApiUtils;
import com.alm.popularmovies.utils.DbUtils;
import com.alm.popularmovies.utils.Utils;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by A. Labay on 26/02/17.
 * As part of the project PopularMovies.
 */

public class DetailsPresenter implements IDetailsMVP.Presenter {

    private Context mContext;
    private IDetailsMVP.View mView;
    private Movie mMovie = null;
    private final ArrayList<Video> mVideos = new ArrayList<>();
    private final ArrayList<Review> mReviews = new ArrayList<>();
    private boolean mIsFav;

    private Subscription mVideosSubscription, mReviewsSubscription;

    public DetailsPresenter(Context context,
                            IDetailsMVP.View view) {
        mContext = context;
        mView = view;
    }

    @Override
    public void restoreState(Bundle state) {
        mMovie = state.getParcelable(DetailsView.EXTRA_MOVIE);
        if (mMovie == null) {
            ((DetailsView) mView).finish();
            return;
        }

        mView.showContent(mMovie);
        loadFavInfo();

        ArrayList<Video> savedVds = state.getParcelableArrayList("my_videos");
        loadVideos(savedVds);

        ArrayList<Review> savedRvs = state.getParcelableArrayList("my_reviews");
        loadReviews(savedRvs);
    }

    private void loadFavInfo() {
        mIsFav = Utils.isMovieFav(mContext.getContentResolver(), mMovie.id);

        mView.toggleFav(mIsFav);
    }

    private void loadVideos(ArrayList<Video> videos) {
        TheMovieDbService service = ((PopularMoviesApp) mContext.getApplicationContext())
                .getService();

        if (videos == null || videos.isEmpty()) {
            mVideosSubscription = service.getVideos(mMovie.id, ApiUtils.API_KEY)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(mVideosSubscriber);
        } else {
            mVideosSubscription = Observable.just(videos)
                    .map(new Func1<ArrayList<Video>, Videos>() {
                        @Override
                        public Videos call(ArrayList<Video> videos) {
                            Videos vds = new Videos();
                            vds.results = new ArrayList<>();
                            vds.results.addAll(videos);
                            return vds;
                        }
                    })
                    .subscribe(mVideosSubscriber);
        }
    }

    private void loadReviews(ArrayList<Review> reviews) {
        TheMovieDbService service = ((PopularMoviesApp) mContext.getApplicationContext())
                .getService();

        if (reviews == null || reviews.isEmpty()) {
            mReviewsSubscription = service.getReviews(mMovie.id, ApiUtils.API_KEY, 1)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(mReviewsSubscriber);
        } else {
            mReviewsSubscription = Observable.just(reviews)
                    .map(new Func1<ArrayList<Review>, Reviews>() {
                        @Override
                        public Reviews call(ArrayList<Review> reviews) {
                            Reviews rvs = new Reviews();
                            rvs.results = new ArrayList<>();
                            rvs.results.addAll(reviews);
                            return rvs;
                        }
                    }).subscribe(mReviewsSubscriber);
        }
    }

    @Override
    public void saveState(Bundle state) {
        state.putParcelable(DetailsView.EXTRA_MOVIE, mMovie);
        if (!mVideos.isEmpty())
            state.putParcelableArrayList("my_videos", mVideos);
        if (!mReviews.isEmpty())
            state.putParcelableArrayList("my_reviews", mReviews);
    }

    @Override
    public void onDestroy() {
        if (mVideosSubscription != null)
            mVideosSubscription.unsubscribe();
        if (mReviewsSubscription != null)
            mReviewsSubscription.unsubscribe();
    }

    @Override
    public void onVideoClicked(Video video) {
        Utils.openYouTubeVideo(mContext, video.key);
    }

    @Override
    public void onReviewClicked(Review review) {
        DialogFragment newFragment = ReviewDialogFragment.create(review);
        newFragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onFavClicked() {
        boolean mNewFavState = mIsFav;
        if (mIsFav) {
            if (DbUtils.removeMovie(mContext.getContentResolver(), mMovie.id))
                mNewFavState = false;
        } else {
            Uri uri = DbUtils.insertMovie(mContext.getContentResolver(), mMovie);

            if (uri != null) {
                mNewFavState = true;
            }
        }

        if (mNewFavState != mIsFav) {
            mIsFav = mNewFavState;
            mView.toggleFav(mIsFav);
        } else {
            Toast.makeText(mContext,
                    mContext.getString(R.string.failed_remove_fav),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean isFav() {
        return mIsFav;
    }

    private final Subscriber<Videos> mVideosSubscriber
            = new Subscriber<Videos>() {

        @Override
        public void onStart() {
            super.onStart();
            mView.showLoadingVideos();
        }

        @Override
        public void onCompleted() {
            mVideosSubscription = null;
        }

        @Override
        public void onError(Throwable e) {
            mView.showVideos(null);
            mVideosSubscription = null;
        }

        @Override
        public void onNext(Videos videos) {
            if (videos != null && videos.results != null) {
                mVideos.addAll(videos.results);
                mView.showVideos(mVideos);
            } else {
                mView.showVideos(null);
            }
        }
    };

    private final Subscriber<Reviews> mReviewsSubscriber
            = new Subscriber<Reviews>() {

        @Override
        public void onStart() {
            super.onStart();
            mView.showLoadingReviews();
        }

        @Override
        public void onCompleted() {
            mReviewsSubscription = null;
        }

        @Override
        public void onError(Throwable e) {
            mView.showReviews(null);
            mReviewsSubscription = null;
        }

        @Override
        public void onNext(Reviews reviews) {
            if (reviews != null && reviews.results != null) {
                mReviews.addAll(reviews.results);
                mView.showReviews(mReviews);
            } else {
                mView.showReviews(null);
            }
        }
    };
}
