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
import com.example.android.popularmovies.model.Trailer;
import com.example.android.popularmovies.model.Trailers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by kosrat on 6/23/16.
 *
 * Getting trailers of a movie from themoviedb API by creating a new thread to work in background.
 */
public class FetchTrailersTask extends AsyncTask<Long, Void, List<Trailer>> {

    public static String LOG_TAG = FetchTrailersTask.class.getSimpleName();

    private final Listener mListener;

    /**
     * Interface definition for a callback to be invoked when trailers are loaded.
     */
    public interface Listener {
        void onFetchFinished(List<Trailer> trailers);
    }

    public FetchTrailersTask(Listener listener) {
        mListener = listener;
    }

    @Override
    protected List<Trailer> doInBackground(Long... params) {
        // if there is no movie id, there is nothing to look up.
        if(params.length == 0){
            return null;
        }

        long movieId = params[0];

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieTaskService service = retrofit.create(MovieTaskService.class);
        Call<Trailers> call = service.findTrailersById(movieId,
                BuildConfig.THE_MOVIE_DB_API_KEY);
        try {
            Response<Trailers> response = call.execute();
            Trailers trailers = response.body();
            return trailers.getTrailers();
        } catch (IOException e) {
            Log.e(LOG_TAG, "A problem occurred talking to the movie db ", e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Trailer> trailers) {
        if (trailers != null) {
            mListener.onFetchFinished(trailers);
        } else {
            mListener.onFetchFinished(new ArrayList<Trailer>());
        }
    }
}
