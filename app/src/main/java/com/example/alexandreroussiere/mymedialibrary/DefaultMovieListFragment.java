package com.example.alexandreroussiere.mymedialibrary;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;


/**
 * Created by Alexandre Roussière on 23/04/2016.
 * This is the fragment use for the pages of the ViewPager
 */
public class DefaultMovieListFragment extends ListFragment {

    private ArrayList<Movie> listMovies;
    private ListView mListView;
    private Context context;
    private Button btn_prev;
    private Button btn_next;
    private LinearLayout listFooterView;
    private TextView emptyText;

    private int pageCount;
    private int increment = 0;

    public int NUM_MOVIE_PAGE = 5;
    public int TOTAL_MOVIES;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.default_movie_list_layout, container, false);
        //Get the layout of the custom footer for the ListView
        listFooterView = (LinearLayout) inflater.inflate(
                R.layout.listmovie_footer_layout, null);

        btn_next = (Button) listFooterView.findViewById(R.id.btn_next);
        btn_prev = (Button) listFooterView.findViewById(R.id.btn_previous);

        //This will be the view display when the ListView is empty
        emptyText = (TextView)view.findViewById(android.R.id.empty);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        context = getActivity();

        //Change the title of the Toolbar
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Movie Library");


        mListView = getListView();
        //Get the list of movies to display
        listMovies = getListMovies();

        //If the list is not empty, we add the buttons for navigation
        if(!listMovies.isEmpty()){
            mListView.addFooterView(listFooterView);
        }

        //We get the number of movies
        TOTAL_MOVIES = listMovies.size();

        /* Check number of the page needed to display those movies */
        int val = TOTAL_MOVIES % NUM_MOVIE_PAGE;
        val = val == 0?0:1;
        pageCount = TOTAL_MOVIES / NUM_MOVIE_PAGE + val;

        //We check if we have to enable/disable buttons
        CheckEnable();

        /* Pagination stuff  */
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Button next ", "clicked !");
                increment++;
                loadList(increment);
                CheckEnable();
            }
        });

        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Button prev ", "clicked !");
                increment--;
                loadList(increment);
                CheckEnable();
            }
        });

        //We only display the number of movies of the 1 page whose number is "increment"
        loadList(increment);

    }

    /**
     * Enable or disable the navigation buttons depending on which page we are on
     */
    private void CheckEnable(){
        if(increment+1 == pageCount && pageCount>1){
            btn_next.setEnabled(false);
            btn_prev.setEnabled(true);
        }else if(increment == 0 && pageCount>1){
            btn_prev.setEnabled(false);
            btn_next.setEnabled(true);
        }else if(increment == 0 && pageCount <= 1) {
            btn_prev.setEnabled(false);
            btn_next.setEnabled(false);
        }
        else{
            btn_prev.setEnabled(true);
            btn_next.setEnabled(true);
        }


}

    /**
     * Load data in the listView
     * @param number
     */
    private void loadList(final int number){

        ArrayList<Movie> sort = new ArrayList<Movie>();

        int start = number * NUM_MOVIE_PAGE;
        for(int i=start;i<(start) + NUM_MOVIE_PAGE;i++){
            //Check that we are not at the last movie object of the list
            if(i<listMovies.size()){
                sort.add(listMovies.get(i));
            }else{
                break;
            }
        }

        mListView.setAdapter(new MovieAdapter(context,sort));
        //When a movie is clicked, we launch a new activity to show its detail
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {

        Bundle args = new Bundle();
                args.putParcelable("movie", listMovies.get(position + NUM_MOVIE_PAGE * number));
                MovieDetailFragment fragment = new MovieDetailFragment();

                //Send the whole movie object to the next activity
                fragment.setArguments(args);
                //Launch the fragment, and add it at the top of the stack
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).
                        addToBackStack("movieDetail").commit();
            }
        });
    }

    /**
     * Create the fragment with the list of movies to display in it
     * @param movies list of movie objects
     * @return the fragment
     */
    public static DefaultMovieListFragment newInstance(ArrayList<Movie> movies) {

        DefaultMovieListFragment f = new DefaultMovieListFragment();
        f.setListMovies(movies);
        return f;
    }

    /* Getter and setter for the list of movies */
    private void setListMovies(ArrayList<Movie> movies){ listMovies = movies;  }
    ArrayList<Movie> getListMovies (){return listMovies;}

}