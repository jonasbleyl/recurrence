package com.bleyl.recurrence.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.bleyl.recurrence.database.DatabaseHelper;
import com.bleyl.recurrence.models.Reminder;
import com.bleyl.recurrence.utils.NagUtil;
import com.bleyl.recurrence.utils.NotificationUtil;

import java.util.Calendar;

public class NagReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHelper database = DatabaseHelper.getInstance(context);
        int reminderId = intent.getIntExtra("NOTIFICATION_ID", 0);

        if (reminderId != 0) {
            Reminder reminder = database.getNotification(reminderId);
            NotificationUtil.createNotification(context, reminder);
            NagUtil.setNextNag(context, reminder, Calendar.getInstance());
        }
    }
}
