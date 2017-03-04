package com.alm.popularmovies.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alm.popularmovies.R;
import com.alm.popularmovies.api.model.Video;

/**
 * Created by A. Labay on 26/02/17.
 * As part of the project PopularMovies.
 */

public class VideosAdapter extends BaseRecyclerAdapter<Video, VideosAdapter.VideoHolder> {

    public VideosAdapter(Context context,
                         OnItemClickListener<Video> itemClickListener) {
        super(context, itemClickListener);
    }

    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VideoHolder(mInflater.inflate(R.layout.list_item_video, parent, false));
    }

    public class VideoHolder extends BaseRecyclerAdapter.BaseHolder<Video>
            implements View.OnClickListener {

        private TextView mTitleTv;

        public VideoHolder(View itemView) {
            super(itemView);
            mTitleTv = (TextView) itemView.findViewById(R.id.list_item_tv_title);

            itemView.setOnClickListener(this);
        }

        @Override
        public void bind(Video item) {
            mTitleTv.setText(item.getName());
        }

        @Override
        public void onClick(View view) {
            int index = getAdapterPosition();
            mItemClickListener.onItemClick(view, index, get(index));
        }
    }
}
