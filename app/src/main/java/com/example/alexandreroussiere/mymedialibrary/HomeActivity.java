package com.example.alexandreroussiere.mymedialibrary;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.util.Calendar;

/**
 * Created by Alexandre Roussière on 26/04/2016.
 * This is the Home of the whole application
 */
public class HomeActivity extends AppCompatActivity {

    private ImageView movie;
    private ImageView tvShow;
    private ImageView book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);

        //We set the alarm to make the service running in the background (for notifications)
        setUpAlarm();

        /*Get the views */
        movie = (ImageView)findViewById(R.id.movie);
        tvShow = (ImageView)findViewById(R.id.tv_show);
        book = (ImageView)findViewById(R.id.book);

        //If the Movie icon is clicked, we launche the movieLibrary activity
        movie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent movieLibrary = new Intent(HomeActivity.this,MainActivity.class);
                startActivity(movieLibrary);
            }
        });

    }

    /**
     * Configure the Alarm to sent a signal at appropriate time
     */
    private void setUpAlarm(){
        Calendar calendar = Calendar.getInstance();
        //We get the actual time
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);
        int currentSecond = calendar.get(Calendar.SECOND);

        // If the time we want to send the signal at is passed, we add one day
        if (currentHour >= 20 && currentMinute >= 0 && currentSecond>=0)
        {
            calendar.add(Calendar.DATE, 1);
        }
        // Configure the time for the signal to be sent
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        //Create the intent to the activity we want to be launched when the signal is sent
        Intent i = new Intent(HomeActivity.this, NotificationService.class);
        PendingIntent myNotification = PendingIntent.getService(this, 0, i, 0);

        //Create an alarm Manager which will launch the activity
        AlarmManager am = (AlarmManager) HomeActivity.this
                .getSystemService(HomeActivity.this.ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, myNotification);

    }
}
