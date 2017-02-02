package com.alm.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alm.popularmovies.model.Movie;
import com.alm.popularmovies.utils.ApiUtils;
import com.bumptech.glide.Glide;
import com.github.florent37.glidepalette.GlidePalette;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by A. Labay on 24/01/17.
 * As part of the project PopularMovies.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieHolder> {

    private final ArrayList<Movie> mItems = new ArrayList<>();

    private Context mContext;

    private OnRecyclerItemClickListener mItemClickListener;

    public MoviesAdapter(Context context, OnRecyclerItemClickListener onRecyclerItemClickListener) {
        mContext = context;
        mItemClickListener = onRecyclerItemClickListener;
    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MovieHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_item_movies, parent, false));
    }

    @Override
    public void onBindViewHolder(MovieHolder holder, int position) {
        Movie movie = mItems.get(position);

        holder.mTitleTv.setText(movie.getTitle());

        String url = ApiUtils.getImageUrl(movie.getImagePath(), ApiUtils.IMAGE_SIZE_NORMAL);

        /*Glide.with(mContext)
                .load(movie.getImageUrl(IMAGE_WIDTH))
                .into(holder.mImageView);*/

        Glide.with(mContext).load(url)
                .listener(GlidePalette.with(url)
                        .use(GlidePalette.Profile.VIBRANT)
                        .intoBackground(holder.mTitleTv)
                        .intoTextColor(holder.mTitleTv, GlidePalette.Swatch.BODY_TEXT_COLOR)
                        .crossfade(true)
                ).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setItems(Collection<Movie> movies) {
        mItems.clear();
        if (movies != null) mItems.addAll(movies);
        notifyDataSetChanged();
    }

    public void clear() {
        mItems.clear();
    }

    public class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView mImageView;

        public TextView mTitleTv;

        public MovieHolder(View itemView) {
            super(itemView);

            mImageView = (ImageView) itemView.findViewById(R.id.list_item_image);
            mTitleTv = (TextView) itemView.findViewById(R.id.list_item_title);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mItemClickListener.onRecyclerItemClick(mItems.get(getAdapterPosition()));
        }
    }

    public interface OnRecyclerItemClickListener {
        void onRecyclerItemClick(Movie movie);
    }
}
