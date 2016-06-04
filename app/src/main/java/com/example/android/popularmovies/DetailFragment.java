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
            String title = intent.getStringExtra("title");
            String release = intent.getStringExtra("release");
            String rated = intent.getStringExtra("rated");
            String overview = intent.getStringExtra("overview");
            String poster = intent.getStringExtra("poster");

            ((TextView) rootView.findViewById(R.id.title_textview)).setText(title);
            ((TextView) rootView.findViewById(R.id.release_textview)).setText(release);
            ((TextView) rootView.findViewById(R.id.rated_textview)).setText(rated);
            ((TextView) rootView.findViewById(R.id.overview_textview)).setText(overview);
            ImageView posterImageView = (ImageView) rootView.findViewById(R.id.poster_imageview);

            // Using Picasso Library for handle image loading and caching
            // for more info look at Picasso reference http://square.github.io/picasso/
            Picasso.with(getContext()).load(poster).into(posterImageView);
        }

        return rootView;
    }
}
