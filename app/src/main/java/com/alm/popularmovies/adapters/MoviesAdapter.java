package com.alm.popularmovies.adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.alm.popularmovies.R;
import com.alm.popularmovies.api.model.Movie;
import com.alm.popularmovies.utils.ApiUtils;
import com.alm.popularmovies.utils.DbUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by A. Labay on 24/01/17.
 * As part of the project PopularMovies.
 */
public class MoviesAdapter extends BaseRecyclerAdapter<Movie, MoviesAdapter.MovieHolder> {

    private final int PLACEHOLDER_COLORS[];

    public MoviesAdapter(Context context, OnItemClickListener<Movie> itemClickListener) {
        super(context, itemClickListener);

        PLACEHOLDER_COLORS = context.getResources().getIntArray(R.array.placeholder_list_colors);
    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MovieHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_item_movie, parent, false));
    }

    /*@Override
    public void onBindViewHolder(MovieHolder holder, int position) {
        Movie movie = mItems.get(position);

        String url = ApiUtils.getImageUrl(movie.poster_path, ApiUtils.IMAGE_SIZE_NORMAL);

        Picasso.with(mContext)
                .load(url)
                .placeholder(new ColorDrawable(PLACEHOLDER_COLORS[position % 5]))
                .into(holder.mImageView);

        holder.toggleFav(DbUtils.isMovieFav(mContext.getContentResolver(), movie.id));
    }*/

    class MovieHolder extends BaseRecyclerAdapter.BaseHolder<Movie>
            implements View.OnClickListener {

        private ImageView mImageView;

        private ImageButton mImageButton;

        private boolean isFav;

        MovieHolder(View itemView) {
            super(itemView);

            mImageView = (ImageView) itemView.findViewById(R.id.list_item_image);
            mImageButton = (ImageButton) itemView.findViewById(R.id.list_item_fav);

            mImageButton.setOnClickListener(view -> {
                Movie movie = get(getAdapterPosition());
                if (isFav) {
                    if (DbUtils.removeMovie(mContext.getContentResolver(), movie.id))
                        toggleFav(false);
                    else
                        Toast.makeText(mContext, R.string.failed_remove_fav, Toast.LENGTH_SHORT).show();
                } else {
                    Uri uri = DbUtils.insertMovie(mContext.getContentResolver(), movie);
                    if (uri != null)
                        toggleFav(true);
                    else
                        Toast.makeText(mContext, R.string.failed_insert_fav, Toast.LENGTH_SHORT).show();

                }
            });

            itemView.setOnClickListener(this);
        }

        @Override
        public void bind(Movie item) {
            String url = ApiUtils.getImageUrl(item.poster_path, ApiUtils.IMAGE_SIZE_NORMAL);

            Picasso.with(mContext)
                    .load(url)
                    .placeholder(new ColorDrawable(PLACEHOLDER_COLORS[getAdapterPosition() % 5]))
                    .into(mImageView);

            toggleFav(DbUtils.isMovieFav(mContext.getContentResolver(), item.id));
        }

        @Override
        public void onClick(View view) {
            final int index = getAdapterPosition();
            mItemClickListener.onItemClick(view, index, get(index));
        }

        private void toggleFav(boolean isFav) {
            this.isFav = isFav;
            mImageButton.setImageResource(isFav ?
                    R.drawable.ic_favorite : R.drawable.ic_not_favorite);
        }
    }
}
