package com.alm.popularmovies.api.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.Date;

/**
 * Created by A. Labay on 24/01/17.
 * As part of the project PopularMovies.
 */

public class Movie implements Parcelable {

    private long _id = -1;

    public int id;

    public String title;

    public String poster_path;

    public double vote_average;

    public Date release_date;

    public String overview;

    public Movie(long _id,
                 int id,
                 String title,
                 String poster_path) {
        this._id = _id;
        this.id = id;
        this.title = title;
        this.poster_path = poster_path;
    }

    public Movie(int id,
                 String title,
                 String poster_path,
                 double vote_average,
                 Date release_date,
                 String overview) {
        this.id = id;
        this.title = title;
        this.poster_path = poster_path;
        this.vote_average = vote_average;
        this.release_date = release_date;
        this.overview = overview;
    }

    public Movie(long _id,
                 int id,
                 String title,
                 String poster_path,
                 double vote_average,
                 long release_date,
                 String overview) {
        this._id = _id;
        this.id = id;
        this.title = title;
        this.poster_path = poster_path;
        this.vote_average = vote_average;
        this.release_date = new Date(release_date);
        this.overview = overview;
    }

    protected Movie(Parcel in) {
        _id = in.readLong();
        id = in.readInt();
        title = in.readString();
        poster_path = in.readString();
        vote_average = in.readDouble();
        release_date = new Date(in.readLong());
        overview = in.readString();
    }

    public boolean hasOverview() {
        return !TextUtils.isEmpty(overview) && !"null".equals(overview);
    }

    public boolean hasImage() {
        return !TextUtils.isEmpty(poster_path) && !"null".equals(poster_path);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(_id);
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(poster_path);
        dest.writeDouble(vote_average);
        dest.writeLong(release_date.getTime());
        dest.writeString(overview);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
