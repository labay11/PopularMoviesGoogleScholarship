package com.alm.popularmovies.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.alm.popularmovies.R;
import com.alm.popularmovies.api.model.Movie;
import com.alm.popularmovies.provider.MovieContract;
import com.alm.popularmovies.ui.details.DetailsView;

/**
 * Created by A. Labay on 01/02/17.
 * As part of the project PopularMovies.
 */

public class Utils {

    public static final String YOUTUBE_URL = "https://www.youtube.com/watch?v=";

    public static final int ANIMATION_DURATION = 750;

    public static boolean isPortrait(Context context) {
        return context.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT;
    }

    /**
     * Calculates the desired number of columns that should show up in the RecyclerView.
     * Based on the experience I use the width of the screen (in dp) and divided by
     * the column width.
     *
     * @return number of columns
     */
    public static int getNumberOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels / ((int) context.getResources().getDimension(R.dimen.list_item_width));
    }

    /**
     * Helper method to animate two views, one which is going from invisible -> visible and
     * another going from visible -> invisible at the same time.
     * @param in view appearing
     * @param out view disappearing
     */
    public static void crossfade(final View in, final View out) {
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        in.setAlpha(0f);
        in.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        in.animate()
                .alpha(1f)
                .setDuration(ANIMATION_DURATION)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        out.animate()
                .alpha(0f)
                .setDuration(ANIMATION_DURATION)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        out.setVisibility(View.GONE);
                    }
                });
    }

    public static void navigateToDetails(Context context, Movie movie) {
        Intent intent = new Intent(context, DetailsView.class);
        intent.putExtra(DetailsView.EXTRA_MOVIE, movie);
        context.startActivity(intent);
    }

    public static boolean isMovieFav(ContentResolver cr, int movieId) {
        Cursor cursor = cr.query(MovieContract.MovieEntry.CONTENT_URI,
                null,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?",
                new String[]{"" + movieId},
                null);

        if (cursor == null)
            return false;

        if (!cursor.moveToFirst()) {
            cursor.close();
            return false;
        }

        boolean isFav = cursor.getCount() > 0;

        cursor.close();

        return isFav;
    }

    public static void openYouTubeVideo(Context context, String key) {
        Uri uri = Uri.parse(YOUTUBE_URL + key);

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(context.getPackageManager()) != null)
            context.startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarColor(Activity context, int color) {
        Window window = context.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(color);
    }
}
