package com.example.szkola.popularmovies.api_calls;

import android.os.AsyncTask;
import android.util.Log;

import com.example.szkola.popularmovies.DetailActivityFragment;
import com.example.szkola.popularmovies.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchReviewTask extends AsyncTask<String, Void, Review[]> {

    Review[] reviewDetailsArr;

    DetailActivityFragment detailActivityFragment;

    public FetchReviewTask(DetailActivityFragment daf) {
        this.detailActivityFragment = daf;

    }


//        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    private Review[] getMovieMeta(String movieJsonStr) throws JSONException {

        JSONObject movieMetaJson = new JSONObject(movieJsonStr);
        JSONArray movieMetaJsonJSONArr = movieMetaJson.getJSONArray("results");

        reviewDetailsArr = new Review[movieMetaJsonJSONArr.length()];
        for (int i = movieMetaJsonJSONArr.length() - 1; i >= 0; i--) {
            reviewDetailsArr[i] = new Review(
                    movieMetaJsonJSONArr.getJSONObject(i).getString("author"),
                    movieMetaJsonJSONArr.getJSONObject(i).getString("content")
            );
        }
        return reviewDetailsArr;
    }


    @Override
    protected Review[] doInBackground(String... params) {


        if (params.length == 0) {
            return null;
        }

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesMetaStr = null;
        try {
            // URL for movies API
            // More info available at:
            // https://www.themoviedb.org/documentation/api
            URL url = new URL(params[0]);

            // Create the request to themoviedb.org, and open the connection
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
                // builder for debugging.
                builder.append(line).append("\n");
            }

            if (builder.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            moviesMetaStr = builder.toString();
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            // If the code didn't successfully get the movies data, there's no point in attempting
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
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }

        try {
            return getMovieMeta(moviesMetaStr);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    protected void onPostExecute(Review[] results) {

        detailActivityFragment.displayReviews(results);

    }
}