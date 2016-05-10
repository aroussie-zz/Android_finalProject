package com.example.alexandreroussiere.mymedialibrary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Created by Alexandre Roussière on 19/04/2016.
 * This is the object where all the information for a movie are
 */
public class Movie implements Parcelable {

    private int id;
    private String title;
    private String original_title;
    private String posterURL;
    private int year;
    private String director;
    private String synopsis;
    private double rate;
    private double personal_rate;
    private boolean is_seen;
    private boolean is_favorite;
    private String date_added;

    /**
     *
     * Default constructor
     */
    public Movie(){
        is_favorite = false;
        is_seen = false;
    }

    /**
     * Constructor from a parcel
     * @param pc
     */
    public Movie(Parcel pc){

        id = pc.readInt();
        year = pc.readInt();
        rate = pc.readDouble();
        personal_rate = pc.readDouble();
        title = pc.readString();
        original_title = pc.readString();
        director = pc.readString();
        synopsis = pc.readString();
        posterURL = pc.readString();
        is_seen = pc.readByte() != 0;
        is_favorite = pc.readByte() != 0;

    }


    /* Getters and Setters */

    public void setId(int i){
        this.id = i;
    }
    public void setTitle(String str){
        this.title = str;
    }
    public void setOriginalTitle(String str){
        this.original_title = str;
    }
    public void setDirector(String str){
        this.director = str;
    }
    public void setSynopsis(String str){
        this.synopsis = str;
    }
    public void setPosterURL(String str){
        this.posterURL = str;
    }
    public void setYear(int i){
        this.year = i;
    }
    public void setRate(double i){
        this.rate = i;
    }
    public void setIsSeen(boolean i){
        this.is_seen = i;
    }
    public void setIsFavorite(boolean i){this.is_favorite = i; }
    public void setDate_added(String date){this.date_added = date;}



    int getId(){
        return id;
    }
    int getYear(){
        return year;
    }
    double getRate(){
        return rate;
    }
    String getTitle(){
        return title;
    }
    String getPosterURL(){
        return posterURL;
    }
    String getOriginal_title(){
        return original_title;
    }

   // Bitmap getPoster(){return poster;}
    String getDirector(){
        return director;
    }
    String getSynopsis(){
        return synopsis;
    }
    boolean getIsSeen() { return is_seen;}
    boolean getIsFavorite() {return is_favorite;}
    String getDate_added() {return date_added;}

    /* Parcelable parts */

    /** Used to give additional hints on how to process the received parcel.**/
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    //Write the movie attributes into the parcel
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id);
        dest.writeInt(year);
        dest.writeDouble(rate);
        dest.writeDouble(personal_rate);
        dest.writeString(title);
        dest.writeString(original_title);
        dest.writeString(director);
        dest.writeString(synopsis);
        dest.writeString(posterURL);
        dest.writeByte((byte) (is_seen ? 1 : 0));
        dest.writeByte((byte) (is_favorite ? 1 : 0));

    }


    /** Static field used to regenerate object, individually or as arrays */
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel pc) {
            return new Movie(pc);
        }
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };



}
