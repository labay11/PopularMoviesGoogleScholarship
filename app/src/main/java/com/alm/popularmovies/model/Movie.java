package com.alm.popularmovies.model;

/**
 * Created by A. Labay on 24/01/17.
 * As part of the project PopularMovies.
 */

public class Movie {

    private int movieId;

    private String title;

    private String imagePath;

    public Movie(int movieId, String title, String imagePath) {
        this.movieId = movieId;
        this.title = title;
        this.imagePath = imagePath;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getTitle() {
        return title;
    }

    public String getImagePath() {
        return imagePath;
    }
}
