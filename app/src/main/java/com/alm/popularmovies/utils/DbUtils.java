package com.alm.popularmovies.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.alm.popularmovies.api.model.Movie;
import com.alm.popularmovies.provider.MovieContract;

/**
 * Created by A. Labay on 04/03/17.
 * As part of the project PopularMovies.
 */

public class DbUtils {

    public static Uri insertMovie(ContentResolver cr, @NonNull Movie movie) {
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.id);
        values.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.title);
        values.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movie.original_title);
        values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.poster_path);
        values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.vote_average);
        values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.release_date.getTime());
        values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.overview);

        return cr.insert(MovieContract.MovieEntry.CONTENT_URI, values);
    }

    public static boolean removeMovie(ContentResolver cr, int movieId) {
        return cr.delete(MovieContract.MovieEntry.CONTENT_URI,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?",
                        new String[]{String.valueOf(movieId)}) > 0;
    }
}
