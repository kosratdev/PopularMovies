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
