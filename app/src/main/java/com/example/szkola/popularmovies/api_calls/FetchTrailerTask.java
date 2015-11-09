package com.example.szkola.popularmovies.api_calls;

import android.os.AsyncTask;
import android.util.Log;

import com.example.szkola.popularmovies.DetailActivityFragment;
import com.example.szkola.popularmovies.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchTrailerTask extends AsyncTask<String, Void, Trailer[]> {

    Trailer[] trailerDetailsArr;

    DetailActivityFragment detailActivityFragment;

    public FetchTrailerTask(DetailActivityFragment daf) {
        this.detailActivityFragment = daf;

    }




    private Trailer[] getMovieMeta(String movieJsonStr) throws JSONException {

        JSONObject movieMetaJson = new JSONObject(movieJsonStr);
        JSONArray movieMetaJsonJSONArr = movieMetaJson.getJSONArray("results");

        trailerDetailsArr = new Trailer[movieMetaJsonJSONArr.length()];
        for (int i = movieMetaJsonJSONArr.length() - 1; i >= 0; i--) {
            trailerDetailsArr[i] = new Trailer(
                    movieMetaJsonJSONArr.getJSONObject(i).getString("name"),
                    "https://www.youtube.com/watch?v="+
                    movieMetaJsonJSONArr.getJSONObject(i).getString("key")
            );
        }
        return trailerDetailsArr;
    }


    @Override
    protected Trailer[] doInBackground(String... params) {


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
    protected void onPostExecute(Trailer[] results) {

        detailActivityFragment.displayTrailers(results);

    }
}