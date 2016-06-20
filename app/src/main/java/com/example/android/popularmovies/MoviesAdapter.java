package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.android.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by kosrat on 6/21/16.
 */
public class MoviesAdapter extends CursorAdapter {

    private Context mContext;
    private static int sLoaderId;

    public MoviesAdapter(Context context, Cursor c, int flags, int loaderId) {
        super(context, c, flags);
        mContext = context;
        sLoaderId = loaderId;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.movie_poster;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder holder = (ViewHolder) view.getTag();

        String poster= cursor.getString(MovieContract.MovieEntry.COL_MOIVE_POSTER);

        // Using Picasso Library for handle image loading and caching
        // for more info look at Picasso reference http://square.github.io/picasso/
        if (!poster.equals("")) {
            Picasso.with(context)
                    .load(poster)
                    .placeholder(R.drawable.test_poster) // before load an image
                    .error(R.mipmap.ic_launcher) // at error of loading image
                    .into(holder.imageView);
        }
    }

    public static class ViewHolder {
        public final ImageView imageView;

        public ViewHolder(View view){
            imageView = (ImageView) view.findViewById(R.id.poster_imageview);
        }
    }
}
