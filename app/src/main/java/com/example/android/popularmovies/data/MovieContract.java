/*
 * Copyright (C) 2016. Kosrat D. Ahmed
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.popularmovies.data;

import android.provider.BaseColumns;

/**
 * Created by kosrat on 6/9/16.
 */
public class MovieContract {

    public static class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "movies";

        // column for storing the id of movie
        public static final String COLUMN_MOIVE_ID = "movie_id";

        // column for storing the title of movie
        public static final String COLUMN_MOVIE_TITLE = "title";

        // column for storing the poster path of movie
        public static final String COLUMN_MOVIE_POSTER = "poster_path";

        // column for storing the overview of movie
        public static final String COLUMN_MOVIE_OVERVIEW = "overview";

        // column for storing the rated of movie
        public static final String COLUMN_MOVIE_RATED = "rated";

        // column for storing the release date of movie
        public static final String COLUMN_MOVIE_RELEASE = "release_date";

        // column for storing backdrop image of the movie
        public static final String COLUMN_MOVIE_BACKDROP = "backdrop_path";
    }
}
