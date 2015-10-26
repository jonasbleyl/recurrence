package com.bleyl.recurrence.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.bleyl.recurrence.database.DatabaseHelper;
import com.bleyl.recurrence.utils.AlarmUtil;
import com.bleyl.recurrence.models.Reminder;
import com.bleyl.recurrence.utils.DateAndTimeUtil;
import com.bleyl.recurrence.utils.NotificationUtil;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    private DatabaseHelper mDatabase;
    private Reminder mReminder;
    private Calendar mCalendar;

    @Override
    public void onReceive(Context context, Intent intent) {
        mDatabase = DatabaseHelper.getInstance(context);
        mReminder = mDatabase.getNotification(intent.getIntExtra("NOTIFICATION_ID", 0));
        updateNotificationShown();

        NotificationUtil.createNotification(context, mReminder);

        // Check if new alarm needs to be set
        if (mReminder.getNumberToShow() > mReminder.getNumberShown() || Boolean.parseBoolean(mReminder.getForeverState())) {
            mCalendar = Calendar.getInstance();

            switch (mReminder.getRepeatType()) {
                case 1: mCalendar.add(Calendar.DATE, 1); break;
                case 2: mCalendar.add(Calendar.WEEK_OF_YEAR, 1); break;
                case 3: mCalendar.add(Calendar.MONTH, 1); break;
                case 4: mCalendar.add(Calendar.YEAR, 1); break;
                case 5: setSpecificDayOfWeek(); break;
            }

            Calendar calendar = DateAndTimeUtil.parseDateAndTime(mReminder.getDateAndTime());
            mCalendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
            mCalendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
            mCalendar.set(Calendar.SECOND, 0);

            mReminder.setDateAndTime(DateAndTimeUtil.toStringDateAndTime(mCalendar));
            mDatabase.updateNotification(mReminder);

            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            AlarmUtil.setAlarm(context, alarmIntent, mReminder.getId(), mCalendar);
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
            int position = (i + (calendar.get(Calendar.DAY_OF_WEEK) - 1)) % mReminder.getDaysOfWeek().length;
            if (mReminder.getDaysOfWeek()[position]) {
                return position + 1;
            }
        }
        return 0;
    }

    public void updateNotificationShown() {
        mReminder.setNumberShown(mReminder.getNumberShown() + 1);
        mDatabase.updateNotification(mReminder);
    }

    public void updateLists(Context context) {
        Intent intent = new Intent("BROADCAST_REFRESH");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}