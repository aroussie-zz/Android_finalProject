package com.example.alexandreroussiere.mymedialibrary;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * Created by Alexandre Roussière on 26/04/2016.
 * Used to show the information about the movie when the user clicks on the notification he got
 */
public class NotifMovieDetail extends AppCompatActivity {

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
    private Button home_button;
    private ScrollView scrollView;
    private ImageView poster;
    private SqlHelper db;
    private CheckBox checkbox_seen;
    private CheckBox checkbox_favorite;
    private Movie movie;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.notif_movie_detail_layout);

        //Get the movie from the notification
        Intent notif = getIntent();
        movie = notif.getParcelableExtra("movie");

        Log.d("NotifMovieDetail: ","Title of the movie: " + movie.getTitle());


        /* All the views */
        checkbox_seen = (CheckBox) findViewById(R.id.checkBox_seen);
        checkbox_favorite = (CheckBox) findViewById(R.id.checkBox_favorite);
        title = (TextView) findViewById(R.id.title);
        original_title = (TextView) findViewById(R.id.original_title);
        director = (TextView) findViewById(R.id.director);
        year = (TextView) findViewById(R.id.year);
        rate = (TextView) findViewById(R.id.rate);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar_rate);
        synopsis = (TextView) findViewById(R.id.synopsis);
        poster = (ImageView) findViewById(R.id.poster);
        scrollView = (ScrollView)findViewById(R.id.movieDetail_view);
        home_button = (Button) findViewById(R.id.btn_home);

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

        db = new SqlHelper(getApplicationContext());

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
                    Toast.makeText(getApplicationContext(),"The movie is seen",Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.d("checkBox_seen unchecked",": Movie is NOT seen");
                    movie.setIsSeen(false);
                    //We can't add in favorite a movie we haven't seen...
                    checkbox_favorite.setChecked(false);
                    checkbox_favorite.setEnabled(false);
                    //update the Database
                    db.updateSeenColumn(movie);
                    Toast.makeText(getApplicationContext(),"The movie is not seen yet",Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(getApplicationContext(),"Added to favorite",Toast.LENGTH_SHORT).show();

                }
                else{
                    Log.d("checkBox_favorite: " ,"unchecked: Movie is not favorite");
                    movie.setIsFavorite(false);
                    //Update the Database
                    db.updateFavoriteColumn(movie);
                    Toast.makeText(getApplicationContext(),"Removed from favorites",Toast.LENGTH_SHORT).show();

                }
            }
        });

        //If Home button clicked, the user go to the movie list view
        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Button Home clicked !", "");
                Intent home = new Intent(NotifMovieDetail.this,MainActivity.class);
                startActivity(home);
                //We kill this activity
                finish();

                }

        });

        new GetMovie().execute();

    }


    /**
     * Get all the movies in the background
     */
    private class GetMovie extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
            Picasso.with(getApplicationContext()).load(movie.getPosterURL()).into(poster);


            //Make the user able to scroll the text
            synopsis.setMovementMethod(new ScrollingMovementMethod());
            synopsis.setText(movie.getSynopsis());


        }

    }
}
