package com.example.alexandreroussiere.mymedialibrary;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Alexandre Roussière on 24/04/2016.
 * Called to display the movies found by the research
 */
public class SearchMovieFragment extends ListFragment {

    private View view;
    private static String URL = "http://api.betaseries.com/movies/search";
    private static String KEY = "key=CEE2694F0106";
    private static String VERSION = "v=2.4";

    private ProgressDialog pDialog;
    private ListView mListView;
    private ArrayList<Movie> listMovies = new ArrayList<Movie>();
    private String currentQuery="";
    private String previousQuery=" ";
    private TextView emptyText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.default_movie_list_layout, container, false);
        //This is the view which will be displayed if the listView is null
        emptyText = (TextView)view.findViewById(android.R.id.empty);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        mListView = getListView();
        super.onActivityCreated(savedInstanceState);

        handleIntent(getActivity().getIntent());

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    /**
     * display the movies found by the SearchResults Activity
     * @param intent
     */
    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            //We get the query of the search
            currentQuery = intent.getStringExtra(SearchManager.QUERY);

            //If the query or different we do a new research.
            //Otherwise it means that the fragment is just called back.
            Log.d("Current query: ",currentQuery);
            Log.d("Previous query: ",previousQuery);
            if(currentQuery != previousQuery){
                //use the query to search the movies
                new GetFoundMovies().execute();
            }else{
                //We call the adapter to display the movies
                mListView.setAdapter(new MovieAdapter(getActivity(),listMovies));

                //When a movie is clicked, we launch a new fragment to show its detail
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView parent, View view, int position, long id) {
                        Bundle args = new Bundle();
                        //We send the movie to the next fragment
                        args.putParcelable("movie", listMovies.get(position));
                        FoundMovieDetailFragment fragment = new FoundMovieDetailFragment();
                        fragment.setArguments(args);
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).
                                addToBackStack("foundMovieDetail").commit();
                    }
                });
            }
        }
    }

    /**
     * Get the movies according to the query
     */
    private class GetFoundMovies extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            //We update the previous query wth the new one
            previousQuery = currentQuery;

            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...Looking for your movie");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {

            //We instantiate the handler for the research
            SearchHandler sh = new SearchHandler();
            try {
                //We encore the query with a good format
                currentQuery = URLEncoder.encode(currentQuery, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //We build the whole url
            String url = URL + "?title=" + currentQuery + "&" + KEY + "&" + VERSION;
            //Making the request and getting the response
            try {
                //We try to get movies from the URL (request the API)
                listMovies = sh.getMoviesFound(url);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);

            //Close the progress dialog
            pDialog.dismiss();

            //If no movies has been found we display a message
            if(listMovies.isEmpty()){

                Toast.makeText(getActivity(),"No Movies Found...",Toast.LENGTH_LONG).show();

            }
            //We call the adapter to display the movies in the listView
            mListView.setAdapter(new MovieAdapter(getActivity(),listMovies));


            //When a movie is clicked, we launch a new fragment to show its detail
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    Bundle args = new Bundle();
                    //We send the movie object to the next fragment
                    args.putParcelable("movie", listMovies.get(position));
                    FoundMovieDetailFragment fragment = new FoundMovieDetailFragment();
                    fragment.setArguments(args);
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).
                            addToBackStack("foundMovieDetail").commit();
                }
            });
        }
    }



}
