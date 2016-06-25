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

    RecyclerView mRecyclerTrailers;
    RecyclerView mRecyclerReviews;

    private FloatingActionButton mFloat;
    private FloatingActionButton mFloatShare;

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

        mFloat = (FloatingActionButton) rootView.findViewById(R.id.float_button);
        mFloatShare = (FloatingActionButton) rootView.findViewById(R.id.fab_share);

        mRecyclerTrailers = (RecyclerView) rootView.findViewById(R.id.trailer_list);
        mRecyclerReviews = (RecyclerView) rootView.findViewById(R.id.review_recycler);

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

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);

        collapsingToolbar.setTitle(mMovie.mTitle);
        ((TextView) rootView.findViewById(R.id.release_textview)).setText(mMovie.mReleaseDate);
        ((TextView) rootView.findViewById(R.id.rated_textview)).setText(mMovie.mRating);
        ((TextView) rootView.findViewById(R.id.overview_textview)).setText(mMovie.mOverview);
        ImageView posterImageView = (ImageView) rootView.findViewById(R.id.poster_imageview);
        ImageView backdrop = (ImageView) rootView.findViewById(R.id.backdrop);

        // Using Picasso Library for handle image loading and caching
        // for more info look at Picasso reference http://square.github.io/picasso/
        Picasso.with(getContext()).load(mMovie.mPoster).into(posterImageView);
        Picasso.with(getContext()).load(mMovie.mBackdrop).into(backdrop);

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

        mFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavorite()) {
                    removeFavorite();
                } else {
                    makeFavorite();
                }
            }
        });

        updateFavoriteButton();

        mFloatShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareVideo();
            }
        });

        return rootView;
    }

    private void ShareVideo(){

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
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mMovie.mId);
    }

    private void fetchReviews() {
        FetchReviewTask task = new FetchReviewTask(this);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mMovie.mId);
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
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mMovie.mTitle);
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
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + mMovie.mId,
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
                            mMovie.mId);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
                            mMovie.mTitle);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER,
                            mMovie.mPoster);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
                            mMovie.mOverview);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RATED,
                            mMovie.mRating);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE,
                            mMovie.mReleaseDate);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP,
                            mMovie.mBackdrop);
                    getContext().getContentResolver().insert(
                            MovieContract.MovieEntry.CONTENT_URI,
                            movieValues
                    );
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(getActivity(), mMovie.mTitle + " Added Into Favorite", Toast.LENGTH_LONG).show();
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
                            MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + mMovie.mId, null);

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(getActivity(), mMovie.mTitle + " Removed From Favorite", Toast.LENGTH_LONG).show();
                updateFavoriteButton();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}
