package com.example.alexandreroussiere.mymedialibrary;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.astuetz.PagerSlidingTabStrip;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by Alexandre Roussière on 24/04/2016.
 * Use to manage the PageView
 */
public class ScreenSlidePageFragment extends Fragment {

    private View view;
    private ViewPager pager;
    private  PagerSlidingTabStrip tabs;
    private ArrayList<Movie> allMovies;
    private ArrayList<Movie> seenMovies;
    private ArrayList<Movie> unSeenMovies;
    private ArrayList<Movie> favoriteMovies;
    private SqlHelper db;
    private MyPagerAdapter pagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.movie_home_layout, container, false);

        // Initialize the ViewPager and set an adapter
        pager = (ViewPager) view.findViewById(R.id.vpPager);

        // Bind the tabs to the ViewPager
        tabs = (PagerSlidingTabStrip)view.findViewById(R.id.tabs);
        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        db =new SqlHelper(getActivity());
        /* Get all the different list of movies */
        try {
            allMovies = db.getAllMovies();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        seenMovies = db.getAllSeenMovies();
        unSeenMovies = db.getAllUnSeenMovies();
        favoriteMovies = db.getAllFavoriteMovies();

        //Limit the number of  next page I have to load when I'm at a page
        pager.setOffscreenPageLimit(1);


        if(pagerAdapter!=null){
            pagerAdapter.notifyDataSetChanged();
        }

        //Create the adapter for the pageView
        pagerAdapter = new MyPagerAdapter(getChildFragmentManager(),allMovies,seenMovies,unSeenMovies,favoriteMovies);
        //Set the adapter and tabs to the pageviewer
        pager.setAdapter(pagerAdapter);
        tabs.setViewPager(pager);

    }


    public  class MyPagerAdapter extends FragmentStatePagerAdapter {

        //Define the number of tabs(pages)
        private  int NUM_ITEMS = 4;

        //Create a List with all the list of movies
        private ArrayList<ArrayList<Movie>>  allLists = new ArrayList<>();

        //define the the title of the tabs
        private final String[] TITLES = {"All", "Seen","unSeen","Favorites"};

        public MyPagerAdapter(FragmentManager fragmentManager,ArrayList<Movie> all,ArrayList<Movie> seen,
                              ArrayList<Movie> unSeen,ArrayList<Movie> favorites) {
            super(fragmentManager);
            //Put each list of movies in the global one
            allLists.add(0,all);
            allLists.add(1,seen);
            allLists.add(2,unSeen);
            allLists.add(3,favorites);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for a particular page
        @Override
        public Fragment getItem(int position) {

            Log.d("Position: ",""+ position);
            switch (position) {
                case 0: return DefaultMovieListFragment.newInstance(allLists.get(position));
                case 1: return DefaultMovieListFragment.newInstance(allLists.get(position));
                case 2: return DefaultMovieListFragment.newInstance(allLists.get(position));
                case 3: return DefaultMovieListFragment.newInstance(allLists.get(position));

                default: return DefaultMovieListFragment.newInstance(allMovies);

            }
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }


        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position] + " (" + allLists.get(position).size() +")";

        }

        //this is called when notifyDataSetChanged() is called
        @Override
        public int getItemPosition(Object object) {
            // refresh all fragments when data set changed
            return PagerAdapter.POSITION_NONE;
        }

    }



}

