package com.example.alexandreroussiere.mymedialibrary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Alexandre Roussière on 22/04/2016.
 * This is the class which deals with the SQLite database
 */
public class SqlHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "MediaLibraryDB";

    // Books table name
    private static final String TABLE_MOVIE = "movie";
    private static final String TABLE_MOVIE_LIBRARY = "movie_library";

    // Books Table Columns names
    private static final String KEY_ID_MOVIE = "id_movie";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DIRECTOR = "director";
    private static final String KEY_POSTER_URL = "poster_url";
    private static final String KEY_YEAR = "year";
    private static final String KEY_DATE_ADDED = "date_added";
    private static final String KEY_IS_SEEN = "is_seen";
    private static final String KEY_IS_FAVORITE = "is_favorite";

    public SqlHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        // SQL statement to create movie table
        String CREATE_MOVIE_TABLE = "CREATE TABLE movie ( " +
                "id INTEGER PRIMARY KEY, " +
                "title TEXT, "+
                "director TEXT, "+
                "poster_url TEXT, "+
                "year INTEGER, "+
                "date_added TEXT, "+
                "is_seen INTEGER, "+
                "is_favorite INTEGER)";

        // SQL statement to create movie_library table
        String CREATE_MOVIE_LIBRARY_TABLE = "CREATE TABLE movie_library ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_movie INTEGER, "+
                "FOREIGN KEY(id_movie) REFERENCES movie(id)) ";

        //create movie and movie_library tables
        db.execSQL(CREATE_MOVIE_TABLE);
        db.execSQL(CREATE_MOVIE_LIBRARY_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS movie");
        db.execSQL("DROP TABLE IF EXISTS movie_library");

        // create fresh books table
        this.onCreate(db);

    }

    /*CRUD operations (create "add", read "get", update, delete) */

    /**
     * Add a movie to the Database
     * @param movie Movie object
     */
    public void addMovie(Movie movie){
        Log.d("addMovie : ", movie.getTitle());

        // get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // set the format to sql date time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date_added = new Date();

        // create ContentValues to add key "column"/value into movie table
        ContentValues values = new ContentValues();
        values.put(KEY_ID, movie.getId()); // get ID
        values.put(KEY_TITLE, movie.getTitle()); // get title
        values.put(KEY_DIRECTOR, movie.getDirector()); // get title
        values.put(KEY_POSTER_URL, movie.getPosterURL()); // get title
        values.put(KEY_YEAR, movie.getYear()); // get title
        values.put(KEY_DATE_ADDED, dateFormat.format(date_added)); // get title
        values.put(KEY_IS_SEEN, movie.getIsSeen() ? 1 : 0); // get title
        values.put(KEY_IS_FAVORITE, movie.getIsFavorite() ? 1 : 0); // get title

        //Values for movie_library table
        ContentValues values_library = new ContentValues();
        values_library.put(KEY_ID_MOVIE,movie.getId());

        // insert
        db.insert(TABLE_MOVIE, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/values

        db.insert(TABLE_MOVIE_LIBRARY,null,values_library);

        Log.d("Movie added: ",movie.getTitle());
        // 4. Close dbase
        db.close();
    }

    /**
     * Get all the movies from the Database
     * @return a list of movies
     * @throws ParseException
     */
    public ArrayList<Movie> getAllMovies() throws ParseException {

        ArrayList<Movie>listMovies =  new ArrayList<Movie>();
        //Get the format we want for the Date
        SimpleDateFormat getDateFormat = new SimpleDateFormat("MM-dd-yyyy");
        SimpleDateFormat addDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Log.d("Try to get all movies","");
        //The query to execute
        String query = "SELECT movie.id,movie.title,movie.director,movie.poster_url,movie.year,movie.date_added," +
                "movie.is_seen,movie.is_favorite " +
                "FROM movie, movie_library "+
                "WHERE movie.id = movie_library.id_movie "+
                "ORDER by movie.date_added DESC";

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        Movie movie= null;
        //If there is a result to the query we create a movie with the information from the table
        if(cursor.moveToFirst()){
            do{
                movie = new Movie();
                movie.setId(Integer.parseInt(cursor.getString(0)));
                movie.setTitle(cursor.getString(1));
                movie.setDirector(cursor.getString(2));
                movie.setPosterURL(cursor.getString(3));
                movie.setYear(Integer.parseInt(cursor.getString(4)));
                movie.setDate_added(getDateFormat.format(addDateFormat.parse(cursor.getString(5))));
                movie.setIsSeen((Integer.parseInt(cursor.getString(6)) != 0));
                movie.setIsFavorite((Integer.parseInt(cursor.getString(7)) != 0));

                //Add the movie to the list of movies
                listMovies.add(movie);
            }while(cursor.moveToNext());
        }

        Log.d("Success to get movies","");

        return listMovies;

    }

    /**
     * Delete a movie from the Database
     * @param movie
     */
    public void deleteMovie(Movie movie) {

        // get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        //delete
        db.delete(TABLE_MOVIE_LIBRARY,
                KEY_ID_MOVIE+" = ?",
                new String[] { String.valueOf(movie.getId()) });

        db.delete(TABLE_MOVIE,
                KEY_ID+" = ?",
                new String[] { String.valueOf(movie.getId()) });

        // close
        db.close();

        Log.d("deleteBook", movie.getTitle());
    }

    /**
     * Update the column is_seen of movie table
     * @param movie
     */
    public void updateSeenColumn(Movie movie){

        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE movie SET is_seen=" + (movie.getIsSeen() ? 1 : 0) + " where id=" + movie.getId();
        db.execSQL(query);
        Log.d("UpdateSeenColumn: ", "column updated with: " + (movie.getIsSeen() ? 1 : 0) );
    }

    /**
     * Update the column is_favorite of movie table
     * @param movie
     */
    public void updateFavoriteColumn(Movie movie){

        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE movie SET is_favorite=" + (movie.getIsFavorite() ? 1 : 0) + " where id=" + movie.getId();
        db.execSQL(query);
        Log.d("UpdateFavoriteColumn: ", "column updated with: " + (movie.getIsSeen() ? 1 : 0) );
    }



    /*Search functions */

    /**
     * Check if a movie is already in the database
     * @param movie
     * @return a boolean
     */
    public boolean movieAlreadyInLibrary(Movie movie){

        SQLiteDatabase db = this.getWritableDatabase();

        String query="SELECT * FROM movie_library WHERE id_movie=" + movie.getId();
        Cursor cursor = db.rawQuery(query,null);
        //If the cursor has at least one row, we return true
        if(cursor.moveToFirst()){
            return true;
        }else
            return false;
    }

    /**
     * Return all the favorite films
     * @return a list of movies
     */
    public ArrayList<Movie> getAllFavoriteMovies(){

        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Movie> favoriteMovies = new ArrayList<Movie>();
        //Get the format we want for the date
        SimpleDateFormat getDateFormat = new SimpleDateFormat("MM-dd-yyyy");
        SimpleDateFormat addDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String query = "SELECT movie.id,movie.title,movie.director,movie.poster_url,movie.year,movie.date_added," +
                "movie.is_seen,movie.is_favorite " +
                "FROM movie, movie_library "+
                "WHERE movie.id = movie_library.id_movie AND movie.is_favorite=1 "+
                "ORDER by movie.date_added DESC";

        Cursor cursor = db.rawQuery(query,null);

        Movie movie= null;
        //If there is a result to the query we create a movie with the information from the table
        if(cursor.moveToFirst()){
            do{
                movie = new Movie();
                movie.setId(Integer.parseInt(cursor.getString(0)));
                movie.setTitle(cursor.getString(1));
                movie.setDirector(cursor.getString(2));
                movie.setPosterURL(cursor.getString(3));
                movie.setYear(Integer.parseInt(cursor.getString(4)));
                try {
                    movie.setDate_added(getDateFormat.format(addDateFormat.parse(cursor.getString(5))));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                movie.setIsSeen((Integer.parseInt(cursor.getString(6)) != 0));
                movie.setIsFavorite((Integer.parseInt(cursor.getString(7)) != 0));

                //Add the movie to the list of movies
                favoriteMovies.add(movie);
            }while(cursor.moveToNext());
        }

        return favoriteMovies;
    }

    /**
     * Return all the movies seen
     * @return A list of movies
     */
    public ArrayList<Movie> getAllSeenMovies(){

        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Movie> seenMovies = new ArrayList<Movie>();
        //Get the format we want for the date
        SimpleDateFormat getDateFormat = new SimpleDateFormat("MM-dd-yyyy");
        SimpleDateFormat addDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String query = "SELECT movie.id,movie.title,movie.director,movie.poster_url,movie.year,movie.date_added," +
                "movie.is_seen,movie.is_favorite " +
                "FROM movie, movie_library "+
                "WHERE movie.id = movie_library.id_movie AND movie.is_seen=1 "+
                "ORDER by movie.date_added DESC";

        Cursor cursor = db.rawQuery(query,null);

        Movie movie= null;
        //If there is a result to the query we create a movie with the information from the table
        if(cursor.moveToFirst()){
            do{
                movie = new Movie();
                movie.setId(Integer.parseInt(cursor.getString(0)));
                movie.setTitle(cursor.getString(1));
                movie.setDirector(cursor.getString(2));
                movie.setPosterURL(cursor.getString(3));
                movie.setYear(Integer.parseInt(cursor.getString(4)));
                try {
                    movie.setDate_added(getDateFormat.format(addDateFormat.parse(cursor.getString(5))));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                movie.setIsSeen((Integer.parseInt(cursor.getString(6)) != 0));
                movie.setIsFavorite((Integer.parseInt(cursor.getString(7)) != 0));

                //Add the movie to the list of movies
                seenMovies.add(movie);
            }while(cursor.moveToNext());
        }

        return seenMovies;
    }

    /**
     * Return all the movies unseen
     * @return A list of movies
     */
    public ArrayList<Movie> getAllUnSeenMovies(){

        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Movie> unSeenMovies = new ArrayList<Movie>();
        //Get the format we want for the date
        SimpleDateFormat getDateFormat = new SimpleDateFormat("MM-dd-yyyy");
        SimpleDateFormat addDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String query = "SELECT movie.id,movie.title,movie.director,movie.poster_url,movie.year,movie.date_added," +
                "movie.is_seen,movie.is_favorite " +
                "FROM movie, movie_library "+
                "WHERE movie.id = movie_library.id_movie AND movie.is_seen=0 "+
                "ORDER by movie.date_added DESC";

        Cursor cursor = db.rawQuery(query,null);

        Movie movie= null;
        //If there is a result to the query we create a movie with the information from the table
        if(cursor.moveToFirst()){
            do{
                movie = new Movie();
                movie.setId(Integer.parseInt(cursor.getString(0)));
                movie.setTitle(cursor.getString(1));
                movie.setDirector(cursor.getString(2));
                movie.setPosterURL(cursor.getString(3));
                movie.setYear(Integer.parseInt(cursor.getString(4)));
                try {
                    movie.setDate_added(getDateFormat.format(addDateFormat.parse(cursor.getString(5))));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                movie.setIsSeen((Integer.parseInt(cursor.getString(6)) != 0));
                movie.setIsFavorite((Integer.parseInt(cursor.getString(7)) != 0));

                //Add the movie to the list of movies
                unSeenMovies.add(movie);
            }while(cursor.moveToNext());
        }

        return unSeenMovies;
    }


}
