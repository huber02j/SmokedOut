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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class  MainActivity extends AppCompatActivity {
    //    Button notificationButton;
    private String motivation = ""; //Used for motivational notification
    private Calendar motivationCal = Calendar.getInstance();
//    int mYear;
//    int mMonth;
//    int mDate;

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

        //Used for testing notification system
//        long futureInMillis = System.currentTimeMillis() + 1000;
//        Calendar cal = Calendar.getInstance();
//
//        setAlarmNotification("Notification Title", "Notification Content", futureInMillis);
//        setAlarmNotification("Notification Title", "Second", futureInMillis+2000);
//        setAlarmNotification("Notification Title", "Third", cal.getTimeInMillis() + 5000);

    }

    /**
     * Add smoke times to database
     **/
    public void addSmokeTimes(View view) {
        DatabaseReference databaseSmokeTimes = FirebaseDatabase.getInstance().getReference("SmokeTimes");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        long smokeDate = System.currentTimeMillis();
        String timeID = databaseSmokeTimes.child(firebaseAuth.getUid()).push().getKey();

        // Submit
        databaseSmokeTimes.child(firebaseAuth.getUid()).child(timeID).setValue(smokeDate);
        Toast.makeText(this, "Smoke Time Recorded", Toast.LENGTH_SHORT).show();
    }

    // Record most common smoke times, set notification
    public void setSmokeTimesNotification(View view) {
        String UID = FirebaseAuth.getInstance().getUid();
        DatabaseReference databaseSmokeTimes = FirebaseDatabase.getInstance().getReference("SmokeTimes").child(UID);

        long smokeDate = System.currentTimeMillis();

        databaseSmokeTimes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {

                // Frequencies for each hour
                int[] hoursFreq = new int[24];

                // Loop through hours for all smoke times
                for (DataSnapshot ds : snap.getChildren()) {
                    long smokeLong = (long) ds.getValue();
                    Date smokeDate = new Date(smokeLong);
                    Calendar smokeCalendar = Calendar.getInstance();
                    smokeCalendar.setTime(smokeDate);
                    int hour = smokeCalendar.get(Calendar.HOUR_OF_DAY);
                    hoursFreq[hour] += 1;
                }

                // Get maximum hour
                int maxHour = getMaxHour(hoursFreq);

                // Set notification for tomorrow (if time has passed)
                Calendar todayCalendar = Calendar.getInstance();
                if (maxHour <= todayCalendar.get(Calendar.HOUR_OF_DAY)) {
                    todayCalendar.add(Calendar.DATE, 1);
                }

                // Update to go off on the hour
                todayCalendar.set(Calendar.HOUR_OF_DAY, maxHour);
                todayCalendar.set(Calendar.MINUTE, 0);
                todayCalendar.set(Calendar.SECOND, 0);
                todayCalendar.set(Calendar.MILLISECOND, 0);

                long setTime = todayCalendar.getTimeInMillis();
                setAlarmNotification("Smoked OUT!", "This next hour is hard, but you can get through it!", setTime);

                // Format toast
                String toastStr = buildToastString(todayCalendar);
                Toast.makeText(MainActivity.this, toastStr, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    // Build toast string for set notification
    private String buildToastString(Calendar cal) {
        Integer hour = cal.get(Calendar.HOUR);
        Integer minutes = cal.get(Calendar.MINUTE);
        String period = cal.get(Calendar.AM_PM) == 1 ? "PM" : "AM";
        String pad = minutes < 10 ? "0" : "";
        String hourStr = Integer.toString(hour) + ":" + pad + Integer.toString(minutes);
        String toastStr = "Notification set for " + hourStr + " " + period + ".";
        return toastStr;
    }

    // Get maximum hour from array
    private int getMaxHour(int[] hours) {
        int largest = 0;
        for (int i = 1; i< hours.length; i++) {
            if (hours[i] > hours[largest]) {
                largest = i;
            }
        }
        return largest;
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
                setMotivationDate();
            }
        });
        builder.setNegativeButton(null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    // Allow user to pick date
    private void setMotivationDate(){
        Calendar calendar = Calendar.getInstance();

        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DAY_OF_MONTH = calendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                motivationCal.set(Calendar.YEAR, year);
                motivationCal.set(Calendar.MONTH, month);
                motivationCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setMotivationTime();
            }
        }, YEAR, MONTH, DAY_OF_MONTH);

        datePickerDialog.show();
    }

    // Allow user to pick time
    private void setMotivationTime(){
        Calendar calendar = Calendar.getInstance();

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                motivationCal.set(Calendar.HOUR_OF_DAY, hour);
                motivationCal.set(Calendar.MINUTE, minute);
                motivationCal.set(Calendar.SECOND, 0);
                motivationCal.set(Calendar.MILLISECOND, 0);

                // Set notification
                long motivationTime = motivationCal.getTimeInMillis();
                setAlarmNotification("Smoked OUT!", motivation, motivationTime);

                // Format toast
                String toastStr = buildToastString(motivationCal);
                Toast.makeText(MainActivity.this, toastStr, Toast.LENGTH_SHORT).show();
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),true);

        timePickerDialog.show();
    }

    // Set notification
    private void setAlarmNotification(String title, String content, long futureInMillis) {
        Log.d("Alarm Notification", "setAlarmNotification: ");
        Notification notification = addNotification(title,content); //Create a new notification

        // Intent to reference Notification Publisher
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);

        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification); //Pack notification into intent
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, System.currentTimeMillis()); //If you need to pass a different id each time

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int)System.currentTimeMillis(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);
        Log.d("ALARM", "time = " + futureInMillis);
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

    public void showNotificationsAlert(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.fragment_add_notification, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        alert.setNegativeButton(null, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    public void addFriend(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.fragment_add_friend, null);
        final EditText etEmail = alertLayout.findViewById(R.id.et_email);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Add Friend");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        alert.setNegativeButton(null, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String email = etEmail.getText().toString();
                System.out.println(email);
                addFriendToDatabase(email);
                Toast.makeText(MainActivity.this, "Username: " + email, Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    /** Add friend **/
    public void addFriendToDatabase(final String teEmail){
        final DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference("UserProfile");
        databaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                String targetUID = "";
                String currUID = FirebaseAuth.getInstance().getUid();
                ArrayList<String> friends = new ArrayList<>();
                for (DataSnapshot ds: snap.getChildren()) {
                    String email = (String) ds.child("userEmail").getValue();
                    String UID = (String) ds.getKey();
                    // Find UID given email
                    if (email.equals(teEmail)) {
                        targetUID = UID;
                    }
                    if (UID.equals(currUID)) {
                        friends = (ArrayList) ds.child("friends").getValue();
                    }
                }
                if (!targetUID.equals("")) {
                    friends.add(targetUID);
                    databaseUser.child(FirebaseAuth.getInstance().getUid()).child("friends").setValue(friends);
                    Toast.makeText(MainActivity.this, "Friend successfully added!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MainActivity.this, "User with email does not exist.", Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        // Submit
        Toast.makeText(this, "Friend Successfully Added", Toast.LENGTH_SHORT).show();
    }

}
