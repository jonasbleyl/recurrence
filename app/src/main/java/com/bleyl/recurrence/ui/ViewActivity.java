package com.bleyl.recurrence.ui;

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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bleyl.recurrence.model.Notification;
import com.bleyl.recurrence.R;
import com.bleyl.recurrence.database.Database;
import com.bleyl.recurrence.util.AlarmUtil;
import com.bleyl.recurrence.util.DateAndTimeUtil;

import java.util.Calendar;

public class ViewActivity extends AppCompatActivity {

    private Notification mNotification;
    private ScrollView scroll;
    private View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        TextView titleTextView = (TextView) findViewById(R.id.title);
        TextView contentTextView = (TextView) findViewById(R.id.content);
        TextView timeTextView = (TextView) findViewById(R.id.time);
        TextView dateTextView = (TextView) findViewById(R.id.date);
        TextView repeatTextView = (TextView) findViewById(R.id.repeat);
        TextView shownTextView = (TextView) findViewById(R.id.shown);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.detail_layout);
        View rootView = findViewById(R.id.root);
        View shadowView = findViewById(R.id.toolbarShadow);
        scroll = (ScrollView) findViewById(R.id.scroll);
        headerView = findViewById(R.id.header);

        // Setup the shared element transitions for this activity
        setupTransitions();

        // Add drawable shadow and adjust layout if build version is before lollipop
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            linearLayout.setPadding(0, 0, 0, 0);
            rootView.setBackgroundColor(Color.WHITE);
            shadowView.setVisibility(View.VISIBLE);
        }

        setSupportActionBar(toolbar);
        ViewCompat.setElevation(headerView, getResources().getDimension(R.dimen.toolbar_elevation));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else {
                    finish();
                }
            }
        });

        Database database = new Database(this.getApplicationContext());
        Intent intent = getIntent();
        int mNotificationID = intent.getIntExtra("NOTIFICATION_ID", 0);

        // Check if notification has been deleted
        if (database.isPresent(mNotificationID)) {
            mNotification = database.getNotification(mNotificationID);
            database.close();

            // Assign notification values to views
            Calendar calendar = DateAndTimeUtil.parseDateAndTime(mNotification.getDateAndTime());
            titleTextView.setText(mNotification.getTitle());
            contentTextView.setText(mNotification.getContent());
            timeTextView.setText(DateAndTimeUtil.toStringReadableTime(calendar));
            dateTextView.setText(DateAndTimeUtil.toStringReadableDate(calendar));
            String[] repeatTexts = getResources().getStringArray(R.array.repeat_array);
            repeatTextView.setText(repeatTexts[mNotification.getRepeatType()]);

            if (Boolean.parseBoolean(mNotification.getForeverState())) {
                shownTextView.setText(getResources().getString(R.string.forever));
            } else {
                String shown = (getResources().getString(R.string.times_shown, mNotification.getNumberShown(), mNotification.getNumberToShow()));
                shownTextView.setText(shown);
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
        new AlertDialog.Builder(this)
                .setMessage(getResources().getString(R.string.delete_confirmation))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        actionDelete();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), null).show();
    }

    public void actionDelete() {
        Database database = new Database(this.getApplicationContext());
        database.delete(mNotification);
        database.close();

        AlarmUtil.cancelAlarm(getApplicationContext(), mNotification.getId());
        finish();
    }

    public void actionEdit() {
        Intent intent = new Intent(this, CreateActivity.class);
        intent.putExtra("NOTIFICATION_ID", mNotification.getId());
        startActivity(intent);
        finish();
    }

    public void returnHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
        int id = item.getItemId();

        if (id == R.id.action_delete) {
            confirmDelete();
        }
        if (id == R.id.action_edit) {
            actionEdit();
        }
        return super.onOptionsItemSelected(item);
    }
}