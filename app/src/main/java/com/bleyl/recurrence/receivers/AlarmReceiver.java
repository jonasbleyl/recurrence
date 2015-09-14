package com.bleyl.recurrence.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.bleyl.recurrence.utils.AlarmUtil;
import com.bleyl.recurrence.database.Database;
import com.bleyl.recurrence.models.Notification;
import com.bleyl.recurrence.utils.DateAndTimeUtil;
import com.bleyl.recurrence.utils.NotificationUtil;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    private Database mDatabase;
    private Notification mNotification;

    @Override
    public void onReceive(Context context, Intent intent) {
        mDatabase = new Database(context.getApplicationContext());
        mNotification = mDatabase.getNotification(intent.getIntExtra("NOTIFICATION_ID", 0));
        updateNotificationShown();

        NotificationUtil.createNotification(context, mNotification, intent);

        // Check if new alarm needs to be set
        if (mNotification.getNumberToShow() > mNotification.getNumberShown() || Boolean.parseBoolean(mNotification.getForeverState())) {
            Calendar calendar = Calendar.getInstance();

            switch (mNotification.getRepeatType()) {
                case 1: calendar.add(Calendar.DATE, 1); break;
                case 2: calendar.add(Calendar.WEEK_OF_YEAR, 1); break;
                case 3: calendar.add(Calendar.MONTH, 1); break;
                case 4: calendar.add(Calendar.YEAR, 1); break;
            }

            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(mNotification.getTime().substring(0, 2)));
            calendar.set(Calendar.MINUTE, Integer.parseInt(mNotification.getTime().substring(2, 4)));
            calendar.set(Calendar.SECOND, 0);

            mNotification.setDateAndTime(DateAndTimeUtil.toStringDateAndTime(calendar));
            mDatabase.update(mNotification);

            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            AlarmUtil.setAlarm(context, alarmIntent, mNotification.getId(), calendar);
        }
        mDatabase.close();

        // Update lists in tab fragments
        updateLists(context);
    }

    public void updateNotificationShown() {
        mNotification.setNumberShown(mNotification.getNumberShown() + 1);
        mDatabase.update(mNotification);
    }

    public void updateLists(Context context) {
        Intent intent = new Intent("BROADCAST_REFRESH");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}