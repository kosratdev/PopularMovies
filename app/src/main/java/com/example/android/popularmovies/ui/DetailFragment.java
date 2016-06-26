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

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.adapter.ReviewAdapter;
import com.example.android.popularmovies.adapter.TrailerAdapter;
import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.model.Trailer;
import com.example.android.popularmovies.network.FetchReviewTask;
import com.example.android.popularmovies.network.FetchTrailersTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by kosrat on 6/4/16.
 * <p/>
 * Display all properties of the Movie.
 */
public class DetailFragment extends Fragment implements TrailerAdapter.Callbacks,
        FetchTrailersTask.Listener, FetchReviewTask.Listener, ReviewAdapter.Callback {

    private Movie mMovie;
    public static final String MOVIE_ARGS = "MOVIE_ARGS";
    public static final String EXTRA_TRAILERS = "EXTRA_TRAILERS";
    public static final String EXTRA_REVIEWS = "EXTRA_REVIEWS";

    private Intent sharingIntent=null;

    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;

    @BindView(R.id.trailer_list)
    RecyclerView mRecyclerTrailers;
    @BindView(R.id.review_recycler)
    RecyclerView mRecyclerReviews;

    @BindView(R.id.float_button)
    FloatingActionButton mFloat;
    @BindView(R.id.fab_share)
    FloatingActionButton mFloatShare;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;

    @BindView(R.id.release_textview)
    TextView mMovieRelease;
    @BindView(R.id.rated_textview)
    TextView mMovieRated;
    @BindView(R.id.overview_textview)
    TextView mMovieOverview;
    @BindView(R.id.poster_imageview)
    ImageView mMoviePoster;
    @BindView(R.id.backdrop)
    ImageView mMovieBackdrop;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(MOVIE_ARGS)) {
            mMovie = getArguments().getParcelable(MOVIE_ARGS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);

        // For horizontal list of trailers
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerTrailers.setLayoutManager(layoutManager);
        mTrailerAdapter = new TrailerAdapter(new ArrayList<Trailer>(), this);
        mRecyclerTrailers.setAdapter(mTrailerAdapter);
        mRecyclerTrailers.setNestedScrollingEnabled(false);

        // For vertical list of reviews
        LinearLayoutManager reviewLayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerReviews.setLayoutManager(reviewLayoutManager);
        mReviewAdapter = new ReviewAdapter(new ArrayList<Review>(), this);
        mRecyclerReviews.setAdapter(mReviewAdapter);

        mCollapsingToolbar.setTitle(mMovie.getTitle());

        mMovieRelease.setText(mMovie.getReleaseDate());
        mMovieRated.setText(mMovie.getRating());
        mMovieOverview.setText(mMovie.getOverview());

        // Using Picasso Library for handle image loading and caching
        // for more info look at Picasso reference http://square.github.io/picasso/
        Picasso.with(getContext())
                .load(mMovie.getPoster())
                .placeholder(R.drawable.temp_poster) // before load an image
                .error(R.drawable.temp_poster)
                .into(mMoviePoster);

        Picasso.with(getContext())
                .load(mMovie.getBackdrop())
                .placeholder(R.drawable.temp_poster) // before load an image
                .error(R.drawable.temp_poster)
                .into(mMovieBackdrop);

        // Fetch trailers only if savedInstanceState == null
        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_TRAILERS)) {
            List<Trailer> trailers = savedInstanceState.getParcelableArrayList(EXTRA_TRAILERS);
            mTrailerAdapter.add(trailers);
//            mButtonWatchTrailer.setEnabled(true);
        } else {
            fetchTrailers();
        }

        // Fetch reviews only if savedInstanceState == null
        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_REVIEWS)) {
            List<Review> reviews = savedInstanceState.getParcelableArrayList(EXTRA_REVIEWS);
            mReviewAdapter.add(reviews);
        } else {
            fetchReviews();
        }

        updateFavoriteButton();

        return rootView;
    }

    @OnClick(R.id.float_button)
    public void makeAsFavorite(){
        if (isFavorite()) {
            removeFavorite();
        } else {
            makeFavorite();
        }
    }

    @OnClick(R.id.fab_share)
    public void ShareVideo(){

        if(sharingIntent != null) {
            Intent intent = Intent.createChooser(sharingIntent, "Share trailer via");

            // We only start the activity if it resolves successfully
            if (sharingIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Log.i("Share", "Couldn't share Video Trailer for key: ");
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Trailer> trailers = mTrailerAdapter.getTrailers();
        if (trailers != null && !trailers.isEmpty()) {
            outState.putParcelableArrayList(EXTRA_TRAILERS, trailers);
        }

        ArrayList<Review> reviews = mReviewAdapter.getReviews();
        if (reviews != null && !reviews.isEmpty()) {
            outState.putParcelableArrayList(EXTRA_REVIEWS, reviews);
        }
    }

    @Override
    public void watch(Trailer trailer, int position) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailer.getTrailerUrl())));
    }

    private void fetchTrailers() {
        FetchTrailersTask task = new FetchTrailersTask(this);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mMovie.getId());
    }

    private void fetchReviews() {
        FetchReviewTask task = new FetchReviewTask(this);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mMovie.getId());
    }

    @Override
    public void onFetchFinished(List<Trailer> trailers) {

        mTrailerAdapter.add(trailers);

        if (mTrailerAdapter.getItemCount() > 0) {
            Trailer trailer = mTrailerAdapter.getTrailers().get(0);
            updateShareIntent(trailer);
        }
    }

    private void updateShareIntent(Trailer trailer) {
        sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mMovie.getTitle());
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, trailer.getName() + ": "
                + trailer.getTrailerUrl());
    }

    @Override
    public void onReviewFetchFinished(List<Review> reviews) {
        mReviewAdapter.add(reviews);
    }

    @Override
    public void read(Review review, int position) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(review.getUrl())));
    }

    private void updateFavoriteButton() {

        if (isFavorite()) {
            mFloat.setImageResource(R.drawable.ic_favorite);
        } else {
            mFloat.setImageResource(R.drawable.ic_favorite_border);
        }
    }

    private boolean isFavorite() {

        Cursor movieCursor = getContext().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry.COLUMN_MOVIE_ID},
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + mMovie.getId(),
                null,
                null);

        if (movieCursor != null && movieCursor.moveToFirst()) {
            movieCursor.close();
            return true;
        } else {
            return false;
        }


    }

    private void makeFavorite() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (!isFavorite()) {
                    ContentValues movieValues = new ContentValues();
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                            mMovie.getId());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
                            mMovie.getTitle());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER,
                            mMovie.getPoster());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
                            mMovie.getOverview());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RATED,
                            mMovie.getRating());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE,
                            mMovie.getReleaseDate());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP,
                            mMovie.getBackdrop());
                    getContext().getContentResolver().insert(
                            MovieContract.MovieEntry.CONTENT_URI,
                            movieValues
                    );
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(getActivity(), mMovie.getTitle() + " Added to Favorite", Toast.LENGTH_LONG).show();
                updateFavoriteButton();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void removeFavorite() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (isFavorite()) {
                    getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                            MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + mMovie.getId(), null);

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(getActivity(), mMovie.getTitle() + " Removed From Favorite", Toast.LENGTH_LONG).show();
                updateFavoriteButton();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}
