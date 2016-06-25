/*
 * Copyright 2016 Kosrat D. Ahmed
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.popularmovies.ui;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.adapter.MovieGridAdapter;
import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.network.FetchMoviesTask;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kosrat on 5/30/16.
 * <p/>
 * Encapsulates fetching the movies and displaying it as a {@link GridView} layout.
 */
public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, MovieGridAdapter.Callbacks {

    private final String MOVIE_DATA = "movie_data";
    private final String MOVIE_SORT = "movie_sort";

    private String mMovieSort;

    private static final int CURSOR_LODER_ID = 0;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Movie movie);
    }

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private MovieGridAdapter mAdapter;

    @BindView(R.id.movie_recycler)
    RecyclerView mRecyclerView;


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Movie> movies = mAdapter.getMovies();
        if (movies != null && !movies.isEmpty()) {
            outState.putParcelableArrayList(MOVIE_DATA, movies);
        }
        outState.putString(MOVIE_SORT, mMovieSort);

        // Needed to avoid confusion, when we back from detail screen (i. e. top rated selected but
        // favorite movies are shown and onCreate was not called in this case).
        if (!mMovieSort.equals(getString(R.string.pref_sort_favorites))) {
            getLoaderManager().destroyLoader(CURSOR_LODER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        mMovieSort = getMovieSort();

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), getNumOfColumns()));
        // To avoid "E/RecyclerView: No adapter attached; skipping layout"
        mAdapter = new MovieGridAdapter(new ArrayList<Movie>(), this);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    public int getNumOfColumns() {

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        int numOfColumns;
        if (dpWidth < dpHeight) {
            // portrait mode
            numOfColumns = 2;
            if (dpWidth >= 600) { // for tablet sw600
                numOfColumns = 3;
            }
        } else {
            // landscape mode
            numOfColumns = 3;
        }
        return numOfColumns;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            mMovieSort = savedInstanceState.getString(MOVIE_SORT);
            if (savedInstanceState.containsKey(MOVIE_DATA)) {
                List<Movie> movies = savedInstanceState.getParcelableArrayList(MOVIE_DATA);
                mAdapter.add(movies);

                // For listening content updates for tow pane mode
                if (mMovieSort.equals(getString(R.string.pref_sort_favorites))) {
                    getLoaderManager().initLoader(CURSOR_LODER_ID, null, this);
                }
            }
        } else {
            // Fetch Movies only if savedInstanceState == null
            fetchMovies(mMovieSort);
        }
    }

    private String getMovieSort() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return preferences.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popular));
    }

    /**
     * update movie posters by getting data from themoviedb API
     */
    private void fetchMovies(String sort) {

        if (!sort.equals(getString(R.string.pref_sort_favorites))) {
            new FetchMoviesTask(getActivity(), mAdapter).execute();
        } else {
            getLoaderManager().initLoader(CURSOR_LODER_ID, null, this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        String newSort = getMovieSort();
        if (!mMovieSort.equals(newSort)) {
            fetchMovies(newSort);
            mMovieSort = newSort;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.add(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//        mMoviesAdapter.swapCursor(null);
    }

    @Override
    public void open(Movie movie, int position) {

        ((Callback) getActivity()).onItemSelected(movie);
//        Log.i("Movie", "open: "+ movie.mTitle);
//        Intent intent = new Intent(getActivity(), DetailActivity.class);
//        intent.putExtra(DetailFragment.MOVIE_ARGS, movie);
//        startActivity(intent);
    }

}
