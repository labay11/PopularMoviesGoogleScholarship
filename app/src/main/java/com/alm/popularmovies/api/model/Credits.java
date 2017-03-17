package com.alm.popularmovies.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by A. Labay on 17/03/17.
 * As part of the project PopularMovies.
 */
public class Credits {

    public static final String JOB_DIRECTOR = "Director";

    public int id;

    public List<Crew> crew;

    public Crew getDirector() {
        if (crew == null || crew.isEmpty())
            return null;

        for (Crew c : crew) {
            if (JOB_DIRECTOR.equals(c.job))
                return c;
        }

        return null;
    }

    public static class Crew implements Parcelable {

        public int id;

        public String job;

        public String name;

        protected Crew(Parcel in) {
            id = in.readInt();
            job = in.readString();
            name = in.readString();
        }

        public static final Creator<Crew> CREATOR = new Creator<Crew>() {
            @Override
            public Crew createFromParcel(Parcel in) {
                return new Crew(in);
            }

            @Override
            public Crew[] newArray(int size) {
                return new Crew[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(id);
            parcel.writeString(job);
            parcel.writeString(name);
        }
    }
}
