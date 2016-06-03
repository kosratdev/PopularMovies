package com.example.android.popularmovies;

import java.util.Calendar;

/**
 * Created by kosrat on 6/2/16.
 *
 * Movie class to store all properties of a Movie
 */
public class Movie {
    String mTitle;
    String mPoster;
    String mOverview;
    float mRating;
    Calendar mRealseDate;

    public Movie(String title, String poster, String overview, float rating, Calendar realseDate) {
        this.mTitle = title;
        this.mPoster = poster;
        this.mOverview = overview;
        this.mRating = rating;
        this.mRealseDate = realseDate;
    }
}
