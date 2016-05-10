package com.example.alexandreroussiere.mymedialibrary;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by Alexandre Roussière on 24/04/2016.
 * Use to display the information of a movie found by the search activity
 */
public class FoundMovieDetailFragment extends Fragment {

    public final static String APP_PATH_SD_CARD = "/MediaLibrary_posters";


    private TextView title;
    private TextView original_title;
    private TextView director;
    private TextView year;
    private TextView synopsis;
    private TextView rate;
    private RatingBar ratingBar;
    private Button add_button;
    private Button back_button;
    private ScrollView scrollView;
    private ImageView poster;
    private SqlHelper db;
    private Movie movie;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.found_movie_detail_layout, container, false);

        //We get the movie object sent by the previous fragment
        Bundle args = getArguments();
        movie = args.getParcelable("movie");

        /* All the views */

        title = (TextView) view.findViewById(R.id.title);
        original_title = (TextView) view.findViewById(R.id.original_title);
        director = (TextView) view.findViewById(R.id.director);
        year = (TextView) view.findViewById(R.id.year);
        rate = (TextView) view.findViewById(R.id.rate);
        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar_rate);
        synopsis = (TextView) view.findViewById(R.id.synopsis);
        poster = (ImageView) view.findViewById(R.id.poster);
        scrollView = (ScrollView)view.findViewById(R.id.foundMovieDetail_view);
        back_button = (Button) view.findViewById(R.id.btn_back);
        add_button = (Button)view.findViewById(R.id.btn_add);

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
        //Change the title of the Toolbar
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Movie Detail");

        /* We set all the views of the layout with data from the movie object */
        title.setText(movie.getTitle());
        original_title.setText(movie.getOriginal_title());
        if (movie.getDirector() == null) {
            director.setText("Unknown");
        } else {
            director.setText(movie.getDirector());
        }
        year.setText("" + movie.getYear());
        rate.setText(String.format("%.2f", movie.getRate()));
        ratingBar.setRating((float) movie.getRate());
       // poster.setImageBitmap(movie.getPoster());
        Picasso.with(getActivity()).load(movie.getPosterURL()).into(poster);


        //Make the user able to scroll the text
        synopsis.setMovementMethod(new ScrollingMovementMethod());
        synopsis.setText(movie.getSynopsis());

        //If back button is clicked we go back to the previous fragment
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Button back clicked !", "");
                getFragmentManager().popBackStack();
            }
        });
        //If add button is clicked, we
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Button add clicked !", "");
                //If the movie is already in the library we don't add it again
                if (db.movieAlreadyInLibrary(movie)) {
                    Toast.makeText(getActivity(), "This movie is already in the library", Toast.LENGTH_LONG).show();
                } else {
                    //Add the movie to the database
                    db.addMovie(movie);
                    Toast.makeText(getActivity(), "Movie added to the library", Toast.LENGTH_LONG).show();
                    ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
                    //Return to the Library home fragment
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).
                            addToBackStack("Home").commit();
                }
            }
        });
    }


}
