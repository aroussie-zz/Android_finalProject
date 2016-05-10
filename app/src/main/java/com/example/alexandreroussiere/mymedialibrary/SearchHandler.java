package com.example.alexandreroussiere.mymedialibrary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Alexandre Roussière on 20/04/2016.
 * Use to get different movie objects from a JSON answer
 */
public class SearchHandler {

    //JSON node names
    private static final String TAG_TITLE="title";
    private static final String TAG_YEAR="production_year";
    private static final String TAG_DIRECTOR="director";
    private static final String TAG_POSTER="poster";
    private static final String TAG_ORIGINAL_TITLE="original_title";
    private static final String TAG_ID="id";
    private static final String TAG_SYNOPSIS="synopsis";
    private static final String TAG_RATE="notes";

    private ArrayList<Movie> listMovies;

    /**
     * Default Constructor
     */
    public SearchHandler() {
        listMovies = new ArrayList<Movie>();
    }

    /**
     * Do the HTTP request and return the list with the movie(s)
     * @param url1 The url of the request
     * @return The list with all the movies
     */
    public ArrayList<Movie> getMoviesFound(String url1) throws IOException {

        URL url = new URL(url1);
        Log.d("Url is: ",url.toString());

        //Make the internet connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try{
            InputStream in = new BufferedInputStream((connection.getInputStream()));
            listMovies = readJsonStream(in);
        }
        finally {
            connection.disconnect();
        }

        return listMovies;
    }


    /**
     * Read all the JSON file
     * @param in the Input Stream
     * @return A list of Movies
     * @throws IOException
     */
    public ArrayList<Movie> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            //This will return a list of moves
            return readMoviesObject(reader);

        }finally {
            reader.close();
        }
    }

    /**
     * Real all the JSON object to find the different Movie objects
     * @param reader
     * @return The list of Movies
     * @throws IOException
     */
    public ArrayList<Movie> readMoviesObject(JsonReader reader) throws IOException{

        /* We go over the file, detects the JSON objects or JSON arrays */
        reader.beginObject();
        while (reader.hasNext()){
            //This is the beginning of a movie JSON object
            if(reader.nextName().equals("movies")){
                reader.beginArray();
                while(reader.hasNext()){
                    //We add to the list of movies the JSON movie object
                    listMovies.add(readMovie(reader));
                }
                reader.endArray();
            }
            else{
                //This is the JSON array object if errors
                reader.beginArray();
                while(reader.hasNext()){
                    String name = reader.nextName();
                    if(name.equals("text")){
                        //We get the error
                        String error = reader.nextString();
                        Log.e("Error when searching : ", error);
                    }else{
                        reader.skipValue();
                    }
                }
                reader.endArray();
            }
        }
        //End the reading of the JSON object
        reader.endObject();
        //Return the list of movies
        return listMovies;
    }



    /**
     * Get through all the movie object and get information about all the film
     * @param reader
     * @return a Movie object
     * @throws IOException
     */
    public Movie readMovie (JsonReader reader) throws IOException{

        Movie movie = new Movie();

        //We begin to read the movie JSON Object
        reader.beginObject();
        //While there are tags, we go over them
        while(reader.hasNext()){
            String name = reader.nextName();
            if(name.equals(TAG_DIRECTOR) && reader.peek() != JsonToken.NULL){
                movie.setDirector(reader.nextString());
            }
            else if(name.equals(TAG_ID)){
                movie.setId(reader.nextInt());
            }
            else if(name.equals(TAG_ORIGINAL_TITLE )&& reader.peek() != JsonToken.NULL){
                movie.setOriginalTitle(reader.nextString());
            }
            else if(name.equals(TAG_POSTER) && reader.peek() != JsonToken.NULL){
                String str = reader.nextString();
                movie.setPosterURL(str);
            }
            else if(name.equals(TAG_SYNOPSIS) && reader.peek() != JsonToken.NULL){
                movie.setSynopsis(reader.nextString());
            }
            else if(name.equals(TAG_TITLE) && reader.peek() != JsonToken.NULL){
                movie.setTitle(reader.nextString());
            }
            else if(name.equals(TAG_YEAR) ){
                movie.setYear(reader.nextInt());
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

