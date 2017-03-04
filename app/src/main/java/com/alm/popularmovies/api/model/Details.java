package com.alm.popularmovies.api.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.alm.popularmovies.utils.ApiUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import okhttp3.ResponseBody;

import static android.content.ContentValues.TAG;

/**
 * Created by A. Labay on 26/02/17.
 * As part of the project PopularMovies.
 */

public class Details implements Parcelable {

    private int movieId;

    private String title;

    private Date releaseDate;

    private String posterPath;

    private float voteAverage;

    private String synopsis;

    public Details(int movieId, String title, String posterPath) {
        this.movieId = movieId;
        this.title = title;
        this.posterPath = posterPath;
    }
    public Details(int movieId,
                 String title,
                 Date releaseDate,
                 String posterPath,
                 float voteAverage,
                 String synopsis) {

        this.movieId = movieId;
        this.title = title;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.voteAverage = voteAverage;
        this.synopsis = synopsis;
    }

    protected Details(Parcel in) {
        this.movieId = in.readInt();
        this.title = in.readString();
        long tmpReleaseDate = in.readLong();
        this.releaseDate = tmpReleaseDate == -1 ? null : new Date(tmpReleaseDate);
        this.posterPath = in.readString();
        this.voteAverage = in.readFloat();
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

    public float getVoteAverage() {
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
        dest.writeFloat(this.voteAverage);
        dest.writeString(this.synopsis);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) { return new Movie(source); }

        @Override
        public Movie[] newArray(int size) {return new Movie[size];}
    };

    public static class Converter implements retrofit2.Converter<ResponseBody, Details> {
        @Override
        public Details convert(ResponseBody value) throws IOException {
            try {
                JSONObject root = new JSONObject(value.string());

                if (root.has("status_message")) {
                    // no results, log error and return null
                    ApiUtils.parseError(root);
                    return null;
                }

                Date realeseDate = null;
                try {
                    realeseDate = ApiUtils.API_DATE_FORMAT.parse(root.getString("release_date"));
                } catch (ParseException e) {
                    Log.e(TAG, "Error parsing date - " + root.getString("release_date"), e);
                }

                return new Details(
                        root.getInt("id"),
                        root.getString("original_title"),
                        realeseDate,
                        root.getString("poster_path"),
                        (float) root.getDouble("vote_average"),
                        root.getString("overview")
                );
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
