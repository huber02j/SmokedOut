package com.example.smokedout;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class NotificationPublisher extends BroadcastReceiver {
    public static String NOTIFICATION_ID = "notification-id" ;
    public static String NOTIFICATION = "notification" ;

    @Override
    public void onReceive(Context context, Intent intent) {
        Notification notification = intent.getParcelableExtra(NOTIFICATION);

        Log.d("notification", "onReceive: inside");
//        int id = intent.getIntExtra(NOTIFICATION_ID, 0); //In case you need different id
        int id = R.string.Notification_ID;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(id, notification);

    }
}
