package com.example.android.popularmovies;

import java.util.Calendar;

/**
 * Created by kosrat on 6/2/16.
 */
public class Movie {
    String title;
    String poster;
    String overview;
    float rating;
    Calendar realseDate;

    public Movie(String title, String poster, String overview, float rating, Calendar realseDate) {
        this.title = title;
        this.poster = poster;
        this.overview = overview;
        this.rating = rating;
        this.realseDate = realseDate;
    }
}
