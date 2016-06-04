package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by kosrat on 5/30/16.
 *
 * Encapsulates fetching the movies and displaying it as a {@link GridView} layout.
 */
public class MoviesFragment extends Fragment {

    private GridViewAdapter mMovieAdapter;
    ArrayList<Movie> mMovieList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mMovieAdapter = new GridViewAdapter(getActivity(), mMovieList);

        GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
        gridview.setAdapter(mMovieAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Movie movie = mMovieAdapter.getItem(position);
                Intent intent= new Intent(getActivity(), DetailActivity.class)
                        .putExtra("title", movie.mTitle)
                        .putExtra("release", getDateStr(movie.mRealseDate))
                        .putExtra("rated", movie.mRating + "/10")
                        .putExtra("overview", movie.mOverview)
                        .putExtra("poster", movie.mPoster);
                startActivity(intent);
            }
        });

        return rootView;
    }

    /**
     * Convert Calendar to String Date
     * @param calendar is an object of calendar
     * @return a string date
     */
    private String getDateStr(Calendar calendar){
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(calendar.getTime());
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
        updateMovies();
    }
}
