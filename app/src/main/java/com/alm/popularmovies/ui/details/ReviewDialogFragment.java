package com.alm.popularmovies.ui.details;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.alm.popularmovies.api.model.Review;

/**
 * Created by A. Labay on 04/03/17.
 * As part of the project PopularMovies.
 */

public class ReviewDialogFragment extends DialogFragment {

    private Review mReview;

    public static ReviewDialogFragment create(Review review) {
        ReviewDialogFragment frag = new ReviewDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("review", review);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args == null || !args.containsKey("review")) {
            dismiss();
            return;
        }

        mReview = args.getParcelable("review");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(mReview.getAuthor())
                .setMessage(mReview.getContent())
                .setNeutralButton("View original", (dialogInterface, i) -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mReview.getUrl()));
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null)
                        startActivity(intent);
                }).setPositiveButton(android.R.string.ok, null)
                .create();
    }
}
