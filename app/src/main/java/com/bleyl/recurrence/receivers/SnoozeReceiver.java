package com.bleyl.recurrence.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bleyl.recurrence.database.DatabaseHelper;
import com.bleyl.recurrence.models.Notification;
import com.bleyl.recurrence.utils.NotificationUtil;

public class SnoozeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHelper database = DatabaseHelper.getInstance(context);
        Notification notification = database.getNotification(intent.getIntExtra("NOTIFICATION_ID", 0));
        NotificationUtil.createNotification(context, notification);
        database.close();
    }
}