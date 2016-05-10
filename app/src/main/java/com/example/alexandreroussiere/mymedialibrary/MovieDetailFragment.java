package com.example.alexandreroussiere.mymedialibrary;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

/**
 * Created by Alexandre Roussière on 24/04/2016.
 * This is used to display the information of a movie from the movie library (home page)
 */
public class MovieDetailFragment extends Fragment {

    private static String URL = "https://api.betaseries.com/movies/movie?";
    private static String KEY = "key=CEE2694F0106";
    private static String VERSION = "v=2.4";

    private TextView title;
    private TextView original_title;
    private TextView director;
    private TextView year;
    private TextView synopsis;
    private TextView rate;
    private RatingBar ratingBar;
    private Button remove_button;
    private Button back_button;
    private ScrollView scrollView;
    private ImageView poster;
    private SqlHelper db;
    private ProgressDialog pDialog;
    private CheckBox checkbox_seen;
    private CheckBox checkbox_favorite;
    private Movie movie;
    private Boolean hasBeenUpdated;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.movie_detail_layout, container, false);

        /* This is to control the back button of the device */
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK  && event.getAction() == KeyEvent.ACTION_UP)
                {
                    //If data hasn't been updated we can't go back normaly
                    if(!hasBeenUpdated){
                        getFragmentManager().popBackStack();
                    }else{
                        //Otherwise, we have to update the data and replace the fragment
                        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).
                                addToBackStack("home").commit();

                    }
                    return true;
                }
                return false;
            }
        } );

        //We get the movie object sent by the previous fragment
        Bundle args = getArguments();
        movie = args.getParcelable("movie");

        /* All the views */
        checkbox_seen = (CheckBox) view.findViewById(R.id.checkBox_seen);
        checkbox_favorite = (CheckBox) view.findViewById(R.id.checkBox_favorite);
        title = (TextView) view.findViewById(R.id.title);
        original_title = (TextView) view.findViewById(R.id.original_title);
        director = (TextView) view.findViewById(R.id.director);
        year = (TextView) view.findViewById(R.id.year);
        rate = (TextView) view.findViewById(R.id.rate);
        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar_rate);
        synopsis = (TextView) view.findViewById(R.id.synopsis);
        poster = (ImageView) view.findViewById(R.id.poster);
        scrollView = (ScrollView)view.findViewById(R.id.movieDetail_view);
        back_button = (Button) view.findViewById(R.id.btn_back);
        remove_button = (Button)view.findViewById(R.id.btn_remove);

        //To be able to scroll the Synopsis TextView
        scrollView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                synopsis.getParent().requestDisallowInterceptTouchEvent(false);

                return false;
            }
        });


        synopsis.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                synopsis.getParent().requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        db = new SqlHelper(getActivity());
        //By default, nothing has been uptdated
        hasBeenUpdated = false;

        //Change the title of the toolbar
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Movie Detail");

        /* Set the checkboxes */
        if(movie.getIsSeen()){
            checkbox_seen.setChecked(true);
            checkbox_favorite.setEnabled(true);

        }else {
            checkbox_seen.setChecked(false);
            checkbox_favorite.setEnabled(false);

        }

        if(movie.getIsFavorite()){
            checkbox_favorite.setChecked(true);
        }else{
            checkbox_favorite.setChecked(false);
        }

        // If seen checkbox is clicked we want to update the database
        checkbox_seen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Log.d("checkBox_seen checked: ","Movie is seen");
                    movie.setIsSeen(true);
                    //If the movie has been seen, the user can add it to his favorite
                    checkbox_favorite.setEnabled(true);
                    //update the Database
                    db.updateSeenColumn(movie);
                    Toast.makeText(getActivity(),"The movie is seen",Toast.LENGTH_SHORT).show();
                    hasBeenUpdated = true;
                }
                else{
                    Log.d("checkBox_seen unchecked",": Movie is NOT seen");
                    movie.setIsSeen(false);
                    //We can't add in favorite a movie we haven't seen...
                    checkbox_favorite.setChecked(false);
                    checkbox_favorite.setEnabled(false);
                    //update the Database
                    db.updateSeenColumn(movie);
                    Toast.makeText(getActivity(),"The movie is not seen yet",Toast.LENGTH_SHORT).show();
                    hasBeenUpdated = true;

                }
            }
        });

        // If favorite checkbox is clicked we want to update the database
        checkbox_favorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Log.d("checkBox_favorite ","checked: Added to favorites");
                    movie.setIsFavorite(true);
                    //Update the Database
                    db.updateFavoriteColumn(movie);
                    Toast.makeText(getActivity(),"Added to favorite",Toast.LENGTH_SHORT).show();
                    hasBeenUpdated = true;

                }
                else{
                    Log.d("checkBox_favorite: " ,"unchecked: Movie is not favorite");
                    movie.setIsFavorite(false);
                    //Update the Database
                    db.updateFavoriteColumn(movie);
                    Toast.makeText(getActivity(),"Removed from favorites",Toast.LENGTH_SHORT).show();
                    hasBeenUpdated = true;

                }
            }
        });

        //Get the movie information
        new GetMovie().execute();

    }


    /**
     * Get all the movies in the background
     */
    private class GetMovie extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...information are loading");
            pDialog.setCancelable(false);
            pDialog.show();
        }


        @Override
        protected Void doInBackground(Void... params) {

            Log.d("Loading detail on: ",String.valueOf(movie.getId()));
            ServiceHandler sh = new ServiceHandler(movie);
            //build the URL for the request
            String url = URL + "id=" + movie.getId() + "&" + KEY +"&" + VERSION;
            try {
                //Try to get the movie information according to the url
                movie = sh.getJSONdata(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // Dismiss the progress dialog
            if (pDialog != null) {
                pDialog.dismiss();
                pDialog = null;
            }
            /* Set all the views with information about the movie */
            title.setText(movie.getTitle());
            original_title.setText( movie.getOriginal_title());
            if(movie.getDirector()==null){
                director.setText("Unknown");
            }else{
                director.setText( movie.getDirector());
            }
            year.setText(""+ movie.getYear());
            rate.setText(String.format("%.2f",movie.getRate()));
            ratingBar.setRating((float)movie.getRate());

        //    poster.setImageBitmap(movie.getPoster());
            Picasso.with(getActivity()).load(movie.getPosterURL()).into(poster);


            //Make the user able to scroll the text
            synopsis.setMovementMethod(new ScrollingMovementMethod());
            synopsis.setText(movie.getSynopsis());

            //If back button is clicked we go back to the previous fragment
            back_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Button back clicked !", "");
                    //If nothing has changed we can just go back
                    if(!hasBeenUpdated){
                        getFragmentManager().popBackStack();
                    }else{
                        //Otherwise we have to refresh the fragment
                        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).
                                addToBackStack("home").commit();
                    }


                }
            });
            //If "remove" button is clicked, we have to remove the movie from the DB
            remove_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Try to delete: ", movie.getTitle());
                    //We delete the picture from the SD card
                    File file = new File(movie.getPosterURL());
                    if(file.delete()){
                        //If the deletion of the picture works, we delete the object from the DB
                        db.deleteMovie(movie);
                        Toast.makeText(getActivity(),"Movie removed from the library",Toast.LENGTH_LONG).show();
                        Log.d("Movie ",movie.getTitle() + " deleted");
                        //Then we go back to the movie library home
                        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).
                                addToBackStack("home").commit();
                    }
                    else {
                        Log.e("Delete Bitmap on SDCard"," Fail !");
                    }

                }
            });

        }


    }


}
