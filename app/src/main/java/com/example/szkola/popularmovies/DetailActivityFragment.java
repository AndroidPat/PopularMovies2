package com.example.szkola.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.szkola.popularmovies.api_calls.FetchReviewTask;
import com.example.szkola.popularmovies.api_calls.FetchTrailerTask;
import com.example.szkola.popularmovies.data.MoviesColumns;
import com.example.szkola.popularmovies.data.MoviesProvider;
import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.BindDrawable;
import butterknife.ButterKnife;

import static com.example.szkola.popularmovies.MainActivity.ismTwoPane;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    @Bind(R.id.txtTitle)
    TextView tvTitle;
    @Bind(R.id.txtRating)
    TextView tvRating;
    @Bind(R.id.txtPlot)
    TextView tvPlot;
    @Bind(R.id.txtDate)
    TextView tvDate;
    @Bind(R.id.imgView)
    ImageView imageView;
    @Bind(R.id.btnFav)
    ImageButton btnFav;
    @Bind(R.id.trailers)
    LinearLayout LinTrailers;
    @Bind(R.id.reviews)
    LinearLayout LinReviews;
    @BindDrawable(R.drawable.default_placeholder)
    Drawable defaultPlaceholder;
    private Toast mAppToast;

    String movieId;
    String title;
    String user_rating;
    String plot;
    String r_date;
    String img_path;

    String reviewsURL;
    String trailerURL;

    ShareActionProvider mShareActionProvider;


    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Bundle extras;


        if (ismTwoPane()) {
            extras = getArguments();
        } else {
            Intent intent = getActivity().getIntent();
            extras = intent.getExtras();
        }

        ButterKnife.bind(this, rootView);

        if (extras != null) {


            movieId = extras.getString("EXTRA_ID");
            title = extras.getString("EXTRA_TITLE");
            user_rating = extras.getString("EXTRA_USER_RATING");
            plot = extras.getString("EXTRA_PLOT");
            r_date = extras.getString("EXTRA_R_DATE");
            img_path = extras.getString("EXTRA_IMG");


            tvTitle.setText(title);
            tvRating.setText(getActivity().getString(R.string.format_rating, user_rating));
            tvPlot.setText(plot);
            if (r_date != null) {
                tvDate.setText(r_date.substring(0, 4));
            }

            PicassoPalette.with(img_path, imageView);
            Picasso.with(getActivity())
                    .load(img_path)
                    .placeholder(defaultPlaceholder)
                    .resize(450, 675)
                    .into(imageView
                            , PicassoPalette.with(img_path, imageView)
                                    .use(PicassoPalette.Profile.VIBRANT)
                                    .intoBackground(tvTitle, PicassoPalette.Swatch.RGB)
                                    .intoTextColor(tvTitle, PicassoPalette.Swatch.BODY_TEXT_COLOR)
                    );

        }

        final SharedPreferences preferences = this.getActivity().getSharedPreferences(movieId, 0);
        boolean favState = preferences.getBoolean(movieId, false);

        if (favState) {
            btnFav.setSelected(true);
        } else {
            btnFav.setSelected(false);
        }

        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Stop any previous toasts
                if (mAppToast != null) {
                    mAppToast.cancel();
                }

                if (!btnFav.isSelected()) {
                    btnFav.setSelected(true);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean(movieId, true);
                    editor.apply();

                    insertData();

                    mAppToast = Toast.makeText(getActivity(), "Added to favourites", Toast.LENGTH_SHORT);
                    mAppToast.show();

                } else {
                    btnFav.setSelected(false);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean(movieId, false);
                    editor.apply();

                    getActivity().getContentResolver().delete(
                            MoviesProvider.Movies.withId(movieId), null, null);

                    mAppToast = Toast.makeText(getActivity(), "Deleted from favourites", Toast.LENGTH_SHORT);
                    mAppToast.show();
                }
            }

        });

        return rootView;
    }

    public void displayTrailers(Trailer[] results) {
        ButterKnife.bind(this, getView());

        if (results != null) {
            final String[] youtubeLink = new String[results.length];
            String[] name = new String[results.length];


            for (int i = results.length - 1; i >= 0; i--) {
                youtubeLink[i] = results[i].getYoutubeURL();
                name[i] = results[i].getName();
                Button btn = new Button(getActivity());
                btn.setText(name[i]);
                btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                final int j = i;
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink[j])));
                    }
                });
                LinTrailers.addView(btn);
            }

            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareForecastIntent(youtubeLink[0]));
            } else {
                Log.d(LOG_TAG, "Share Action Provider is null?");
            }
        } else {
            Log.d(LOG_TAG, "no trailers to display");
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareForecastIntent("No trailer data for the given movie"));
            } else {
                Log.d(LOG_TAG, "Share Action Provider is null?");
            }
        }
    }

    public void displayReviews(Review[] results) {
        ButterKnife.bind(this, getView());

        if (results != null) {

            String[] author = new String[results.length];
            String[] contents = new String[results.length];

            for (int i = results.length - 1; i >= 0; i--) {
                author[i] = results[i].getAuthor();
                contents[i] = results[i].getContents();
                TextView tvAuthor = new TextView(getActivity());
                tvAuthor.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                tvAuthor.setPadding(40, 10, 40, 10);
                tvAuthor.setTextColor(Color.BLACK);
                tvAuthor.setText(getActivity().getString(R.string.format_wrote, author[i]));
                TextView tvContents = new TextView(getActivity());
                tvContents.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                tvContents.setPadding(40, 10, 40, 10);
                tvContents.setText(contents[i]);

                LinReviews.addView(tvAuthor);
                LinReviews.addView(tvContents);
            }
        } else {
            Log.d(LOG_TAG, "no reviews to display");
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        reviewsURL = "http://api.themoviedb.org/3/movie/" + movieId + "/reviews?api_key=" + getResources().getString(R.string.apiKey);
        trailerURL = "http://api.themoviedb.org/3/movie/" + movieId + "/videos?api_key=" + getResources().getString(R.string.apiKey);

        if (movieId != null) {
            new FetchTrailerTask(this).execute(trailerURL);
            new FetchReviewTask(this).execute(reviewsURL);
        } else
            Toast.makeText(getActivity(), "Fetching problem", Toast.LENGTH_SHORT).show();


        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {


        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

    }

    public void insertData() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(MoviesColumns.COLUMN_MOVIE_ID, movieId);
        contentValues.put(MoviesColumns.COLUMN_TITLE, title);
        contentValues.put(MoviesColumns.COLUMN_USER_RATING, user_rating);
        contentValues.put(MoviesColumns.COLUMN_PLOT, plot);
        contentValues.put(MoviesColumns.COLUMN_R_DATE, r_date);
        contentValues.put(MoviesColumns.COLUMN_POSTER_PATH, img_path);

        getActivity().getContentResolver().insert(MoviesProvider.Movies.CONTENT_URI, contentValues);
    }


    private Intent createShareForecastIntent(String msg) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                msg);
        return shareIntent;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
