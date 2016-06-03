package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Encapsulates fetching the movies and displaying it as a {@link GridView} layout.
 * Created by kosrat on 5/30/16.
 */
public class MoviesFragment extends Fragment {
    private GridViewAdapter mMovieAdapter;
    ArrayList<Movie> movieList = new ArrayList<>();

    public MoviesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mMovieAdapter = new GridViewAdapter(getActivity(), movieList);

        GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
        gridview.setAdapter(mMovieAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });
        updateMovies();
        return rootView;
    }

    /**
     * update movie posters by getting data from themoviedb API
     */
    private void updateMovies() {
        FetchMoviesTask moviesTask = new FetchMoviesTask(mMovieAdapter);
        moviesTask.execute();
    }

}
