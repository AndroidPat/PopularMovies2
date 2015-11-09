package com.example.szkola.popularmovies;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * class serving as a persistent entity for SQLite and Parcelable
 */
public class MovieMeta implements Parcelable {

    String movieId;
    String title;
    String plot;
    String userRating;
    String releaseDate;
    String posterPath;

    public MovieMeta(){
    }

    public MovieMeta(String movieId,String title, String plot, String userRating, String releaseDate,String posterPath){
        this.movieId = movieId;
        this.title = title;
        this.plot = plot;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;

    }




    protected MovieMeta(Parcel in) {
        movieId =in.readString();
        title =in.readString();
        plot =in.readString();
        userRating =in.readString();
        releaseDate =in.readString();
        posterPath = in.readString();

    }

    public String getMovie_id() {
        return movieId;
    }

    public String getTitle() {
        return title;
    }

    public String getPlot() {
        return plot;
    }

    public String getUserRating() {
        return userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public static final Creator<MovieMeta> CREATOR = new Creator<MovieMeta>() {
        @Override
        public MovieMeta createFromParcel(Parcel in) {
            return new MovieMeta(in);
        }

        @Override
        public MovieMeta[] newArray(int size) {
            return new MovieMeta[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(movieId);
        parcel.writeString(title);
        parcel.writeString(plot);
        parcel.writeString(userRating);
        parcel.writeString(releaseDate);
        parcel.writeString(posterPath);
    }

    public static MovieMeta fromCursor(Cursor cursor){
        MovieMeta movieMeta = new MovieMeta();
        return movieMeta;
    }
}
