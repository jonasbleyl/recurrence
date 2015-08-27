package com.bleyl.recurrence.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.bleyl.recurrence.utils.AlarmUtil;
import com.bleyl.recurrence.database.Database;
import com.bleyl.recurrence.models.Notification;
import com.bleyl.recurrence.R;
import com.bleyl.recurrence.utils.DateAndTimeUtil;
import com.bleyl.recurrence.ui.activities.ViewActivity;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    private Database mDatabase;
    private Notification mNotification;

    @Override
    public void onReceive(Context context, Intent intent) {
        mDatabase = new Database(context.getApplicationContext());
        updateNotificationShown(intent);

        // Create intent for notification onClick behaviour
        Intent viewIntent = new Intent(context, ViewActivity.class);
        viewIntent.putExtra("NOTIFICATION_ID", mNotification.getId());
        PendingIntent pending = PendingIntent.getActivity(context, mNotification.getId(), viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notifications_white_24dp)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(mNotification.getContent()))
                .setContentTitle(mNotification.getTitle())
                .setContentText(mNotification.getContent())
                .setTicker(mNotification.getTitle())
                .setContentIntent(pending)
                .setAutoCancel(true);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        // Set notification preferences options
        if (prefs.getBoolean("checkBoxSound", true)) builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        if (prefs.getBoolean("checkBoxLED", true))  builder.setLights(Color.BLUE, 700, 1500);
        if (prefs.getBoolean("checkBoxOngoing", false)) builder.setOngoing(true);
        if (prefs.getBoolean("checkBoxVibrate", true)) {
            long[] pattern = {0, 300, 0};
            builder.setVibrate(pattern);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(mNotification.getId(), builder.build());

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

            AlarmUtil.setAlarm(context, mNotification.getId(), calendar);
        }
        mDatabase.close();

        // Update lists in tab fragments
        updateLists(context);
    }

    public void updateNotificationShown(Intent intent) {
        mNotification = mDatabase.getNotification(intent.getIntExtra("NOTIFICATION_ID", 0));
        mNotification.setNumberShown(mNotification.getNumberShown() + 1);
        mDatabase.update(mNotification);
    }

    public void updateLists(Context context) {
        Intent intent = new Intent("BROADCAST_REFRESH");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}