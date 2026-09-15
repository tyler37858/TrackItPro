package com.example.trackitpro;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class EventAlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "trackitpro_events";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        long eventId = intent.getLongExtra("eventId", 0);
        long userId = intent.getLongExtra("userId", -1);
        String title = intent.getStringExtra("title");
        String date = intent.getStringExtra("date");

        if (title == null) title = "Event reminder";
        if (date == null) date = "";

        // creates the notification channel when it is required
        createChannelIfNeeded(context);

        //open the event screen for the correct user when tapped
        Intent openAppIntent = new Intent(context, EventsActivity.class);
        openAppIntent.putExtra("userId", userId);

        PendingIntent contentIntent = PendingIntent.getActivity(
                context,
                (int) eventId,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        //build the event notification reminder for hte event
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText("Happening now: " + date)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (nm != null) {
            nm.notify((int) eventId, builder.build());
        }
    }

    //create the event notification channel on newer than android 8 versions
    private void createChannelIfNeeded(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (nm == null) return;

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Event Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            nm.createNotificationChannel(channel);
        }
    }
}



