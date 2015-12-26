package com.bleyl.recurrence.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bleyl.recurrence.database.DatabaseHelper;
import com.bleyl.recurrence.models.Reminder;
import com.bleyl.recurrence.utils.NagUtil;
import com.bleyl.recurrence.utils.NotificationUtil;

import java.util.Calendar;

public class SnoozeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHelper database = DatabaseHelper.getInstance(context);
        Reminder reminder = database.getNotification(intent.getIntExtra("NOTIFICATION_ID", 0));
        int reminderId = reminder.getId();
        if (reminderId != 0 && reminder.getActiveState() != 0) {
            NotificationUtil.createNotification(context, reminder);
            if (reminder.getNagTimer() != 0) {
                NagUtil.setNextNag(context, reminder, Calendar.getInstance());
            }
        }
        database.close();
    }
}