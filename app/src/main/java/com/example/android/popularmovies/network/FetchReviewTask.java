/*
 * Copyright 2016 Kosrat D. Ahmed
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.example.android.popularmovies.network;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.model.Reviews;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by kosrat on 6/24/16.
 */
public class FetchReviewTask extends AsyncTask<Long , Void, List<Review>> {

    public static String LOG_TAG = FetchReviewTask.class.getSimpleName();

    public Listener mListener;

    /**
     * Interface definition for a callback to be invoked when trailers are loaded.
     */
    public interface Listener{
        void onReviewFetchFinished(List<Review> reviews);
    }
    public FetchReviewTask(Listener listener){
        mListener = listener;
    }
    @Override
    protected List<Review> doInBackground(Long... params) {


        if(params.length == 0){
            return null;
        }

        Long id = params[0];

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieTaskService movieService = retrofit.create(MovieTaskService.class);
        Call<Reviews> call = movieService.findReviewsById(id, BuildConfig.THE_MOVIE_DB_API_KEY);

        try {
            Response<Reviews> response = call.execute();
            Reviews reviews = response.body();

            return reviews.getReviews();
        } catch (IOException e) {
            Log.e(LOG_TAG, "A problem occurred talking to the movie db ", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Review> reviews) {
        if(reviews != null){
            mListener.onReviewFetchFinished(reviews);
        }else{
            mListener.onReviewFetchFinished(new ArrayList<Review>());
        }

    }
}
