package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kosrat on 6/2/16.
 *
 * Movie class to store all properties of a Movie
 */
public class Movie implements Parcelable{
    String mTitle;
    String mPoster;
    String mOverview;
    String mRating;
    String mReleaseDate;

    public Movie(String title, String poster, String overview, String rating, String releaseDate) {
        this.mTitle = title;
        this.mPoster = poster;
        this.mOverview = overview;
        this.mRating = rating;
        this.mReleaseDate = releaseDate;
    }

    public Movie(){

    }


    @Override
    public int describeContents() {
        return 0;
    }

    // This is where you write the values you want to save to the `Parcel`.
    // The `Parcel` class has methods defined to help you save all of your values.
    // Note that there are only methods defined for simple values, lists, and other Parcelable objects.
    // You may need to make several classes Parcelable to send the data you want.
    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(mTitle);
        parcel.writeString(mPoster);
        parcel.writeString(mOverview);
        parcel.writeString(mRating);
        parcel.writeString(mReleaseDate);

    }

    // After implementing the `Parcelable` interface, we need to create the
    // `Parcelable.Creator<MyParcelable> CREATOR` constant for our class;
    // Notice how it has our class specified as its type.

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public Movie createFromParcel(Parcel parcel) {
            Movie mMovie = new Movie();
            mMovie.mTitle = parcel.readString();
            mMovie.mPoster = parcel.readString();
            mMovie.mOverview = parcel.readString();
            mMovie.mRating = parcel.readString();
            mMovie.mReleaseDate = parcel.readString();

            return mMovie;

        }

        // We just need to copy this and change the type to match our class.

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };
}
