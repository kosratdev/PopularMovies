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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

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
        FetchMoviesTask moviesTask = new FetchMoviesTask(getActivity(), mMovieAdapter);
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
}
