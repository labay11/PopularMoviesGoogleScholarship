package com.alm.popularmovies.api.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by A. Labay on 25/02/17.
 * As part of the project PopularMovies.
 */

public class Review implements Parcelable {

    private int movieId;

    private String author;

    private String content;

    private String url;

    public Review(int movieId,
                  String author,
                  String content,
                  String url) {

        this.movieId = movieId;
        this.author = author;
        this.content = content;
        this.url = url;
    }

    protected Review(Parcel in) {
        movieId = in.readInt();
        author = in.readString();
        content = in.readString();
        url = in.readString();
    }

    public int getMovieId() {
        return movieId;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movieId);
        dest.writeString(author);
        dest.writeString(content);
        dest.writeString(url);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}
