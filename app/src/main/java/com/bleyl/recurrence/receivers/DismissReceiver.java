package com.bleyl.recurrence.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bleyl.recurrence.database.DatabaseHelper;
import com.bleyl.recurrence.models.Reminder;
import com.bleyl.recurrence.utils.AlarmUtil;
import com.bleyl.recurrence.utils.NagUtil;
import com.bleyl.recurrence.utils.NotificationUtil;

public class DismissReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getIntExtra("NOTIFICATION_ID", 0);

        NagUtil.cancelNag(context, notificationId);
        NotificationUtil.cancelNotification(context, notificationId);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);

        DatabaseHelper database = DatabaseHelper.getInstance(context);
        Reminder reminder = database.getNotification(notificationId);
        reminder.setActiveState(Boolean.toString(false));
        database.updateNotification(reminder);
        database.close();
    }
}
