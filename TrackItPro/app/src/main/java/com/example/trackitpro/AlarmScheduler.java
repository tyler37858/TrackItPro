package com.example.trackitpro;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

public class AlarmScheduler {

    // schedules the alarm for the event time
    public static void scheduleEventAlarm(
            Context context, long eventId, long userId,
            String title, String date, long triggerTimeMillis)
    {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null)
            return;

        Intent intent = new Intent(context, EventAlarmReceiver.class);
        intent.putExtra("eventId", eventId);
        intent.putExtra("userId", userId);
        intent.putExtra("title", title);
        intent.putExtra("date", date);

        // eventId makes it unique so alarms don't overwrite each other
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) eventId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTimeMillis,
                        pendingIntent
                );
            }
            else
            {
                openExactAlarmSettings(context);
            }

        } else {
            scheduleExactAlarm(
                    alarmManager,
                    triggerTimeMillis,
                    pendingIntent
            );
        }
    }

    //schedule the exact alarm using the provided event time
    private static void scheduleExactAlarm(AlarmManager alarmManager, long triggerTimeMillis,
                                    PendingIntent pendingIntent)
    {
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeMillis,
                pendingIntent);
    }

    //open the system settings to alarm access can be granted
    private static void openExactAlarmSettings(Context context)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S)
            return;

        Intent settingsIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);

        settingsIntent.setData(Uri.parse("package:" + context.getPackageName()));

        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(settingsIntent);
    }

    // cancels alarm when event is deleted
    public static void cancelEventAlarm(Context context, long eventId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);


        if (alarmManager == null)
            return;

        Intent intent = new Intent(context, EventAlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) eventId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.cancel(pendingIntent);
    }
}


