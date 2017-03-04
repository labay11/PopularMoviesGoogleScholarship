package com.alm.popularmovies.api.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by A. Labay on 25/02/17.
 * As part of the project PopularMovies.
 */

public class Video implements Parcelable {

    public String name;

    public String key;

    public String type;

    public String site;

    public Video(String name,
                 String videoKey) {
        this.name = name;
        this.key = videoKey;
    }

    protected Video(Parcel in) {
        name = in.readString();
        key = in.readString();
        type = in.readString();
        site = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(key);
        dest.writeString(type);
        dest.writeString(site);
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public String getType() {
        return type;
    }

    public String getSite() {
        return site;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
}
