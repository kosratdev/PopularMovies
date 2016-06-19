package com.example.android.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

/*
    These are functions and some test data to make it easier to test your database and
    Content Provider.  Note that you'll want your MovieContract class to exactly match the one
    in our solution to use these as-given.
 */
public class TestUtilities extends AndroidTestCase {
    static final String TEST_ID = "293660";

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }


    static ContentValues createMovieValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.MovieEntry.COLUMN_MOIVE_ID, TEST_ID);
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, "Deadpool");
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, "/inVq3FRqcYIRl2la8iZikYYxFNR.jpg");
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, "Movie Overview");
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RATED, 7.17);
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE, "2016-02-09");
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP, "/inVq3FRqcYIRl2la8iZikYYxFNR.jpg");

        return testValues;
    }
}
