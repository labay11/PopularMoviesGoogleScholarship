package com.alm.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.Date;

/**
 * Created by A. Labay on 01/02/17.
 * As part of the project PopularMovies.
 */

public class MovieDetails implements Parcelable {

    public int movieId;

    public String title;

    public Date releaseDate;

    public String posterPath;

    public double voteAverage;

    public String synopsis;

    public MovieDetails(int movieId,
                        String title,
                        Date releaseDate,
                        double voteAverage,
                        String synopsis,
                        String posterPath) {

        this.movieId = movieId;
        this.title = title;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.voteAverage = voteAverage;
        this.synopsis = synopsis;
    }

    protected MovieDetails(Parcel in) {
        movieId = in.readInt();
        title = in.readString();
        posterPath = in.readString();
        voteAverage = in.readDouble();
        synopsis = in.readString();
    }

    public boolean hasImage() {
        return !TextUtils.isEmpty(posterPath) && !posterPath.equals("null");
    }

    public static final Creator<MovieDetails> CREATOR = new Creator<MovieDetails>() {
        @Override
        public MovieDetails createFromParcel(Parcel in) {
            return new MovieDetails(in);
        }

        @Override
        public MovieDetails[] newArray(int size) {
            return new MovieDetails[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(movieId);
        parcel.writeString(title);
        parcel.writeLong(releaseDate != null ? releaseDate.getTime() : 0);
        parcel.writeString(posterPath);
        parcel.writeDouble(voteAverage);
        parcel.writeString(synopsis);
    }
}
