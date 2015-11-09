package com.example.szkola.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callbacks {


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

    public static boolean mTwoPane;

    public static boolean ismTwoPane() {
        return mTwoPane;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.app.ActionBar menu = getSupportActionBar();
        if (menu != null) {
            menu.setDisplayShowHomeEnabled(true);
            menu.setLogo(R.mipmap.ic_launcher);
            menu.setDisplayUseLogoEnabled(true);
        }


        if (findViewById(R.id.fragment_detail_container) != null) {
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }
    }


    @Override
    public void onItemSelected(String id, String title, String user_rating, String plot, String r_date, String img_path) {

        Bundle extras = new Bundle();
        extras.putString("EXTRA_ID", id);
        extras.putString("EXTRA_TITLE", title);
        extras.putString("EXTRA_USER_RATING", user_rating);
        extras.putString("EXTRA_PLOT", plot);
        extras.putString("EXTRA_R_DATE", r_date);
        extras.putString("EXTRA_IMG", img_path);

        if (mTwoPane) {
            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(extras);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_detail_container, fragment)
                    .commit();
        }else{
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtras(extras);
            startActivity(intent);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);

            intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.SortPreferenceFragment.class.getName());
            intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
