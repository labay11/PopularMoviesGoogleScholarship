package com.alm.popularmovies.loaders;

import android.content.Context;
import android.database.Cursor;

import com.alm.popularmovies.api.model.Movie;
import com.alm.popularmovies.provider.MovieContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by A. Labay on 25/02/17.
 * As part of the project PopularMovies.
 */

public class FavoritesLoader extends TaskLoader<List<Movie>> {

    public FavoritesLoader(Context context) {
        super(context);
    }

    @Override
    public List<Movie> loadInBackground() {

        Cursor cursor = getContext().getContentResolver()
                .query(MovieContract.MovieEntry.CONTENT_URI,
                        MovieContract.MovieEntry.PROJECTION,
                        null,
                        null,
                        MovieContract.MovieEntry.COLUMN_TITLE);

        if (cursor == null)
            return null;

        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }

        List<Movie> movies = new ArrayList<>(cursor.getCount());
        do {
            movies.add(new Movie(
                    cursor.getLong(0),
                    cursor.getInt(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getDouble(5),
                    cursor.getLong(6),
                    cursor.getString(7)
            ));
        } while (cursor.moveToNext());

        cursor.close();

        return movies;
    }
}
