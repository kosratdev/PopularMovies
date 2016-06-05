package com.example.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;

/**
 * Created by kosrat on 6/3/16.
 * <p/>
 * Getting movie data from themoviedb API by creating a new thread to work in background.
 */
public class FetchMoviesTask extends AsyncTask<Void, Void, Movie[]> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    private GridViewAdapter mMovieAdapter;
    private Context mContext;

    public FetchMoviesTask(Context context, GridViewAdapter movieAdapter) {
        mContext = context;
        mMovieAdapter = movieAdapter;
    }

    /**
     * Take the String representing the complete movie in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     * <p/>
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     *
     * @param movieJsonStr is a json string.
     * @return an array of string
     */
    private Movie[] getMovieDataFromJson(String movieJsonStr)
            throws JSONException, ParseException {

        // Base image url that is used to get movie posters which is describes in this reference
        // http://docs.themoviedb.apiary.io/#reference/configuration/configuration/get?console=1
        final String BASE_POSTER_PATH = "http://image.tmdb.org/t/p/w500/";

        // These are the names of the JSON objects that need to be extracted.
        final String TMD_LIST = "results";
        final String TMD_TITLE = "original_title";
        final String TMD_POSTER = "poster_path";
        final String TMD_OVERVIEW = "overview";
        final String TMD_RATE = "vote_average";
        final String TMD_RELEASE = "release_date";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(TMD_LIST);

        Movie[] allMovies = new Movie[movieArray.length()];

        for (int i = 0; i < movieArray.length(); i++) {
            String title;
            String poster;
            String overview;
            String rate;
            String release;

            // Get the JSON object representing a movie
            JSONObject aMovie = movieArray.getJSONObject(i);

            // Get all properties of the movie.
            title = aMovie.getString(TMD_TITLE);
            poster = BASE_POSTER_PATH + aMovie.getString(TMD_POSTER);
            overview = aMovie.getString(TMD_OVERVIEW);
            rate = aMovie.getString(TMD_RATE);
            release = aMovie.getString(TMD_RELEASE);

            allMovies[i] = new Movie(title, poster, overview, rate, release);
        }

        return allMovies;

    }

    @Override
    protected Movie[] doInBackground(Void... params) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String sortType = preferences.getString(mContext.getString(R.string.pref_sort_key),
                mContext.getString(R.string.pref_sort_popular));
        try {
            // Construct the URL for the TheMovieDb query
            // Possible parameters are available at https://www.themoviedb.org/documentation/api
            final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/" + sortType;

            final String API_PARAM = "api_key";

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(API_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                    .build();
            Log.i(LOG_TAG, builtUri.toString());

            URL url = new URL(builtUri.toString());

            // Create the request to TheMovieDb, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder builder = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                builder.append(line).append("\n");
            }

            if (builder.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            movieJsonStr = builder.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }


        try {
            return getMovieDataFromJson(movieJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the movie.
        return null;
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
        if (movies != null) {
            mMovieAdapter.clear();
            for (Movie movie : movies) {
                mMovieAdapter.add(movie);
            }
        }
    }
}
