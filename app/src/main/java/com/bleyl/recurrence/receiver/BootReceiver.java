package com.bleyl.recurrence.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bleyl.recurrence.util.AlarmUtil;
import com.bleyl.recurrence.database.Database;
import com.bleyl.recurrence.model.Notification;
import com.bleyl.recurrence.util.DateAndTimeUtil;

import java.util.Calendar;
import java.util.List;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Database database = new Database(context.getApplicationContext());
        List<Notification> notificationList = database.getActiveNotifications();
        database.close();

        for (Notification notification : notificationList) {
            Calendar calendar = DateAndTimeUtil.parseDateAndTime(notification.getDateAndTime());
            calendar.set(Calendar.SECOND, 0);
            AlarmUtil.setAlarm(context, notification.getId(), calendar);
        }
    }
}