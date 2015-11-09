package com.example.szkola.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_detail_container, new DetailActivityFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //this section prevent the main activity from being recreated when
        //a user navigates back to it
        //no additional api calls occur and position within the list view is maintained
        //the exception is 'favourites' where the view needs to be updated every time a user
        //removes a movie
        if (id == android.R.id.home) {
            String preferences = PreferenceManager.getDefaultSharedPreferences(this).
                    getString("sort_list", "default");


            Intent upIntent = new Intent(this, MainActivity.class);
            switch (preferences) {
                case ("favourites"):
                    NavUtils.navigateUpTo(this, upIntent);
                    break;
                default:
                    if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                        NavUtils.navigateUpTo(this, upIntent);
                        finish();
                    } else {
                        finish();
                    }
            }
            return true;

        }

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
