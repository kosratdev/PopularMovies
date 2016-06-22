/**
 * Copyright 2016 Kosrat D. Ahmed
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * <p>
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.squareup.picasso.Picasso;

/**
 * Created by kosrat on 6/4/16.
 * <p/>
 * Display all properties of the Movie.
 */
public class DetailFragment extends Fragment {

    private Movie mMovie;
    public static final String MOVIE_ARGS = "mMovie args";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();

        if (intent != null) {
            mMovie = intent.getExtras().getParcelable(MOVIE_ARGS);
            ((TextView) rootView.findViewById(R.id.title_textview)).setText(mMovie.mTitle);
            ((TextView) rootView.findViewById(R.id.release_textview)).setText(mMovie.mReleaseDate);
            ((TextView) rootView.findViewById(R.id.rated_textview)).setText(mMovie.mRating);
            ((TextView) rootView.findViewById(R.id.overview_textview)).setText(mMovie.mOverview);
            ImageView posterImageView = (ImageView) rootView.findViewById(R.id.poster_imageview);

            // Using Picasso Library for handle image loading and caching
            // for more info look at Picasso reference http://square.github.io/picasso/
            Picasso.with(getContext()).load(mMovie.mPoster).into(posterImageView);
        }

        Button btnFavorite = (Button) rootView.findViewById(R.id.btn_favorite);
        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(MovieEntry.COLUMN_MOVIE_ID, mMovie.mId);
                values.put(MovieEntry.COLUMN_MOVIE_BACKDROP, mMovie.mBackdrop);
                values.put(MovieEntry.COLUMN_MOVIE_TITLE, mMovie.mTitle);
                values.put(MovieEntry.COLUMN_MOVIE_POSTER, mMovie.mPoster);
                values.put(MovieEntry.COLUMN_MOVIE_OVERVIEW, mMovie.mOverview);
                values.put(MovieEntry.COLUMN_MOVIE_RATED, mMovie.mRating);
                values.put(MovieEntry.COLUMN_MOVIE_RELEASE, mMovie.mReleaseDate);

                getActivity().getContentResolver().insert(MovieEntry.CONTENT_URI, values);

                Toast.makeText(getActivity(), "Data inserted to db", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }
}
