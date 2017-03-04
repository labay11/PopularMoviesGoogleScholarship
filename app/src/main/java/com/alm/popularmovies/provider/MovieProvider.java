package com.alm.popularmovies.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alm.popularmovies.BuildConfig;

/**
 * Created by A. Labay on 19/02/17.
 * As part of the project PopularMovies.
 */

public class MovieProvider extends ContentProvider {

    public static final String TAG = MovieProvider.class.getSimpleName();

    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".movies";

    private static UriMatcher sMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int FAVORITE = 10;
    private static final int FAVORITE_ID = 11;

    static {
        sMatcher.addURI(AUTHORITY, MovieContract.MovieEntry.PATH, FAVORITE);
        sMatcher.addURI(AUTHORITY, MovieContract.MovieEntry.PATH + "/#", FAVORITE_ID);
    }

    private MovieDbHelper mDb = null;


    public MovieProvider() { }

    @Override
    public boolean onCreate() {
        mDb = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String where,
                        @Nullable String[] whereValues,
                        @Nullable String orderBy) {

        final SQLiteDatabase db = mDb.getReadableDatabase();

        Cursor res;
        switch (sMatcher.match(uri)) {
            case FAVORITE:
                res = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        where,
                        whereValues,
                        null,
                        null,
                        orderBy);
                break;
            case FAVORITE_ID:
                long id = ContentUris.parseId(uri);
                if (id < 0)
                    throw new UnsupportedOperationException("Unknown id for uri - " + uri);

                res = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry._ID + "=" + id,
                        null,
                        null,
                        null,
                        null);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri - " + uri);
        }

        res.setNotificationUri(getContext().getContentResolver(), uri);

        return res;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sMatcher.match(uri)) {
            case FAVORITE:
                return "vnd.android.cursor.dir/favorite";
            case FAVORITE_ID:
                return "vnd.android.cursor.item/favorite";

            default:
                throw new UnsupportedOperationException("Unknown uri - " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri,
                      @Nullable ContentValues contentValues) {
        Uri returnUri;
        switch (sMatcher.match(uri)) {
            case FAVORITE:
                long id = mDb.getWritableDatabase()
                        .insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);
                if (id < 0)
                    throw new SQLException("Failed to insert row into " + uri);
                returnUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, id);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri - " + uri);
        }

        getContext().getContentResolver().notifyChange(returnUri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri,
                      @Nullable String where,
                      @Nullable String[] whereValues) {

        int res = 0;
        switch (sMatcher.match(uri)) {
            case FAVORITE:
                res = mDb.getWritableDatabase()
                        .delete(MovieContract.MovieEntry.TABLE_NAME, where, whereValues);
                break;

            case FAVORITE_ID:
                long id = ContentUris.parseId(uri);
                if (id < 0)
                    throw new UnsupportedOperationException("Unknown id for uri - " + uri);

                res = mDb.getWritableDatabase()
                            .delete(MovieContract.MovieEntry.TABLE_NAME,
                                    MovieContract.MovieEntry._ID +"="+id, null);
                break;
        }

        if (res > 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return res;
    }

    @Override
    public int update(@NonNull Uri uri,
                      @Nullable ContentValues contentValues,
                      @Nullable String where,
                      @Nullable String[] whereValues) {

        String appendWhere = null;
        String table;
        switch (sMatcher.match(uri)) {
            case FAVORITE_ID:
                long id = ContentUris.parseId(uri);
                if (id < 0)
                    throw new UnsupportedOperationException("Unknown id for uri - " + uri);

                appendWhere = MovieContract.MovieEntry._ID + "=" + id;

            case FAVORITE:
                table = MovieContract.MovieEntry.TABLE_NAME;
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri - " + uri);
        }

        if (TextUtils.isEmpty(where))
            where = appendWhere;
        else
            where = appendWhere + " AND (" + where + ")";

        int count = mDb.getWritableDatabase()
                .update(table, contentValues, where, whereValues);

        if (count > 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }
}
