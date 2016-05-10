package com.example.alexandreroussiere.mymedialibrary;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import java.util.Calendar;

/**
 * This is the movie Library home
 */
public class MainActivity extends AppCompatActivity {


    protected Toolbar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        /* Get the toolbar and custom it */
        actionBar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(actionBar);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_home_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Movie Home");


        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything
            if (savedInstanceState != null) {

                return;
            }

            // Create a Screen slide fragment to be placed in the activity layout
            ScreenSlidePageFragment firstFragment = new ScreenSlidePageFragment();

            // Add the fragment to the 'fragment_container' FrameLayout
            FragmentTransaction ft =  getSupportFragmentManager().beginTransaction();
            ft.add(R.id.fragment_container, firstFragment);
            ft.commit();
        }
    }


    @Override
    /**
     * Create the menu in the toolbar
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        //The menu item are loading from the resource file
        inflater.inflate(R.menu.menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        //Manage the search item in the toolbar
        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    /**
     * Manage the navigation of the app in the toolbar
     */
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent i = new Intent(MainActivity.this,MainActivity.class);
                startActivity(i);

            case R.id.action_movie_library:
                Intent home = new Intent(MainActivity.this, MainActivity.class);
                startActivity(home);
        }
        return super.onOptionsItemSelected(item);
    }


}
