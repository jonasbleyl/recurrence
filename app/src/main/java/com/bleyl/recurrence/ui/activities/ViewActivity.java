package com.bleyl.recurrence.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
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
import com.bleyl.recurrence.utils.AlarmUtil;
import com.bleyl.recurrence.utils.DateAndTimeUtil;
import com.bleyl.recurrence.utils.NotificationUtil;

import java.util.Calendar;

public class ViewActivity extends AppCompatActivity {

    private Reminder mReminder;
    private ScrollView scroll;
    private View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        TextView notificationTitleText = (TextView) findViewById(R.id.notification_title);
        TextView notificationTimeText = (TextView) findViewById(R.id.notification_time);
        TextView contentText = (TextView) findViewById(R.id.notification_content);
        ImageView iconImage = (ImageView) findViewById(R.id.notification_icon);
        ImageView circleImage = (ImageView) findViewById(R.id.notification_circle);
        TextView timeText = (TextView) findViewById(R.id.time);
        TextView dateText = (TextView) findViewById(R.id.date);
        TextView repeatText = (TextView) findViewById(R.id.repeat);
        TextView shownText = (TextView) findViewById(R.id.shown);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.detail_layout);
        View shadowView = findViewById(R.id.toolbarShadow);
        scroll = (ScrollView) findViewById(R.id.scroll);
        headerView = findViewById(R.id.header);

        // Setup the shared element transitions for this activity
        setupTransitions();

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        if (getActionBar() != null) getActionBar().setDisplayHomeAsUpEnabled(true);

        // Add drawable shadow and adjust layout if build version is before lollipop
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            linearLayout.setPadding(0, 0, 0, 0);
            shadowView.setVisibility(View.VISIBLE);
        } else {
            ViewCompat.setElevation(headerView, getResources().getDimension(R.dimen.toolbar_elevation));
        }

        DatabaseHelper database = DatabaseHelper.getInstance(this);
        Intent intent = getIntent();
        int mNotificationID = intent.getIntExtra("NOTIFICATION_ID", 0);

        // Check if notification has been deleted
        if (database.isNotificationPresent(mNotificationID)) {
            mReminder = database.getNotification(mNotificationID);
            database.close();

            // Assign notification values to views
            Calendar calendar = DateAndTimeUtil.parseDateAndTime(mReminder.getDateAndTime());
            notificationTitleText.setText(mReminder.getTitle());
            contentText.setText(mReminder.getContent());
            dateText.setText(DateAndTimeUtil.toStringReadableDate(calendar));
            int iconResId = getResources().getIdentifier(mReminder.getIcon(), "drawable", getPackageName());
            iconImage.setImageResource(iconResId);
            circleImage.setColorFilter(Color.parseColor(mReminder.getColour()));
            String readableTime = DateAndTimeUtil.toStringReadableTime(calendar, this);
            timeText.setText(readableTime);
            notificationTimeText.setText(readableTime);

            if (mReminder.getRepeatType() == 5) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(getResources().getString(R.string.repeats_on));
                stringBuilder.append(" ");
                String[] shortWeekDays = DateAndTimeUtil.getShortWeekDays();
                for (int i = 0; i < mReminder.getDaysOfWeek().length; i++) {
                    if (mReminder.getDaysOfWeek()[i]) {
                        stringBuilder.append(shortWeekDays[i]);
                        stringBuilder.append(" ");
                    }
                }
                repeatText.setText(stringBuilder);
            } else {
                String[] repeatTexts = getResources().getStringArray(R.array.repeat_array);
                repeatText.setText(repeatTexts[mReminder.getRepeatType()]);
            }

            if (Boolean.parseBoolean(mReminder.getForeverState())) {
                shownText.setText(getResources().getString(R.string.forever));
            } else {
                String shown = (getResources().getString(R.string.times_shown, mReminder.getNumberShown(), mReminder.getNumberToShow()));
                shownText.setText(shown);
            }
        } else {
            database.close();
            // Return home as this notification has been deleted
            returnHome();
        }
    }

    public void setupTransitions() {
        // Add shared element transition animation if on Lollipop or later
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Enter transitions
            TransitionSet setEnter = new TransitionSet();

            Transition slideDown = new Explode();
            slideDown.addTarget(headerView);
            slideDown.excludeTarget(scroll, true);
            slideDown.setDuration(500);
            setEnter.addTransition(slideDown);

            Transition fadeOut = new Slide(Gravity.BOTTOM);
            fadeOut.addTarget(scroll);
            fadeOut.setDuration(500);
            setEnter.addTransition(fadeOut);

            // Exit transitions
            TransitionSet setExit = new TransitionSet();

            Transition slideDown2 = new Explode();
            slideDown2.addTarget(headerView);
            slideDown2.setDuration(570);
            setExit.addTransition(slideDown2);

            Transition fadeOut2 = new Slide(Gravity.BOTTOM);
            fadeOut2.addTarget(scroll);
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
        AlarmUtil.cancelAlarm(getApplicationContext(), alarmIntent, mReminder.getId());
        finish();
    }

    public void actionEdit() {
        Intent intent = new Intent(this, CreateEditActivity.class);
        intent.putExtra("NOTIFICATION_ID", mReminder.getId());
        startActivity(intent);
        finish();
    }

    public void returnHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_viewer, menu);
        return true;
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
            case R.id.action_show_now:
                actionShowNow();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}