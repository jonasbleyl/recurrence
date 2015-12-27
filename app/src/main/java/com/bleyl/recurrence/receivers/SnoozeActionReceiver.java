package com.bleyl.recurrence.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bleyl.recurrence.R;
import com.bleyl.recurrence.utils.AlarmUtil;
import com.bleyl.recurrence.utils.NotificationUtil;

import java.util.Calendar;

public class SnoozeActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int reminderId = intent.getIntExtra("NOTIFICATION_ID", 0);
        NotificationUtil.cancelNotification(context, reminderId);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int defaultMinutes = context.getResources().getInteger(R.integer.default_snooze_minutes);
        int snoozeLength = sharedPreferences.getInt("snoozeLength", defaultMinutes);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, snoozeLength);

        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("naggingReminder", true)) {
            Intent alarmIntent = new Intent(context, NagReceiver.class);
            AlarmUtil.cancelAlarm(context, alarmIntent, reminderId);
        }

        Intent alarmIntent = new Intent(context, SnoozeReceiver.class);
        AlarmUtil.setAlarm(context, alarmIntent, reminderId, calendar);
    }
}