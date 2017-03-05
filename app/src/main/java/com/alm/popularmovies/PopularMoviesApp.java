package com.alm.popularmovies;

import android.app.Application;

import com.alm.popularmovies.api.TheMovieDbService;
import com.alm.popularmovies.api.TheMovieDbServiceImpl;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by A. Labay on 27/02/17.
 * As part of the project PopularMovies.
 */
@ReportsCrashes(mode = ReportingInteractionMode.DIALOG,
        mailTo = "alm.programming.and@gmail.com",
        logcatArguments = {"-t", "5000", "-v", "time"},
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = android.R.drawable.ic_dialog_info,
        resDialogTitle = R.string.crash_dialog_title,
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt,
        resDialogOkToast = R.string.crash_dialog_ok_toast)
public class PopularMoviesApp extends Application {

    private TheMovieDbService mService;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            // Initialise ACRA
            ACRA.init(this);
        }
    }

    public synchronized TheMovieDbService getService() {
        if (mService == null) {
            mService = TheMovieDbServiceImpl.create();
        }

        return mService;
    }
}
