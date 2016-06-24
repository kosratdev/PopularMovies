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
