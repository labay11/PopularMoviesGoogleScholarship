package com.alm.popularmovies.api;

import com.alm.popularmovies.utils.ApiUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by A. Labay on 26/02/17.
 * As part of the project PopularMovies.
 */

public class TheMovieDbServiceImpl {

    public static final String ENDPOINT = "https://api.themoviedb.org/3/";

    public static TheMovieDbService create() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build();

        Gson gson = new GsonBuilder()
                .setDateFormat(ApiUtils.DATE_FORMAT)
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();

        return retrofit.create(TheMovieDbService.class);
    }
}
