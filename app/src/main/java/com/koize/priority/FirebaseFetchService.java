package com.koize.priority;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.koize.priority.ui.reminders.RemindersData;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


public class FirebaseFetchService extends IntentService {


    public FirebaseFetchService() {
        super("FirebaseFetchService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            FetchFirebaseBackground fetchFirebaseBackground = new FetchFirebaseBackground();
            Query query = fetchFirebaseBackground.reminderRef.orderByChild("firstReminderDateTime");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        RemindersData remindersData = dataSnapshot.getValue(RemindersData.class);

                        if (remindersData.getFirstReminderDateTime() != 0 && remindersData.getReminderPendingIntent() == null && remindersData.getFirstReminderDateTime() > System.currentTimeMillis()) {
                            scheduleNoti(remindersData);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    public void scheduleNoti(RemindersData remindersData) {
        long reminderDateTime = remindersData.getFirstReminderDateTime();
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(reminderDateTime), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, h:mm:a");
        String formattedTime = formatter.format(dateTime);
        Notification.Builder builder = new Notification.Builder(getApplicationContext(), "reminders");
        builder.setContentTitle(remindersData.getReminderTitle());
        builder.setContentText(remindersData.getReminderTitle() + " at " + formattedTime);
        builder.setSmallIcon(R.drawable.baseline_access_time_24);
        Notification notification = builder.build();
        Intent intent = new Intent(getApplicationContext(), NotiReceiver.class);
        intent.putExtra(NotiReceiver.NOTIFICATION, notification);
        intent.putExtra("id", remindersData.getReminderId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), remindersData.getReminderId(), intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminderDateTime - 28800000, pendingIntent);
        remindersData.setReminderPendingIntent(pendingIntent);
    }


}