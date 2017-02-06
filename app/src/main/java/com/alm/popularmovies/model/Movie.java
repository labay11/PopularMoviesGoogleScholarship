package com.alm.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.Date;

/**
 * Created by A. Labay on 24/01/17.
 * As part of the project PopularMovies.
 */

public class Movie implements Parcelable {

    private int movieId;

    private String title;

    private Date releaseDate;

    private String posterPath;

    private double voteAverage;

    private String synopsis;

    public Movie(int movieId, String title, String posterPath) {
        this.movieId = movieId;
        this.title = title;
        this.posterPath = posterPath;
    }

    public Movie(int movieId,
                 String title,
                 Date releaseDate,
                 String posterPath,
                 double voteAverage,
                 String synopsis) {

        this.movieId = movieId;
        this.title = title;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.voteAverage = voteAverage;
        this.synopsis = synopsis;
    }

    protected Movie(Parcel in) {
        this.movieId = in.readInt();
        this.title = in.readString();
        long tmpReleaseDate = in.readLong();
        this.releaseDate = tmpReleaseDate == -1 ? null : new Date(tmpReleaseDate);
        this.posterPath = in.readString();
        this.voteAverage = in.readDouble();
        this.synopsis = in.readString();
    }

    public int getMovieId() {
        return movieId;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public boolean hasImage() {
        return !TextUtils.isEmpty(posterPath) && !posterPath.equals("null");
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public boolean hasSynopsis() {
        return !TextUtils.isEmpty(synopsis) && synopsis.equals("null");
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.movieId);
        dest.writeString(this.title);
        dest.writeLong(this.releaseDate != null ? this.releaseDate.getTime() : -1);
        dest.writeString(this.posterPath);
        dest.writeDouble(this.voteAverage);
        dest.writeString(this.synopsis);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) { return new Movie(source); }

        @Override
        public Movie[] newArray(int size) {return new Movie[size];}
    };
}
