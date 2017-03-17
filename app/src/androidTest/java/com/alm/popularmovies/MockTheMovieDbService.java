package com.alm.popularmovies;

import com.alm.popularmovies.api.TheMovieDbService;
import com.alm.popularmovies.api.model.Credits;
import com.alm.popularmovies.api.model.Movies;
import com.alm.popularmovies.api.model.Reviews;
import com.alm.popularmovies.api.model.Videos;
import com.google.gson.Gson;

import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.mock.BehaviorDelegate;
import rx.Observable;

/**
 * Created by A. Labay on 07/03/17.
 * As part of the project PopularMovies.
 */
public class MockTheMovieDbService implements TheMovieDbService {

    /*
     * Mock service tutorial: https://riggaroo.co.za/retrofit-2-mocking-http-responses/
     */

    private final BehaviorDelegate<TheMovieDbService> delegate;

    public MockTheMovieDbService(BehaviorDelegate<TheMovieDbService> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Observable<Movies> getPopularMovies(@Query("api_key") String apiKey,
                                               @Query("region") String region,
                                               @Query("language") String language,
                                               @Query("page") int page) {
        return delegate.returningResponse(new Gson().fromJson(MockData.POPULAR_MOVIES_JSON, Movies.class))
                .getPopularMovies(apiKey, region, language, page);
    }

    @Override
    public Observable<Movies> getTopRatedMovies(@Query("api_key") String apiKey,
                                                @Query("region") String region,
                                                @Query("language") String language,
                                                @Query("page") int page) {
        return null;
    }

    @Override
    public Observable<Credits> getCredits(@Path("movieId") int movieId,
                                          @Query("api_key") String apiKey) {
        return null;
    }

    @Override
    public Observable<Videos> getVideos(@Path("movieId") int movieId,
                                        @Query("api_key") String apiKey) {
        return null;
    }

    @Override
    public Observable<Reviews> getReviews(@Path("movieId") int movieId,
                                          @Query("api_key") String apiKey,
                                          @Query("page") int page) {
        return null;
    }

    @Override
    public Observable<Movies> search(@Query("api_key") String apiKey,
                                     @Query("query") String q,
                                     @Query("region") String region,
                                     @Query("language") String language) {
        return null;
    }
}
