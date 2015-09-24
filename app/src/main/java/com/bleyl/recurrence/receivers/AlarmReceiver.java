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
    private Calendar mCalendar;

    @Override
    public void onReceive(Context context, Intent intent) {
        mDatabase = new Database(context.getApplicationContext());
        mNotification = mDatabase.getNotification(intent.getIntExtra("NOTIFICATION_ID", 0));
        updateNotificationShown();

        NotificationUtil.createNotification(context, mNotification);

        // Check if new alarm needs to be set
        if (mNotification.getNumberToShow() > mNotification.getNumberShown() || Boolean.parseBoolean(mNotification.getForeverState())) {
            mCalendar = Calendar.getInstance();

            switch (mNotification.getRepeatType()) {
                case 1: mCalendar.add(Calendar.DATE, 1); break;
                case 2: mCalendar.add(Calendar.WEEK_OF_YEAR, 1); break;
                case 3: mCalendar.add(Calendar.MONTH, 1); break;
                case 4: mCalendar.add(Calendar.YEAR, 1); break;
                case 5: setSpecificDayOfWeek(); break;
            }

            mCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(mNotification.getTime().substring(0, 2)));
            mCalendar.set(Calendar.MINUTE, Integer.parseInt(mNotification.getTime().substring(2, 4)));
            mCalendar.set(Calendar.SECOND, 0);

            mNotification.setDateAndTime(DateAndTimeUtil.toStringDateAndTime(mCalendar));
            mDatabase.update(mNotification);

            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            AlarmUtil.setAlarm(context, alarmIntent, mNotification.getId(), mCalendar);
        }
        mDatabase.close();

        // Update lists in tab fragments
        updateLists(context);
    }

    public void setSpecificDayOfWeek() {
        int day = getNextSelectedDayOfWeek();
        if (day <= mCalendar.get(Calendar.DAY_OF_WEEK)) {
            mCalendar.add(Calendar.WEEK_OF_YEAR, 1);
        }
        mCalendar.set(Calendar.DAY_OF_WEEK, day);
    }

    public int getNextSelectedDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        for (int i = 0; i < 7; i++) {
            int position = (i + (calendar.get(Calendar.DAY_OF_WEEK) - 1)) % mNotification.getDaysOfWeek().length;
            if (mNotification.getDaysOfWeek()[position]) {
                return position + 1;
            }
        }
        return 0;
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