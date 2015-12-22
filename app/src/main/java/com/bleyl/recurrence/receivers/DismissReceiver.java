package com.bleyl.recurrence.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bleyl.recurrence.utils.NagUtil;
import com.bleyl.recurrence.utils.NotificationUtil;

public class DismissReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NagUtil.cancelNag(context, intent.getIntExtra("NOTIFICATION_ID", 0));
        NotificationUtil.cancelNotification(context, intent.getIntExtra("NOTIFICATION_ID", 0));
    }
}
