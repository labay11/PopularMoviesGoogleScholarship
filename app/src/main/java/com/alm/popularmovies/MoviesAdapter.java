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
import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;

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

        String url = ApiUtils.getImageUrl(movie.getPosterPath(), ApiUtils.IMAGE_SIZE_NORMAL);

        Picasso.with(mContext)
                .load(url)
                .into(holder.mImageView,
                        PicassoPalette.with(url, holder.mImageView)
                                .use(PicassoPalette.Profile.VIBRANT)
                                .intoBackground(holder.mTitleTv)
                                .intoTextColor(holder.mTitleTv, PicassoPalette.Swatch.BODY_TEXT_COLOR));
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

    public void addItems(Collection<Movie> movies) {
        if (movies != null) {
            mItems.addAll(movies);
            notifyItemRangeInserted(mItems.size()-movies.size(), mItems.size());
        }
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
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
