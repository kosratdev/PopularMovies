package com.example.android.popularmovies.network;

import com.example.android.popularmovies.model.Trailers;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by kosrat on 6/23/16.
 */
public interface MovieTaskService {

//    @GET("3/movie/{sort_by}")
//    Call<Movies> discoverMovies(@Path("sort_by") String sortBy, @Query("api_key") String apiKey);

    @GET("3/movie/{id}/videos")
    Call<Trailers> findTrailersById(@Path("id") long movieId, @Query("api_key") String apiKey);

//    @GET("3/movie/{id}/reviews")
//    Call<Reviews> findReviewsById(@Path("id") long movieId, @Query("api_key") String apiKey);

}
