package com.example.android.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kosrat on 6/24/16.
 */
public class Reviews {

    @SerializedName("results")
    private List<Review> reviews= new ArrayList<>();

    public List<Review> getReviews(){
        return reviews;
    }
}
