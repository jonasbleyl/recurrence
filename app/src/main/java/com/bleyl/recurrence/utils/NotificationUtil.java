package com.bleyl.recurrence.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.bleyl.recurrence.R;
import com.bleyl.recurrence.models.Notification;
import com.bleyl.recurrence.receivers.SnoozeActionReceiver;
import com.bleyl.recurrence.ui.activities.ViewActivity;

public class NotificationUtil {

    public static void createNotification(Context context, Notification notification, Intent intent) {
        // Create intent for notification onClick behaviour
        Intent viewIntent = new Intent(context, ViewActivity.class);
        viewIntent.putExtra("NOTIFICATION_ID", notification.getId());
        PendingIntent pending = PendingIntent.getActivity(context, notification.getId(), viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create intent for notification snooze click behaviour
        Intent snoozeIntent = new Intent(context, SnoozeActionReceiver.class);
        snoozeIntent.putExtra("NOTIFICATION_ID", notification.getId());
        PendingIntent pendingSnooze = PendingIntent.getBroadcast(context, notification.getId(), snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        int imageResId = context.getResources().getIdentifier(notification.getIcon(), "drawable", context.getPackageName());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(imageResId)
                .setColor(Color.parseColor(notification.getColour()))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notification.getContent()))
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getContent())
                .setTicker(notification.getTitle())
                .setAutoCancel(true);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        // Set notification preferences options
        if (sharedPreferences.getBoolean("checkBoxSound", true)) {
            builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
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
        if (sharedPreferences.getBoolean("checkBoxDismiss", false)) {
            builder.setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT));
        } else {
            builder.setContentIntent(pending);
        }
        if (sharedPreferences.getBoolean("checkBoxSnooze", false)) {
            builder.addAction(R.drawable.ic_snooze_white_24dp, context.getResources().getString(R.string.snooze), pendingSnooze);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notification.getId(), builder.build());
    }

    public static void cancelNotification(Context context, int notificationId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }
}