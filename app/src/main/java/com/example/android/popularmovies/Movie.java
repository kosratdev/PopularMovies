package com.example.android.popularmovies;

/**
 * Created by kosrat on 6/2/16.
 *
 * Movie class to store all properties of a Movie
 */
public class Movie {
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
}
