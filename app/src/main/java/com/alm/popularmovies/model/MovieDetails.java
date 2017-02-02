package com.alm.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by A. Labay on 01/02/17.
 * As part of the project PopularMovies.
 */

public class MovieDetails implements Parcelable {

    public int movieId;

    public String title;

    public Date releaseDate;

    public String imagePath;

    public double voteAverage;

    public String synopsis;

    public MovieDetails(int movieId,
                        String title,
                        Date releaseDate,
                        String imagePath,
                        double voteAverage,
                        String synopsis) {

        this.movieId = movieId;
        this.title = title;
        this.releaseDate = releaseDate;
        this.imagePath = imagePath;
        this.voteAverage = voteAverage;
        this.synopsis = synopsis;
    }

    protected MovieDetails(Parcel in) {
        movieId = in.readInt();
        title = in.readString();
        imagePath = in.readString();
        voteAverage = in.readDouble();
        synopsis = in.readString();
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
        parcel.writeString(imagePath);
        parcel.writeDouble(voteAverage);
        parcel.writeString(synopsis);
    }
}
