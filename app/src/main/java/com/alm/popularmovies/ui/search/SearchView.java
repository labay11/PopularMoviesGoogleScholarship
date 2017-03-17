package com.alm.popularmovies.ui.search;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.alm.popularmovies.R;
import com.alm.popularmovies.adapters.BaseRecyclerAdapter;
import com.alm.popularmovies.adapters.SearchAdapter;
import com.alm.popularmovies.api.model.Movie;
import com.alm.popularmovies.utils.Utils;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by A. Labay on 12/03/17.
 * As part of the project PopularMovies.
 */
public class SearchView extends AppCompatActivity implements
        ISearch.View,
        BaseRecyclerAdapter.OnItemClickListener<Movie> {

    @BindView(R.id.et_search)
    public EditText mSearchView;
    @BindView(R.id.progress_bar)
    public ProgressBar mProgressBar;
    @BindView(R.id.rv_movies)
    public RecyclerView mRecyclerView;
    @BindView(R.id.container_error)
    public View mErrorContainer;

    private ISearch.Presenter mPresenter;

    private SearchAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        setupRecyclerView();

        mPresenter = new SearchPresenter(this, this);
        mPresenter.onCreate(savedInstanceState);

        RxTextView.textChanges(mSearchView)
                .filter(charSequence -> charSequence.length() >= 3)
                .delay(300, TimeUnit.MILLISECONDS)
                .subscribe(charSequence -> mPresenter.query(charSequence.toString()));

        /*mSearchView.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_SEARCH) {
                hideSoftKeyboard();
                return true;
            }

            return false;
        });*/
    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new SearchAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPresenter.saveState(outState);
    }

    @Override
    public void setQuery(String q) {
        mSearchView.setText(q);
    }

    @Override
    public void showResults(List<Movie> data) {
        mProgressBar.setVisibility(View.GONE);

        if (data == null || data.isEmpty()) {
            mErrorContainer.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            mAdapter.clear();
        } else {
            int count = mAdapter.getItemCount();
            mAdapter.set(data);
            mErrorContainer.setVisibility(View.GONE);
            if (count == 0)
                Utils.crossfade(mRecyclerView, mProgressBar);
        }
    }

    @Override
    public void showError() {
        mErrorContainer.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mAdapter.clear();
    }

    @Override
    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
        if (mAdapter.getItemCount() == 0)
            mRecyclerView.setVisibility(View.GONE);
        mErrorContainer.setVisibility(View.GONE);
    }

    @Override
    public ArrayList<Movie> getResults() {
        return mAdapter.getItems();
    }

    @Override
    public void onItemClick(View itemView, int pos, Movie item) {
        Utils.navigateToDetails(this, item);
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
}
