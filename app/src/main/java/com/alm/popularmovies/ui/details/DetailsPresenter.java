package com.alm.popularmovies.ui.details;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.alm.popularmovies.PopularMoviesApp;
import com.alm.popularmovies.R;
import com.alm.popularmovies.api.TheMovieDbService;
import com.alm.popularmovies.api.model.Credits;
import com.alm.popularmovies.api.model.Movie;
import com.alm.popularmovies.api.model.Review;
import com.alm.popularmovies.api.model.Video;
import com.alm.popularmovies.utils.ApiUtils;
import com.alm.popularmovies.utils.DbUtils;
import com.alm.popularmovies.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
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
    private Credits.Crew mDirector = null;
    private boolean mIsFav;

    private Subscription mVideosSubscription, mReviewsSubscription, mCreditsSubscription;

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

        Credits.Crew savedDirector = state.getParcelable("my_director");
        loadDirector(savedDirector);
    }

    private void loadFavInfo() {
        mIsFav = DbUtils.isMovieFav(mContext.getContentResolver(), mMovie.id);

        mView.toggleFav(mIsFav);
    }

    private void loadVideos(ArrayList<Video> videos) {
        if (videos == null || videos.isEmpty()) {
            TheMovieDbService service = ((PopularMoviesApp) mContext.getApplicationContext())
                    .getService();

            mVideosSubscription = service.getVideos(mMovie.id, ApiUtils.API_KEY)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(vds -> vds != null ? vds.results : null)
                    .subscribe(mVideosSubscriber);
        } else {
            mVideosSubscription = Observable.just(videos)
                    .subscribe(mVideosSubscriber);
        }
    }

    private void loadReviews(ArrayList<Review> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            TheMovieDbService service = ((PopularMoviesApp) mContext.getApplicationContext())
                    .getService();

            mReviewsSubscription = service.getReviews(mMovie.id, ApiUtils.API_KEY, 1)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(rvs -> rvs != null ? rvs.results : null)
                    .subscribe(mReviewsSubscriber);
        } else {
            mReviewsSubscription = Observable.just(reviews)
                    .subscribe(mReviewsSubscriber);
        }
    }

    private void loadDirector(Credits.Crew director) {
        if (director == null || TextUtils.isEmpty(director.name)) {
            TheMovieDbService service = ((PopularMoviesApp) mContext.getApplicationContext())
                    .getService();

            mCreditsSubscription = service.getCredits(mMovie.id, ApiUtils.API_KEY)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(rvs -> rvs != null ? rvs.getDirector() : null)
                    .subscribe(mCreditsSubscriber);
        } else {
            mCreditsSubscription = Observable.just(director)
                    .subscribe(mCreditsSubscriber);
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
        if (mCreditsSubscription != null)
            mCreditsSubscription.unsubscribe();
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
                    mIsFav ? R.string.failed_remove_fav : R.string.failed_insert_fav,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean isFav() {
        return mIsFav;
    }

    private final Subscriber<List<Video>> mVideosSubscriber
            = new Subscriber<List<Video>>() {

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
        public void onNext(List<Video> videos) {
            if (videos != null && !videos.isEmpty()) {
                mVideos.addAll(videos);
                mView.showVideos(mVideos);
            } else {
                mView.showVideos(null);
            }
        }
    };

    private final Subscriber<List<Review>> mReviewsSubscriber
            = new Subscriber<List<Review>>() {

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
        public void onNext(List<Review> reviews) {
            if (reviews != null && !reviews.isEmpty()) {
                mReviews.addAll(reviews);
                mView.showReviews(mReviews);
            } else {
                mView.showReviews(null);
            }
        }
    };

    private final Subscriber<Credits.Crew> mCreditsSubscriber
            = new Subscriber<Credits.Crew>() {

        @Override
        public void onStart() {
            super.onStart();
            mView.showLoadingReviews();
        }

        @Override
        public void onCompleted() {
            mCreditsSubscription = null;
        }

        @Override
        public void onError(Throwable e) {
            mView.showDirector(null);
            mCreditsSubscription = null;
        }

        @Override
        public void onNext(Credits.Crew crew) {
            mDirector = crew;
            mView.showDirector(crew != null ? crew.name : null);
        }
    };
}
