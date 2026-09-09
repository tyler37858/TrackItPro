package com.example.trackitpro;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlarmScheduler {

    // schedules the alarm for the event time
    public static void scheduleEventAlarm(Context context, long eventId, String title, String date, long triggerTimeMillis) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, EventAlarmReceiver.class);
        intent.putExtra("eventId", eventId);
        intent.putExtra("title", title);
        intent.putExtra("date", date);

        // eventId makes it unique so alarms dont overwrite each other
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) eventId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            // exact alarm at the chosen time (usually works fine)
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    pendingIntent
            );
        }
    }

    // cancels alarm when event is deleted
    public static void cancelEventAlarm(Context context, long eventId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, EventAlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) eventId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}


