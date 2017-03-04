package com.alm.popularmovies.adapters;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alm.popularmovies.R;
import com.alm.popularmovies.api.model.Review;

/**
 * Created by A. Labay on 26/02/17.
 * As part of the project PopularMovies.
 */

public class ReviewsAdapter extends BaseRecyclerAdapter<Review, ReviewsAdapter.ReviewHolder> {

    public ReviewsAdapter(Context context, OnItemClickListener<Review> itemClickListener) {
        super(context, itemClickListener);
    }

    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ReviewHolder(mInflater.inflate(R.layout.list_item_review, parent, false));
    }

    public class ReviewHolder extends BaseRecyclerAdapter.BaseHolder<Review>
            implements View.OnClickListener {

        private TextView mAuthorTv, mContentTv;

        public ReviewHolder(View itemView) {
            super(itemView);

            mAuthorTv = (TextView) itemView.findViewById(R.id.list_item_tv_author);
            mContentTv = (TextView) itemView.findViewById(R.id.list_item_tv_content);

            itemView.setOnClickListener(this);
        }

        @Override
        public void bind(Review item) {
            mAuthorTv.setText(item.getAuthor());
            if (Build.VERSION.SDK_INT >= 24) {
                mContentTv.setText(Html.fromHtml(item.getContent(), Html.FROM_HTML_MODE_COMPACT));
            } else {
                //noinspection deprecation
                mContentTv.setText(Html.fromHtml(item.getContent()));
            }
        }

        @Override
        public void onClick(View view) {
            int index = getAdapterPosition();
            mItemClickListener.onItemClick(view, index, get(index));
        }
    }
}
