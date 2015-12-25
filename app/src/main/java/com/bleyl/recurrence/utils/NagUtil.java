package com.bleyl.recurrence.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.bleyl.recurrence.models.Reminder;
import com.bleyl.recurrence.receivers.NagReceiver;

import java.util.Calendar;

public class NagUtil {
    public static void setNextNag(Context context, Reminder reminder, Calendar calendar) {
        calendar.add(Calendar.SECOND, reminder.getNagTimer());

        Intent nagIntent = new Intent(context, NagReceiver.class);
        AlarmUtil.setAlarm(context, nagIntent, reminder.getId(), calendar);
    }

    public static void cancelNag(Context context, int notificationId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NagReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }
}