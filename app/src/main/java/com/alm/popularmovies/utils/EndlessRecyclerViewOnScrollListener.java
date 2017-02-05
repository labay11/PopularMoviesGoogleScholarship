package com.alm.popularmovies.utils;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by A. Labay on 05/02/17.
 * As part of the project PopularMovies.
 */

public abstract class EndlessRecyclerViewOnScrollListener extends RecyclerView.OnScrollListener {

    private static final int VISIBLE_THRESHOLD = 5; // changes depending on screen dim

    private GridLayoutManager gridLayoutManager;
    private boolean loading = false;

    private int page = 1;

    public EndlessRecyclerViewOnScrollListener(GridLayoutManager gridLayoutManager) {
        this.gridLayoutManager = gridLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int totalItemCount = gridLayoutManager.getItemCount();
        int lastVisibleItemPosition = gridLayoutManager.findLastVisibleItemPosition();

        boolean endHasBeenReached = lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount;
        if (!loading && totalItemCount > 0 && endHasBeenReached) {
            loading = true;
            onLoadMore(++page);
        }
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public boolean isLoading() {
        return loading;
    }

    public void reset() {
        loading = false;
        page = 1;
    }

    public abstract void onLoadMore(int page);
}
