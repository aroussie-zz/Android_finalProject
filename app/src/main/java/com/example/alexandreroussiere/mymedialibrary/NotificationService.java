package com.example.alexandreroussiere.mymedialibrary;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import java.util.ArrayList;
import java.util.Random;


/**
 * Created by Alexandre Roussière on 26/04/2016.
 * Used to send a notification to the user
 */
public class NotificationService extends Service {

    private SqlHelper db;
    private ArrayList<Movie> unSeenMovies;
    private int ID_NOTIF = 2;
    private Random randomGenerator;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){

        db = new SqlHelper(getApplicationContext());
        //We get the unseen movies list
        unSeenMovies = db.getAllUnSeenMovies();
        //Generate a random number between 0 and the size of the list
        randomGenerator = new Random();
        int randomIndex = randomGenerator.nextInt(unSeenMovies.size());
        Log.d("Notification: ", "Notification is preparing");

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //Create a notification Manager
        NotificationManager mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        //Create the intent for the activity launched when the user clicks on the notification
        Intent showMovie = new Intent(this.getApplicationContext(), NotifMovieDetail.class);
        //The intent will send information about a random movie taken from the unseen movie list
        showMovie.putExtra("movie",unSeenMovies.get(randomIndex));
        Log.d("Notification : ", "Title of the movie sent: " + unSeenMovies.get(randomIndex).getTitle());
        //The FLAG_UPDATE_CURRENT is to update the previous data sent to the activity.
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, showMovie, PendingIntent.FLAG_UPDATE_CURRENT);

        //Create the notification
        Notification mNotify = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("My media Library: Tonigh suggestion")
                .setContentText("What about watching " + unSeenMovies.get(randomIndex).getTitle())
                .setSmallIcon(R.drawable.popcorn)
                .setContentIntent(pIntent)
                .setSound(sound)
                .build();

        Log.d("Notification: ", "Notification sent");

        //Notify the manager that the notification has been sent with a certain ID.
        mNM.notify(ID_NOTIF, mNotify);
        ID_NOTIF++;
        stopSelf();


    }
}
