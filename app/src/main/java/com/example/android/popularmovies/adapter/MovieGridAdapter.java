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

/**
 * Created by kosrat on 6/22/16.
 */
public class MovieGridAdapter
        extends RecyclerView.Adapter<MovieGridAdapter.ViewHolder> {

    private final ArrayList<Movie> mMovies;
    private final Callbacks mCallbacks;

    public interface Callbacks {
        void open(Movie movie, int position);
    }

    public MovieGridAdapter(ArrayList<Movie> movies, Callbacks callbacks) {
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

        holder.mTitleView.setText(movie.mTitle);

        String posterUrl = movie.mPoster;

        if (!posterUrl.equals("")) {
            Picasso.with(context)
                    .load(posterUrl)
                    .placeholder(R.drawable.test_poster) // before load an image
                    .error(R.mipmap.ic_launcher) // at error of loading image
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        ImageView mThumbnailView;
        TextView mTitleView;

        public ViewHolder(View view) {
            super(view);
            mThumbnailView = (ImageView) view.findViewById(R.id.iv_thumbnail);
            mTitleView = (TextView) view.findViewById(R.id.tv_title);
            mView = view;
        }

    }

    public void add(List<Movie> movies) {
        mMovies.clear();
        mMovies.addAll(movies);
        notifyDataSetChanged();
    }

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