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

package com.example.android.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kosrat on 6/22/16.
 * Movie Recycle View Adapter.
 * Provide a reference to the views for each data item
 * Complex data items may need more than one view per item, and
 * you provide access to all the views for a data item in a view holder
 */
public class MovieAdapter
        extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private final ArrayList<Movie> mMovies;
    private final Callbacks mCallbacks;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        void open(Movie movie, int position);
    }

    public MovieAdapter(ArrayList<Movie> movies, Callbacks callbacks) {
        mMovies = movies;
        this.mCallbacks = callbacks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Movie movie = mMovies.get(position);
        final Context context = holder.mView.getContext();

        holder.mTitleView.setText(movie.getTitle());

        String posterUrl = movie.getPoster();

        if (!posterUrl.equals("")) {
            Picasso.with(context)
                    .load(posterUrl)
                    .placeholder(R.drawable.temp_poster) // before load an image
                    .error(R.drawable.temp_poster) // at error of loading image
                    .into(holder.mThumbnailView);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.open(movie, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    /**
     * Cache of the children views for a review list item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        @BindView(R.id.iv_thumbnail)
        ImageView mThumbnailView;
        @BindView(R.id.tv_title)
        TextView mTitleView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view;
        }

    }

    /**
     * Add the movie data to the view
     * @param movies is the movie ArrayList to attach
     * the view which is get from TheMovieDb API.
     */
    public void add(List<Movie> movies) {
        mMovies.clear();
        mMovies.addAll(movies);
        notifyDataSetChanged();
    }

    /**
     * Add the movie data to the view
     * @param cursor is the cursor to attach
     * the view which is get from local database.
     */
    public void add(Cursor cursor) {
        mMovies.clear();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(MovieContract.MovieEntry.COL_MOVIE_ID);
                String title = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_TITLE);
                String posterPath = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_POSTER);
                String overview = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_OVERVIEW);
                String rating = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_RATED);
                String releaseDate = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_RELEASE);
                String backdropPath = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_BACKDROP);
                Movie movie = new Movie(id, backdropPath, title, posterPath, overview, rating, releaseDate);
                mMovies.add(movie);
            } while (cursor.moveToNext());
        }
        notifyDataSetChanged();
    }

    public ArrayList<Movie> getMovies() {
        return mMovies;
    }
}