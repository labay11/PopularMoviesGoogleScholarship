package com.alm.popularmovies.api;

import com.alm.popularmovies.api.model.Movie;
import com.alm.popularmovies.api.model.Movies;
import com.alm.popularmovies.api.model.Reviews;
import com.alm.popularmovies.api.model.Videos;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by A. Labay on 26/02/17.
 * As part of the project PopularMovies.
 */

public interface TheMovieDbService {

    @GET("movie/popular")
    Observable<Movies> getPopularMovies(
            @Query("api_key") String apiKey,
            @Query("region") String region,
            @Query("language") String language,
            @Query("page") int page);

    @GET("movie/top_rated")
    Observable<Movies> getTopRatedMovies(
            @Query("api_key") String apiKey,
            @Query("region") String region,
            @Query("language") String language,
            @Query("page") int page);

    @GET("movie/{movieId}")
    Observable<Movie> getDetails(
            @Path("movieId") int movieId,
            @Query("api_key") String apiKey,
            @Query("language") String language);

    @GET("movie/{movieId}/videos")
    Observable<Videos> getVideos(
            @Path("movieId") int movieId,
            @Query("api_key") String apiKey);

    @GET("movie/{movieId}/reviews")
    Observable<Reviews> getReviews(
            @Path("movieId") int movieId,
            @Query("api_key") String apiKey,
            @Query("page") int page);
}
