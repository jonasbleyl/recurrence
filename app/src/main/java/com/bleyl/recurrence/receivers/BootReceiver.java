package com.bleyl.recurrence.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bleyl.recurrence.database.DatabaseHelper;
import com.bleyl.recurrence.enums.NotificationsType;
import com.bleyl.recurrence.utils.AlarmUtil;
import com.bleyl.recurrence.models.Notification;
import com.bleyl.recurrence.utils.DateAndTimeUtil;

import java.util.Calendar;
import java.util.List;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHelper database = DatabaseHelper.getInstance(context);
        List<Notification> notificationList = database.getNotificationList(NotificationsType.ACTIVE);
        database.close();
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);

        for (Notification notification : notificationList) {
            Calendar calendar = DateAndTimeUtil.parseDateAndTime(notification.getDateAndTime());
            calendar.set(Calendar.SECOND, 0);
            AlarmUtil.setAlarm(context, alarmIntent, notification.getId(), calendar);
        }
    }
}