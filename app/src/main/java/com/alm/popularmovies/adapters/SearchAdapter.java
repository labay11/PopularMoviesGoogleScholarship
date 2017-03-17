package com.alm.popularmovies.adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alm.popularmovies.R;
import com.alm.popularmovies.api.model.Movie;
import com.alm.popularmovies.utils.ApiUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by A. Labay on 17/03/17.
 * As part of the project PopularMovies.
 */
public class SearchAdapter extends BaseRecyclerAdapter<Movie, SearchAdapter.SearchHolder> {

    private final int PLACEHOLDER_COLORS[];

    public SearchAdapter(Context context, OnItemClickListener<Movie> itemClickListener) {
        super(context, itemClickListener);

        PLACEHOLDER_COLORS = context.getResources().getIntArray(R.array.placeholder_list_colors);
    }

    @Override
    public SearchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SearchHolder(mInflater.inflate(R.layout.list_item_search, parent, false));
    }

    public class SearchHolder extends BaseRecyclerAdapter.BaseHolder<Movie>
            implements View.OnClickListener {

        private TextView mTitle;

        private ImageView mImageView;

        public SearchHolder(View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.list_item_tv_title);
            mImageView = (ImageView) itemView.findViewById(R.id.list_item_image);

            itemView.setOnClickListener(this);
        }

        @Override
        public void bind(Movie item) {
            mTitle.setText(item.title);

            String url = ApiUtils.getImageUrl(item.poster_path, ApiUtils.IMAGE_SIZE_NORMAL);

            Picasso.with(mImageView.getContext())
                    .load(url)
                    .placeholder(new ColorDrawable(PLACEHOLDER_COLORS[getAdapterPosition() % 5]))
                    .into(mImageView);
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            mItemClickListener.onItemClick(view, pos, get(pos));
        }
    }
}
