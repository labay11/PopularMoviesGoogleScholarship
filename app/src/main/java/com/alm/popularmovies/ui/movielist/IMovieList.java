package com.alm.popularmovies.ui.movielist;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.alm.popularmovies.api.model.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by A. Labay on 27/02/17.
 * As part of the project PopularMovies.
 */

public interface IMovieList {

    interface Presenter {

        boolean restoreState(@Nullable Bundle state);

        void saveState(Bundle state);

        void load(int page);

        void onDestroy();
    }

    interface View {

        void addMovies(List<Movie> movies);

        void showError();

        void showLoading(int page);

        void hideLoading();

        ArrayList<Movie> getMovies();

        boolean hasMovies();

        void setPage(int page);
    }
}
