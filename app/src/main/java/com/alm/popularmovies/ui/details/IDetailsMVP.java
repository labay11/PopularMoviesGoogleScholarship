package com.alm.popularmovies.ui.details;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.alm.popularmovies.api.model.Movie;
import com.alm.popularmovies.api.model.Review;
import com.alm.popularmovies.api.model.Video;

import java.util.List;

/**
 * Created by A. Labay on 26/02/17.
 * As part of the project PopularMovies.
 */

public interface IDetailsMVP {

    interface Presenter {

        void restoreState(@Nullable Bundle state);

        void saveState(Bundle state);

        void onDestroy();

        void onVideoClicked(Video video);

        void onReviewClicked(Review review);

        void onFavClicked();

        boolean isFav();

    }

    interface View {
        void showLoadingVideos();

        void showLoadingReviews();

        void showContent(Movie movie);

        void showVideos(List<Video> videos);

        void showReviews(List<Review> reviews);

        void toggleFav(boolean isFav);
    }
}
