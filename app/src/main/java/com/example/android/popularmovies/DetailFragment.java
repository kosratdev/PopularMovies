/**
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
package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by kosrat on 6/4/16.
 * <p/>
 * Display all properties of the Movie.
 */
public class DetailFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if(intent != null) {
            Movie movie = intent.getExtras().getParcelable("movieDetail");
            ((TextView) rootView.findViewById(R.id.title_textview)).setText(movie.mTitle);
            ((TextView) rootView.findViewById(R.id.release_textview)).setText(movie.mReleaseDate);
            ((TextView) rootView.findViewById(R.id.rated_textview)).setText(movie.mRating);
            ((TextView) rootView.findViewById(R.id.overview_textview)).setText(movie.mOverview);
            ImageView posterImageView = (ImageView) rootView.findViewById(R.id.poster_imageview);

            // Using Picasso Library for handle image loading and caching
            // for more info look at Picasso reference http://square.github.io/picasso/
            Picasso.with(getContext()).load(movie.mPoster).into(posterImageView);
        }

        return rootView;
    }
}
