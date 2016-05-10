package com.example.alexandreroussiere.mymedialibrary;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Alexandre Roussière on 21/04/2016.
 * Use to display a list of movie
 */
public class MovieAdapter extends BaseAdapter {

    private LayoutInflater mInflator;
    private Context mContext;
    private ArrayList<Movie> listMovies;
    private TextView title;
    private TextView year;
    private TextView director;
    private TextView date;
    private ImageView posterView;


    /**
     * Constructor
     * @param c the Context
     * @param listOfMovies
     */
    public MovieAdapter(Context c,ArrayList<Movie> listOfMovies) {
        mContext = c;
        mInflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listMovies = listOfMovies;
        Log.i("Main Activity process: ", "New adapter create");
    }

    @Override
    public int getCount() {
        return listMovies.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.movie_list_row_layout, null);
        }

        title = (TextView) convertView.findViewById(R.id.title);
        title.setText("Title: " + listMovies.get(position).getTitle());

        year = (TextView) convertView.findViewById(R.id.year);
        year.setText("Year: " + listMovies.get(position).getYear());

        director = (TextView) convertView.findViewById(R.id.director);
        if(listMovies.get(position).getDirector()==null){
            director.setText("Director: Unknown");
        }else{
            director.setText("Director: " + listMovies.get(position).getDirector());
        }

        posterView = (ImageView) convertView.findViewById(R.id.poster);
        Picasso.with(mContext).load(listMovies.get(position).getPosterURL()).into(posterView);

        date = (TextView) convertView.findViewById(R.id.date);
        if(listMovies.get(position).getDate_added()!=null){
            date.setText("Added: " + listMovies.get(position).getDate_added().toString());
        }

        return convertView;

    }


}
