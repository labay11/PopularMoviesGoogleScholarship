package com.alm.popularmovies.ui.search;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.alm.popularmovies.api.model.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by A. Labay on 12/03/17.
 * As part of the project PopularMovies.
 */
public interface ISearch {

    interface View {

        void setQuery(String q);

        void showResults(List<Movie> data);

        void showError();

        void showLoading();

        ArrayList<Movie> getResults();
    }

    interface Presenter {

        void query(@NonNull String q);

        void onCreate(Bundle savedInstanceState);

        void saveState(Bundle outState);

        void onDestroy();

    }

}
