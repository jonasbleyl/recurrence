package com.bleyl.recurrence.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.bleyl.recurrence.R;
import com.bleyl.recurrence.models.Reminder;
import com.bleyl.recurrence.receivers.DismissReceiver;
import com.bleyl.recurrence.receivers.NagReceiver;
import com.bleyl.recurrence.receivers.SnoozeActionReceiver;
import com.bleyl.recurrence.activities.ViewActivity;

import java.util.Calendar;

public class NotificationUtil {

    public static void createNotification(Context context, Reminder reminder) {
        // Create intent for notification onClick behaviour
        Intent viewIntent = new Intent(context, ViewActivity.class);
        viewIntent.putExtra("NOTIFICATION_ID", reminder.getId());
        viewIntent.putExtra("NOTIFICATION_DISMISS", true);
        PendingIntent pending = PendingIntent.getActivity(context, reminder.getId(), viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create intent for notification snooze click behaviour
        Intent snoozeIntent = new Intent(context, SnoozeActionReceiver.class);
        snoozeIntent.putExtra("NOTIFICATION_ID", reminder.getId());
        PendingIntent pendingSnooze = PendingIntent.getBroadcast(context, reminder.getId(), snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        int imageResId = context.getResources().getIdentifier(reminder.getIcon(), "drawable", context.getPackageName());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(imageResId)
                .setColor(Color.parseColor(reminder.getColour()))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(reminder.getContent()))
                .setContentTitle(reminder.getTitle())
                .setContentText(reminder.getContent())
                .setTicker(reminder.getTitle())
                .setContentIntent(pending);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (sharedPreferences.getBoolean("checkBoxNagging", false)) {
            Intent swipeIntent = new Intent(context, DismissReceiver.class);
            swipeIntent.putExtra("NOTIFICATION_ID", reminder.getId());
            PendingIntent pendingDismiss = PendingIntent.getBroadcast(context, reminder.getId(), swipeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setDeleteIntent(pendingDismiss);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, sharedPreferences.getInt("nagMinutes", context.getResources().getInteger(R.integer.default_nag_minutes)));
            calendar.add(Calendar.SECOND, sharedPreferences.getInt("nagSeconds", context.getResources().getInteger(R.integer.default_nag_seconds)));
            Intent alarmIntent = new Intent(context, NagReceiver.class);
            AlarmUtil.setAlarm(context, alarmIntent, reminder.getId(), calendar);
        }

        String soundUri = sharedPreferences.getString("NotificationSound", "content://settings/system/notification_sound");
        if (soundUri.length() != 0) {
            builder.setSound(Uri.parse(soundUri));
        }
        if (sharedPreferences.getBoolean("checkBoxLED", true)) {
            builder.setLights(Color.BLUE, 700, 1500);
        }
        if (sharedPreferences.getBoolean("checkBoxOngoing", false)) {
            builder.setOngoing(true);
        }
        if (sharedPreferences.getBoolean("checkBoxVibrate", true)) {
            long[] pattern = {0, 300, 0};
            builder.setVibrate(pattern);
        }
        if (sharedPreferences.getBoolean("checkBoxMarkAsDone", false)) {
            Intent intent = new Intent(context, DismissReceiver.class);
            intent.putExtra("NOTIFICATION_ID", reminder.getId());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, reminder.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.addAction(R.drawable.ic_done_white_24dp, context.getString(R.string.mark_as_done), pendingIntent);
        }
        if (sharedPreferences.getBoolean("checkBoxSnooze", false)) {
            builder.addAction(R.drawable.ic_snooze_white_24dp, context.getString(R.string.snooze), pendingSnooze);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.setPriority(Notification.PRIORITY_HIGH);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(reminder.getId(), builder.build());
    }

    public static void cancelNotification(Context context, int notificationId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }
}