package com.bleyl.recurrence.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.bleyl.recurrence.database.DatabaseHelper;
import com.bleyl.recurrence.models.Reminder;
import com.bleyl.recurrence.receivers.AlarmReceiver;

import java.util.Calendar;

public class AlarmUtil {

    public static void setAlarm(Context context, Intent intent, int notificationId, Calendar calendar) {
        intent.putExtra("NOTIFICATION_ID", notificationId);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    public static void cancelAlarm(Context context, Intent intent, int notificationId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

    public static void setNextAlarm(Context context, Reminder reminder, DatabaseHelper database) {
        Calendar calendar = DateAndTimeUtil.parseDateAndTime(reminder.getDateAndTime());
        calendar.set(Calendar.SECOND, 0);

        switch (reminder.getRepeatType()) {
            case Reminder.HOURLY:
                calendar.add(Calendar.HOUR, reminder.getInterval());
                break;
            case Reminder.DAILY:
                calendar.add(Calendar.DATE, reminder.getInterval());
                break;
            case Reminder.WEEKLY:
                calendar.add(Calendar.WEEK_OF_YEAR, reminder.getInterval());
                break;
            case Reminder.MONTHLY:
                calendar.add(Calendar.MONTH, reminder.getInterval());
                break;
            case Reminder.YEARLY:
                calendar.add(Calendar.YEAR, reminder.getInterval());
                break;
            case Reminder.SPECIFIC_DAYS:
                Calendar weekCalendar = (Calendar) calendar.clone();
                weekCalendar.add(Calendar.DATE, 1);
                for (int i = 0; i < 7; i++) {
                    int position = (i + (weekCalendar.get(Calendar.DAY_OF_WEEK) - 1)) % 7;
                    if (reminder.getDaysOfWeek()[position]) {
                        calendar.add(Calendar.DATE, i + 1);
                        break;
                    }
                }
                break;
        }

        reminder.setDateAndTime(DateAndTimeUtil.toStringDateAndTime(calendar));
        database.addNotification(reminder);

        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        setAlarm(context, alarmIntent, reminder.getId(), calendar);
    }
}