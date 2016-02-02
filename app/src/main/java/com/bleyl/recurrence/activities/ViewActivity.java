package com.bleyl.recurrence.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bleyl.recurrence.database.DatabaseHelper;
import com.bleyl.recurrence.models.Reminder;
import com.bleyl.recurrence.R;
import com.bleyl.recurrence.receivers.AlarmReceiver;
import com.bleyl.recurrence.receivers.DismissReceiver;
import com.bleyl.recurrence.receivers.SnoozeReceiver;
import com.bleyl.recurrence.utils.AlarmUtil;
import com.bleyl.recurrence.utils.DateAndTimeUtil;
import com.bleyl.recurrence.utils.NotificationUtil;
import com.bleyl.recurrence.utils.TextFormatUtil;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ViewActivity extends AppCompatActivity {

    @Bind(R.id.notification_title) TextView mNotificationTitleText;
    @Bind(R.id.notification_time) TextView mNotificationTimeText;
    @Bind(R.id.notification_content) TextView mContentText;
    @Bind(R.id.notification_icon) ImageView mIconImage;
    @Bind(R.id.notification_circle) ImageView mCircleImage;
    @Bind(R.id.time) TextView mTimeText;
    @Bind(R.id.date) TextView mDateText;
    @Bind(R.id.repeat) TextView mRepeatText;
    @Bind(R.id.shown) TextView mShownText;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.detail_layout) LinearLayout mLinearLayout;
    @Bind(R.id.toolbar_shadow) View mShadowView;
    @Bind(R.id.scroll) ScrollView mScrollView;
    @Bind(R.id.header) View mHeaderView;
    @Bind(R.id.view_coordinator) CoordinatorLayout mCoordinatorLayout;

    private Reminder mReminder;
    private boolean mHideMarkAsDone;
    private boolean mReminderChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        ButterKnife.bind(this);
        setupTransitions();

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        if (getActionBar() != null) getActionBar().setDisplayHomeAsUpEnabled(true);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(null);

        // Add drawable shadow and adjust layout if build version is before lollipop
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mLinearLayout.setPadding(0, 0, 0, 0);
            mShadowView.setVisibility(View.VISIBLE);
        } else {
            ViewCompat.setElevation(mHeaderView, getResources().getDimension(R.dimen.toolbar_elevation));
        }

        DatabaseHelper database = DatabaseHelper.getInstance(this);
        Intent intent = getIntent();
        int mReminderId = intent.getIntExtra("NOTIFICATION_ID", 0);

        // Arrived to activity from notification on click
        // Cancel notification and nag alarm
        if (intent.getBooleanExtra("NOTIFICATION_DISMISS", false)) {
            Intent dismissIntent = new Intent().setClass(this, DismissReceiver.class);
            dismissIntent.putExtra("NOTIFICATION_ID", mReminderId);
            sendBroadcast(dismissIntent);
        }

        // Check if notification has been deleted
        if (database.isNotificationPresent(mReminderId)) {
            mReminder = database.getNotification(mReminderId);
            database.close();
        } else {
            database.close();
            returnHome();
        }
    }

    public void assignReminderValues() {
        Calendar calendar = DateAndTimeUtil.parseDateAndTime(mReminder.getDateAndTime());
        mNotificationTitleText.setText(mReminder.getTitle());
        mContentText.setText(mReminder.getContent());
        mDateText.setText(DateAndTimeUtil.toStringReadableDate(calendar));
        mIconImage.setImageResource(getResources().getIdentifier(mReminder.getIcon(), "drawable", getPackageName()));
        mCircleImage.setColorFilter(Color.parseColor(mReminder.getColour()));
        String readableTime = DateAndTimeUtil.toStringReadableTime(calendar, this);
        mTimeText.setText(readableTime);
        mNotificationTimeText.setText(readableTime);

        if (mReminder.getRepeatType() == 6) {
            mRepeatText.setText(TextFormatUtil.formatDaysOfWeekText(this, mReminder.getDaysOfWeek()));
        } else {
            if (mReminder.getInterval() > 1) {
                mRepeatText.setText(TextFormatUtil.formatAdvancedRepeatText(this, mReminder.getRepeatType(), mReminder.getInterval()));
            } else {
                mRepeatText.setText(getResources().getStringArray(R.array.repeat_array)[mReminder.getRepeatType()]);
            }
        }

        if (Boolean.parseBoolean(mReminder.getForeverState())) {
            mShownText.setText(getResources().getString(R.string.forever));
        } else {
            mShownText.setText(getResources().getString(R.string.times_shown, mReminder.getNumberShown(), mReminder.getNumberToShow()));
        }

        // Hide "Mark as done" action if reminder is inactive
        mHideMarkAsDone = mReminder.getNumberToShow() <= mReminder.getNumberShown() && !Boolean.parseBoolean(mReminder.getForeverState());
        invalidateOptionsMenu();
    }

    public void setupTransitions() {
        // Add shared element transition animation if on Lollipop or later
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Enter transitions
            TransitionSet setEnter = new TransitionSet();

            Transition slideDown = new Explode();
            slideDown.addTarget(mHeaderView);
            slideDown.excludeTarget(mScrollView, true);
            slideDown.setDuration(500);
            setEnter.addTransition(slideDown);

            Transition fadeOut = new Slide(Gravity.BOTTOM);
            fadeOut.addTarget(mScrollView);
            fadeOut.setDuration(500);
            setEnter.addTransition(fadeOut);

            // Exit transitions
            TransitionSet setExit = new TransitionSet();

            Transition slideDown2 = new Explode();
            slideDown2.addTarget(mHeaderView);
            slideDown2.setDuration(570);
            setExit.addTransition(slideDown2);

            Transition fadeOut2 = new Slide(Gravity.BOTTOM);
            fadeOut2.addTarget(mScrollView);
            fadeOut2.setDuration(280);
            setExit.addTransition(fadeOut2);

            getWindow().setEnterTransition(setEnter);
            getWindow().setReturnTransition(setExit);
        }
    }

    public void confirmDelete() {
        new AlertDialog.Builder(this, R.style.Dialog)
                .setMessage(getResources().getString(R.string.delete_confirmation))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        actionDelete();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), null).show();
    }

    public void actionShowNow() {
        NotificationUtil.createNotification(this, mReminder);
    }

    public void actionDelete() {
        DatabaseHelper database = DatabaseHelper.getInstance(this);
        database.deleteNotification(mReminder);
        database.close();
        Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        AlarmUtil.cancelAlarm(this, alarmIntent, mReminder.getId());
        Intent snoozeIntent = new Intent(getApplicationContext(), SnoozeReceiver.class);
        AlarmUtil.cancelAlarm(this, snoozeIntent, mReminder.getId());
        finish();
    }

    public void actionEdit() {
        Intent intent = new Intent(this, CreateEditActivity.class);
        intent.putExtra("NOTIFICATION_ID", mReminder.getId());
        startActivity(intent);
        finish();
    }

    public void actionMarkAsDone() {
        mReminderChanged = true;
        DatabaseHelper database = DatabaseHelper.getInstance(this);
        // Check whether next alarm needs to be set
        if (mReminder.getNumberShown() + 1 != mReminder.getNumberToShow() || Boolean.parseBoolean(mReminder.getForeverState())) {
            AlarmUtil.setNextAlarm(this, mReminder, database, DateAndTimeUtil.parseDateAndTime(mReminder.getDateAndTime()));
        } else {
            Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
            AlarmUtil.cancelAlarm(this, alarmIntent, mReminder.getId());
            mReminder.setDateAndTime(DateAndTimeUtil.toStringDateAndTime(Calendar.getInstance()));
        }
        mReminder.setNumberShown(mReminder.getNumberShown() + 1);
        database.addNotification(mReminder);
        assignReminderValues();
        database.close();
        Snackbar.make(mCoordinatorLayout, getResources().getString(R.string.toast_mark_as_done), Snackbar.LENGTH_SHORT).show();
    }

    public void returnHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void updateReminder() {
        DatabaseHelper database = DatabaseHelper.getInstance(this);
        mReminder = database.getNotification(mReminder.getId());
        database.close();
        assignReminderValues();
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter("BROADCAST_REFRESH"));
        updateReminder();
        super.onResume();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        super.onPause();
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mReminderChanged = true;
            updateReminder();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_viewer, menu);
        if (mHideMarkAsDone) {
            menu.findItem(R.id.action_mark_as_done).setVisible(false);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mReminderChanged) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_delete:
                confirmDelete();
                return true;
            case R.id.action_edit:
                actionEdit();
                return true;
            case R.id.action_mark_as_done:
                actionMarkAsDone();
                return true;
            case R.id.action_show_now:
                actionShowNow();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}