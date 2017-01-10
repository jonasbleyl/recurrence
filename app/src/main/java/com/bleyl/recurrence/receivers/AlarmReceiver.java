package com.bleyl.recurrence.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.bleyl.recurrence.database.DatabaseHelper;
import com.bleyl.recurrence.utils.AlarmUtil;
import com.bleyl.recurrence.models.Reminder;
import com.bleyl.recurrence.utils.NotificationUtil;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHelper database = DatabaseHelper.getInstance(context);
        Reminder reminder = database.getReminder(intent.getIntExtra("REMINDER_ID", 0));
        reminder.setNumberShown(reminder.getNumberShown() + 1);
        database.addReminder(reminder);

        boolean showQuietly = intent.getBooleanExtra("QUIET", false);
        NotificationUtil.createNotification(context, reminder, showQuietly);

        // Check if new alarm needs to be set
        if (reminder.getNumberToShow() > reminder.getNumberShown() || Boolean.parseBoolean(reminder.getForeverState())) {
            AlarmUtil.setNextAlarm(context, reminder, database);
        }
        Intent updateIntent = new Intent("BROADCAST_REFRESH");
        LocalBroadcastManager.getInstance(context).sendBroadcast(updateIntent);
        database.close();
    }
}