/**
 * Copyright 2016 Kosrat D. Ahmed
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * <p/>
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kosrat on 5/30/16.
 * <p/>
 * Encapsulates fetching the movies and displaying it as a {@link GridView} layout.
 */
public class MoviesFragment extends Fragment {

    private GridViewAdapter mMovieAdapter;
    ArrayList<Movie> mMovieList = new ArrayList<>();
    private GridView mGridView;

    private final String MOVIE_DATA = "movie_data";

    private String mMovieSort;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            // restore saved movie data
            mMovieList = savedInstanceState.getParcelableArrayList(MOVIE_DATA);
            setGridView(mMovieList);
        } else {
            // fetch data
            updateMovies();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // save movie data on rotate
        outState.putParcelableArrayList(MOVIE_DATA, mMovieList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mMovieSort = getMovieSort();

        mGridView = (GridView) rootView.findViewById(R.id.gridview);
        setGridView(mMovieList);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Movie movie = mMovieAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("movieDetail", movie);
                startActivity(intent);
            }
        });

        return rootView;
    }

    /**
     * Set gridview with movie's thumbnail
     *
     * @param movieList is a movie list thumbnail
     */
    private void setGridView(ArrayList<Movie> movieList) {
        mMovieAdapter = new GridViewAdapter(getActivity(), movieList);
        mGridView.setAdapter(mMovieAdapter);
    }

    private String getMovieSort() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return preferences.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popular));
    }

    /**
     * update movie posters by getting data from themoviedb API
     */
    private void updateMovies() {
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute();
    }

    @Override
    public void onResume() {
        super.onResume();

        String newSort = getMovieSort();
        if(!mMovieSort.equals(newSort)) {
            updateMovies();
            mMovieSort = newSort;
        }
    }

    /**
     * Grid view image adapter
     */
    public class GridViewAdapter extends ArrayAdapter<Movie> {

        /**
         * This is our own custom constructor (it doesn't mirror a superclass constructor).
         * The context is used to inflate the layout file, and the List is the data we want
         * to populate into the lists
         *
         * @param context   The current context. Used to inflate the layout file.
         * @param movieList A List of Movie objects to display in a list
         */

        public GridViewAdapter(Context context, List<Movie> movieList) {
            // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
            // the second argument is used when the ArrayAdapter is populating a single ImageView.
            // Because this is a custom adapter for an ImageView, the adapter is not
            // going to use this second argument, so it can be any value. Here, we used 0.
            super(context, 0, movieList);
        }

        /**
         * Provides a view for an AdapterView (ListView, GridView, etc.)
         *
         * @param position    The AdapterView position that is requesting a view
         * @param convertView The recycled view to populate.
         *                    (search online for "android view recycling" to learn more)
         * @param parent      The parent ViewGroup that is used for inflation.
         * @return The View for the position in the AdapterView.
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Gets the Movie object from the ArrayAdapter at the appropriate position
            Movie movie = getItem(position);

            ViewHolder holder;
            // Adapters recycle views to AdapterViews.
            // If this is a new View object we're getting, then inflate the layout.
            // If not, this view already has the layout inflated from a previous call to getView,
            // and we modify the View widgets as usual.
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.movie_poster, parent, false);
                holder = new ViewHolder();
                holder.poster = (ImageView) convertView.findViewById(R.id.poster_imageview);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            // Using Picasso Library for handle image loading and caching
            // for more info look at Picasso reference http://square.github.io/picasso/
            if (!movie.mPoster.equals("")) {
                Picasso.with(getContext())
                        .load(movie.mPoster)
                        .placeholder(R.drawable.test_poster) // before load an image
                        .error(R.mipmap.ic_launcher) // at error of loading image
                        .into(holder.poster);
            }


            return convertView;
        }

        /**
         * ViewHolder used to not call findViewById() frequently during the scrolling of ListView
         * (or GridView), which can slow down performance.
         */
        private class ViewHolder{
            public ImageView poster;
        }
    }
    /**
     * Getting movie data from themoviedb API by creating a new thread to work in background.
     */
    public class FetchMoviesTask extends AsyncTask<Void, Void, Movie[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        /**
         * Take the String representing the complete movie in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         *
         * @param movieJsonStr is a json string.
         * @return an array of string
         */
        private Movie[] getMovieDataFromJson(String movieJsonStr)
                throws JSONException, ParseException {

            // Base image url that is used to get movie posters which is describes in this reference
            // http://docs.themoviedb.apiary.io/#reference/configuration/configuration/get?console=1
            final String BASE_POSTER_PATH = "http://image.tmdb.org/t/p/w500/";

            // These are the names of the JSON objects that need to be extracted.
            final String TMD_LIST = "results";
            final String TMD_TITLE = "original_title";
            final String TMD_POSTER = "poster_path";
            final String TMD_OVERVIEW = "overview";
            final String TMD_RATE = "vote_average";
            final String TMD_RELEASE = "release_date";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(TMD_LIST);

            Movie[] allMovies = new Movie[movieArray.length()];

            for (int i = 0; i < movieArray.length(); i++) {
                String title;
                String poster;
                String overview;
                String rate;
                String release;

                // Get the JSON object representing a movie
                JSONObject aMovie = movieArray.getJSONObject(i);

                // Get all properties of the movie.
                title = aMovie.getString(TMD_TITLE);
                poster = BASE_POSTER_PATH + aMovie.getString(TMD_POSTER);
                overview = aMovie.getString(TMD_OVERVIEW);
                rate = aMovie.getString(TMD_RATE);
                release = aMovie.getString(TMD_RELEASE);

                allMovies[i] = new Movie(title, poster, overview, rate, release);
            }

            return allMovies;

        }

        @Override
        protected Movie[] doInBackground(Void... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortType = preferences.getString(getActivity().getString(R.string.pref_sort_key),
                    getActivity().getString(R.string.pref_sort_popular));
            try {
                // Construct the URL for the TheMovieDb query
                // Possible parameters are available at https://www.themoviedb.org/documentation/api
                final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/" + sortType;

                final String API_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(API_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();
                Log.i(LOG_TAG, builtUri.toString());

                URL url = new URL(builtUri.toString());

                // Create the request to TheMovieDb, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder builder = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    builder.append(line).append("\n");
                }

                if (builder.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = builder.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }


            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the movie.
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            if (movies != null) {
                mMovieAdapter.clear();
                for (Movie movie : movies) {
                    mMovieAdapter.add(movie);
                }
            }
        }
    }
}
