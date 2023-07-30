package com.koize.priority;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class NotiReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        Notification notification = new NotificationCompat.Builder(context, "reminders")
                .setContentTitle(intent.getStringExtra("title"))
                //.setContentText("You've received new messages.")
                .setSmallIcon(R.drawable.baseline_access_time_24)
                .build();

        // Display the notification to the user.
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(intent.getIntExtra("id", 1), notification);    }
}