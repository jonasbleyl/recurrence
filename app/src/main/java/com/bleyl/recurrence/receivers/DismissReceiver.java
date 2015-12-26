package com.bleyl.recurrence.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bleyl.recurrence.database.DatabaseHelper;
import com.bleyl.recurrence.models.Reminder;
import com.bleyl.recurrence.utils.NagUtil;
import com.bleyl.recurrence.utils.NotificationUtil;

public class DismissReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra("NOTIFICATION_ID", 0);

        NagUtil.cancelNag(context, id);
        NotificationUtil.cancelNotification(context, id);

        DatabaseHelper database = DatabaseHelper.getInstance(context);
        Reminder reminder = database.getNotification(id);
        reminder.setActiveState(Boolean.toString(false));
        database.updateNotification(reminder);
        database.close();
    }
}
