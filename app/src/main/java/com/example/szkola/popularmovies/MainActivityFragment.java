package com.example.szkola.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.szkola.popularmovies.api_calls.FetchMoviesTask;
import com.example.szkola.popularmovies.data.MoviesColumns;
import com.example.szkola.popularmovies.data.MoviesProvider;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public MainActivityFragment() {
    }

    private static final int MOVIES_LOADER = 0;

    private static final String[] MOVIES_COLUMNS =
            {
                    MoviesColumns._ID,
                    MoviesColumns.COLUMN_MOVIE_ID,
                    MoviesColumns.COLUMN_TITLE,
                    MoviesColumns.COLUMN_PLOT,
                    MoviesColumns.COLUMN_USER_RATING,
                    MoviesColumns.COLUMN_R_DATE,
                    MoviesColumns.COLUMN_POSTER_PATH
            };

    static final int COL_ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_TITLE = 2;
    static final int COL_PLOT = 3;
    static final int COL_USER_RATING = 4;
    static final int COL_R_DATE = 5;
    static final int COL_POSTER_PATH = 6;

    @Bind(R.id.moviesGridView)
    GridView gvMovies;

    String preferences = "";


    MovieMeta[] movieDetailsArr;
    public ImageAdapter mImageAdapter;
    private MainAdapter mMainAdapter;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getActivity(), MoviesProvider.Movies.CONTENT_URI,
                MOVIES_COLUMNS,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity()).
                getString("sort_list", "default");
        if (preferences.equals("favourites")) {
            mMainAdapter.swapCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity()).
                getString("sort_list", "default");
        if (preferences.equals("favourites")) {
            if (mMainAdapter != null) {
                mMainAdapter.swapCursor(null);
            }
        }
    }


    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        void onItemSelected(String id, String title, String user_rating, String plot, String r_date, String img_path);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState == null) {
            sortMovies();
        } else {
            Parcelable[] mD = savedInstanceState.getParcelableArray("MOVIE_KEY");
            if (mD != null) {
                movieDetailsArr = new MovieMeta[mD.length];
                System.arraycopy(mD, 0, movieDetailsArr, 0, mD.length);
                setmImageAdapter(movieDetailsArr);
            }
            preferences = PreferenceManager.getDefaultSharedPreferences(getActivity()).
                    getString("sort_list", "default");

            switch (preferences) {
                case ("popularity"):
                    getActivity().setTitle("Popular Movies");
                    break;
                case ("rating"):
                    getActivity().setTitle("Highly-Rated Movies");
                    break;
                case ("favourites"):
                    sortFavourites();
                    getActivity().setTitle("Favourites");
                    break;
                default:
                    getActivity().setTitle("Popular Movies App");
            }

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArray("MOVIE_KEY", movieDetailsArr);
        super.onSaveInstanceState(outState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ButterKnife.bind(this, rootView);

        gvMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                                preferences = PreferenceManager.getDefaultSharedPreferences(getActivity()).
                                                        getString("sort_list", "default");
                                                if (preferences.equals("favourites")) {
                                                    Cursor c = (Cursor) adapterView.getItemAtPosition(position);
                                                    if (c != null) {
                                                        ((Callbacks) getActivity()).onItemSelected(
                                                                c.getString(COL_MOVIE_ID),
                                                                c.getString(COL_TITLE),
                                                                c.getString(COL_USER_RATING),
                                                                c.getString(COL_PLOT),
                                                                c.getString(COL_R_DATE),
                                                                c.getString(COL_POSTER_PATH));
                                                    }


                                                } else {

                                                    String movieId = movieDetailsArr[position].getMovie_id();
                                                    String title = movieDetailsArr[position].getTitle();
                                                    String user_rating = movieDetailsArr[position].getUserRating();
                                                    String plot = movieDetailsArr[position].getPlot();
                                                    String r_date = movieDetailsArr[position].getReleaseDate();
                                                    String img_path = movieDetailsArr[position].getPosterPath();
                                                    ((Callbacks) getActivity()).onItemSelected(movieId, title, user_rating, plot, r_date, img_path);
                                                }
                                            }
                                        }
        );


        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void sortMovies() {

        String popularURL = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=" + getResources().getString(R.string.apiKey);
        String ratingsURL = "http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&vote_count.gte=1000&api_key=" +
                getResources().getString(R.string.apiKey);
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask(this);
        String preferences = PreferenceManager.getDefaultSharedPreferences(getActivity()).
                getString("sort_list", popularURL);

        switch (preferences) {
            case ("popularity"):
                if (isNetworkAvailable()) {
                    fetchMoviesTask.execute(popularURL);
                    getActivity().setTitle("Popular Movies");
                } else {
                    Toast.makeText(getActivity(), "No network connection", Toast.LENGTH_SHORT).show();
                }

                break;
            case ("rating"):
                if (isNetworkAvailable()) {
                    fetchMoviesTask.execute(ratingsURL);
                    getActivity().setTitle("Highly-Rated Movies");
                } else {
                    Toast.makeText(getActivity(), "No network connection", Toast.LENGTH_SHORT).show();
                }

                break;
            case ("favourites"):
                sortFavourites();
                getActivity().setTitle("Favourites");
                break;
            default:
                //display popular movies by default
                getActivity().setTitle("Popular Movies App");
                fetchMoviesTask.execute(popularURL);
        }
    }

    private void sortFavourites() {
        mMainAdapter = new MainAdapter(getActivity(), null, 0);
        gvMovies.setAdapter(mMainAdapter);
    }


    public void setmImageAdapter(MovieMeta[] results) {

        movieDetailsArr = results;
        ButterKnife.bind(this, getView());
        String[] posterPathArr = new String[results.length];


        for (int i = results.length - 1; i >= 0; i--) {
            posterPathArr[i] = results[i].getPosterPath();
        }


        mImageAdapter = new ImageAdapter(MainActivityFragment.this, getActivity(), posterPathArr);

        if (getView() != null) {
            gvMovies.setAdapter(mImageAdapter);
        } else {

            Toast.makeText(getActivity(), "no view", Toast.LENGTH_SHORT).show();
        }
    }


    //Checking for network connection - based on a stackoverflow snippet
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}





