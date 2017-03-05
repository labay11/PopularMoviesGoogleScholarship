package com.alm.popularmovies.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by A. Labay on 19/02/17.
 * As part of the project PopularMovies.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movies.db";

    public static final int DATABASE_VERSION = 3;

    private static final String CREATE_TABLE_FAVORITES = "CREATE TABLE " +
            MovieContract.MovieEntry.TABLE_NAME + "(" +
            MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL UNIQUE," +
            MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL," +
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL," +
            MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT," +
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " REAL DEFAULT 0," +
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " INTEGER," +
            MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT" +
            ");";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_FAVORITES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldv, int newv) {
        switch (newv) {
            case 3: // added original title column
            case 2: // added columns vote_average, release_date & overview -> recreate table
                sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
                onCreate(sqLiteDatabase);
                break;

            default:
                sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
                onCreate(sqLiteDatabase);
        }

    }
}
