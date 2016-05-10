package com.example.alexandreroussiere.mymedialibrary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by Alexandre Roussière on 4/18/2016.
 * Will be used to do an HTTP request and get back the movies
 */
public class ServiceHandler {

    //JSON node names
    private static final String TAG_TITLE="title";
    private static final String TAG_YEAR="production_year";
    private static final String TAG_DIRECTOR="director";
    private static final String TAG_POSTER="poster";
    private static final String TAG_ORIGINAL_TITLE="original_title";
    private static final String TAG_ID="id";
    private static final String TAG_SYNOPSIS="synopsis";
    private static final String TAG_RATE="notes";



    private Movie movie;

    /**
     * Default Constructor
     */
    public ServiceHandler(Movie m) {
        movie = m;
    }

    /**
     * Do the HTTP request and return the list with the movie(s)
     * @param url1 The url of the request
     * @return The list with all the movies
     */
    public Movie getJSONdata(String url1) throws IOException {

        URL url = new URL(url1);
        Log.d("Url is: ",url.toString());

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try{
            InputStream in = new BufferedInputStream((connection.getInputStream()));
            movie = readJsonStream(in);
        }
        finally {
            connection.disconnect();
        }

        return movie;
    }


    /**
     * Read all the JSON file
     * @param in the Input Stream
     * @return A list of Movies
     * @throws IOException
     */
    public Movie readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readMovieObject(reader);

        }finally {
            reader.close();
        }
    }

    /**
     * Real all the JSON object to find the Movie objects
     * @param reader
     * @return The list of Movies
     * @throws IOException
     */
    public Movie readMovieObject(JsonReader reader) throws IOException{

        reader.beginObject();
        while (reader.hasNext()){
            if(reader.nextName().equals("movie")){
                movie = readMovie(reader);
            }
            else{
                reader.beginArray();
                while(reader.hasNext()){
                    String name = reader.nextName();
                    if(name.equals("text")){
                        String error = reader.nextString();
                        Log.e("Error when searching : ", error);
                    }else{
                        reader.skipValue();
                    }
                }
                reader.endArray();
            }
        }
        reader.endObject();
        return movie;
    }

    /**
     * Get through all the movie object and get information
     * @param reader
     * @return a Movie object
     * @throws IOException
     */
    public Movie readMovie (JsonReader reader) throws IOException{


            //We begin to read the movie JSON Object
            reader.beginObject();
            //While there are tags, we go over them
            while(reader.hasNext()){
                String name = reader.nextName();

                if(name.equals(TAG_ORIGINAL_TITLE) && reader.peek() != JsonToken.NULL){
                    movie.setOriginalTitle(reader.nextString());
                }

                else if(name.equals(TAG_SYNOPSIS) && reader.peek() != JsonToken.NULL){
                    movie.setSynopsis(reader.nextString());
                }

                else if(name.equals(TAG_RATE)){
                    reader.beginObject();
                    while(reader.hasNext()){
                        name=reader.nextName();
                        if(name.equals("mean")){
                            movie.setRate(reader.nextDouble());
                        }
                        else{
                            reader.skipValue();
                        }
                    }
                    reader.endObject();
                }
                //The other tags don't matter so we skip them
                else{
                    reader.skipValue();
                }
            }
            reader.endObject();

        return movie;
    }




}
