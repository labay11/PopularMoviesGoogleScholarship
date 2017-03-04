package com.alm.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alm.popularmovies.R;
import com.alm.popularmovies.utils.ApiUtils;
import com.alm.popularmovies.api.model.Movie;
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

    private boolean isFav = false;

    public MoviesAdapter(Context context,
                         OnRecyclerItemClickListener onRecyclerItemClickListener) {
        mContext = context;
        mItemClickListener = onRecyclerItemClickListener;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MovieHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_item_movie, parent, false));
    }

    @Override
    public void onBindViewHolder(MovieHolder holder, int position) {
        Movie movie = mItems.get(position);

        holder.mTitleTv.setText(movie.title);

        if (isFav) {
            holder.mFavButton.setVisibility(View.VISIBLE);
        }

        String url = ApiUtils.getImageUrl(movie.poster_path, ApiUtils.IMAGE_SIZE_NORMAL);

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

    public ArrayList<Movie> getItems() {
        return mItems;
    }

    public void removeItem(int pos) {
        mItems.remove(pos);
        notifyItemRemoved(pos);
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView mImageView;

        public TextView mTitleTv;

        public ImageButton mFavButton;

        public MovieHolder(View itemView) {
            super(itemView);

            mImageView = (ImageView) itemView.findViewById(R.id.list_item_image);
            mTitleTv = (TextView) itemView.findViewById(R.id.list_item_title);
            mFavButton = (ImageButton) itemView.findViewById(R.id.list_item_fav);

            mFavButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int index = getAdapterPosition();
                    mItemClickListener.onToggleFavClick(index, mItems.get(index));
                }
            });

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            final int index = getAdapterPosition();
            mItemClickListener.onRecyclerItemClick(index, mItems.get(index));
        }
    }

    public interface OnRecyclerItemClickListener {
        void onRecyclerItemClick(int index, Movie movie);

        void onToggleFavClick(int index, Movie movie);
    }
}
