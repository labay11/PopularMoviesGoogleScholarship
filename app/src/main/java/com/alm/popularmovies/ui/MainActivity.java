package com.alm.popularmovies.ui;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.alm.popularmovies.R;
import com.alm.popularmovies.ui.movielist.MovieListView;
import com.alm.popularmovies.utils.PreferenceUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.app_name, R.string.app_name);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        int id = loadLastVisibleScreen();
        navigationView.setCheckedItem(id);

        if (savedInstanceState != null) {
            mFragment = getSupportFragmentManager()
                    .getFragment(savedInstanceState, "fragmentSavedState");
            if (mFragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack if needed
                transaction.replace(R.id.frame_container, mFragment);
                //transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        } else {
            loadFragment(id);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mFragment != null) {
            //Save the fragment's instance
            getSupportFragmentManager().putFragment(outState, "fragmentSavedState", mFragment);
        }
    }

    private int loadLastVisibleScreen() {
        switch (PreferenceUtils.getLastVIsibleScreen(this)) {
            case PreferenceUtils.SCREEN_POPULAR:
                return R.id.nav_popular;
            case PreferenceUtils.SCREEN_RATE:
                return R.id.nav_rated;
            case PreferenceUtils.SCREEN_FAVORITES:
                return R.id.nav_fav;

            default:
                return R.id.nav_popular;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            showAboutDialog();
        } else {
            loadFragment(id);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(@IdRes int id) {
        if (id == R.id.nav_popular) {
            mFragment = MovieListView.create(PreferenceUtils.SCREEN_POPULAR);
        } else if (id == R.id.nav_rated) {
            mFragment = MovieListView.create(PreferenceUtils.SCREEN_RATE);
        } else if (id == R.id.nav_fav) {
            mFragment = FavoritesFragment.create();
        }

        if (mFragment != null) {
            // Create new fragment and transaction
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack if needed
            transaction.replace(R.id.frame_container, mFragment);
            //transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();

            PreferenceUtils.setLastVisibleScreen(this, getScreenFromId(id));
        }
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.about)
                .setMessage(R.string.about_message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private int getScreenFromId(@IdRes int id) {
        switch (id) {
            case R.id.nav_popular:
                return PreferenceUtils.SCREEN_POPULAR;
            case R.id.nav_rated:
                return PreferenceUtils.SCREEN_RATE;
            case R.id.nav_fav:
                return PreferenceUtils.SCREEN_FAVORITES;

            default:
                return PreferenceUtils.SCREEN_POPULAR;
        }
    }
}
