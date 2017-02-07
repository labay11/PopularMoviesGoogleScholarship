package com.alm.popularmovies;

import android.app.Application;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by A. Labay on 02/02/17.
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
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            // Initialise ACRA
            ACRA.init(this);
        }
    }
}
