package com.alm.popularmovies.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by A. Labay on 19/02/17.
 * As part of the project PopularMovies.
 */

public class MovieContract {

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + MovieProvider.AUTHORITY);

    private MovieContract () { }

    public static class MovieEntry implements BaseColumns {
        public static final String PATH = "movies";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon().appendPath(PATH).build();

        public static final String TABLE_NAME = "favorites";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_POSTER_PATH = "poster";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_RELEASE_DATE = "COLUMN_RELEASE_DATE";
        public static final String COLUMN_OVERVIEW = "overview";

        public static final String[] PROJECTION = new String[] {
                _ID,
                COLUMN_MOVIE_ID,
                COLUMN_TITLE,
                COLUMN_ORIGINAL_TITLE,
                COLUMN_POSTER_PATH,
                COLUMN_VOTE_AVERAGE,
                COLUMN_RELEASE_DATE,
                COLUMN_OVERVIEW
        };
    }
}
