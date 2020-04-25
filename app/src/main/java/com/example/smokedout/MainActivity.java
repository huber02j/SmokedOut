package com.example.smokedout;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;

public class  MainActivity extends AppCompatActivity {
//    Button notificationButton;
    private String motivation = ""; //Used for motivational notification
    private Calendar motivationCal = Calendar.getInstance();
    int mYear;
    int mMonth;
    int mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        //Set up the notification channel when the app starts
        createNotificationChannel();
        long futureInMillis = SystemClock.elapsedRealtime() + 1000;

        setAlarmNotification("Notification Title", "Notification Content", futureInMillis);
        setAlarmNotification("Notification Title", "Second", futureInMillis+2000);

    }

    /** Called when the user clicks the add button */
    public void addGoal(View view) {
        Intent intent = new Intent(this, AddGoalActivity.class);
        startActivity(intent);
    }

    /** Called to set a motivational notification **/
    public void motivationReminder(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Motivational Text");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                motivation = input.getText().toString();
                Toast.makeText(MainActivity.this, motivation, Toast.LENGTH_SHORT).show();
                setMotivationDate();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    private void setMotivationDate(){
        Calendar calendar = Calendar.getInstance();

        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MINUTE);
        int DATE = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int date) {
                mYear = year;
                mMonth = month;
                mDate = date;
                setMotivationTime();
            }
        }, YEAR, MONTH, DATE);

        datePickerDialog.show();
    }

    private void setMotivationTime(){
        Calendar calendar = Calendar.getInstance();

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                motivationCal.set(Calendar.YEAR, mYear);
                motivationCal.set(Calendar.MONTH, mMonth);
                motivationCal.set(Calendar.DATE, mDate);
                motivationCal.set(Calendar.HOUR, hour);
                motivationCal.set(Calendar.MINUTE, minute);

                long millis = motivationCal.getTimeInMillis();
                long futureInMillis = millis - SystemClock.elapsedRealtime();


                setAlarmNotification("Smoked OUT!", motivation, futureInMillis);
            }
        }, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE),true);

        timePickerDialog.show();
    }

    private void setAlarmNotification(String title, String content, long futureInMillis) {
        Log.d("Alarm Notification", "setAlarmNotification: ");
        Notification notification = addNotification(title,content); //Create a new notification

        // Intent to reference Notification Publisher
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);

        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification); //Pack notification into intent
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, Calendar.getInstance().getTimeInMillis()); //If you need to pass a different id each time

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int)System.currentTimeMillis(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
//        Log.d("ALARM", )
        Log.d("ALARM", "setAlarmNotification: Set alarm");
    }

    private Notification addNotification(String title, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.CHANNEL_ID))
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_home_black_24dp)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setChannelId(getString(R.string.CHANNEL_ID));
        return builder.build();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.CHANNEL_ID), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
