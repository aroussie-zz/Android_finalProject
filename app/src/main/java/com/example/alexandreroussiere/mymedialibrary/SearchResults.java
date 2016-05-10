package com.example.alexandreroussiere.mymedialibrary;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

/**
 * This is automatically called when the user enter a string into the SearchBar
 */
public class SearchResults extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //We build the toolbar
        actionBar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(actionBar);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_home_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search");

        // Create a new Fragment to be placed in the activity layout
        SearchMovieFragment searchFragment = new SearchMovieFragment();

        // Add the fragment to the 'fragment_container' FrameLayout
        FragmentTransaction ft =  getSupportFragmentManager().beginTransaction();

        //Check the activity is called from a callback or a new call
        //To know if we have to add the fragment or just replace the previous one
        if (findViewById(R.id.fragment_container) != null) {

            ft.replace(R.id.fragment_container, searchFragment);
            ft.addToBackStack("search");
            ft.commit();
        }else{
            ft.add(R.id.fragment_container, searchFragment);
            ft.addToBackStack("search");
            ft.commit();

        }

    }

}
