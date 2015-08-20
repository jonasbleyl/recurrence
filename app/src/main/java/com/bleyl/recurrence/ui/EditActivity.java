package com.bleyl.recurrence.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bleyl.recurrence.model.Notification;
import com.bleyl.recurrence.R;
import com.bleyl.recurrence.database.Database;
import com.bleyl.recurrence.util.AlarmUtil;
import com.bleyl.recurrence.util.AnimationUtil;
import com.bleyl.recurrence.util.DateAndTimeUtil;

import java.util.Calendar;

public class EditActivity extends AppCompatActivity {

    private EditText mTitleEditText;
    private EditText mContentEditText;
    private TextView mTimeText;
    private TextView mDateText;
    private TextView mRepeatText;
    private SwitchCompat mForeverSwitch;
    private EditText mTimesEditText;
    private TableRow mForeverRow;
    private TableRow mBottomRow;
    private View mBottomView;
    private TextView mShowText;
    private TextView mTimesText;
    private ImageView mImageWarningTime;
    private ImageView mImageWarningDate;
    private ImageView mImageWarningShow;
    private Calendar mCalendar;
    private int mRepeatType;
    private int mTimesShown;
    private Notification mNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        getWindow().setBackgroundDrawable(null);

        mTitleEditText = (EditText) findViewById(R.id.notificationTitle);
        mContentEditText = (EditText) findViewById(R.id.notificationContent);
        mTimeText = (TextView) findViewById(R.id.time);
        mDateText = (TextView) findViewById(R.id.date);
        mRepeatText = (TextView) findViewById(R.id.repeatDay);
        mForeverSwitch = (SwitchCompat) findViewById(R.id.switchToggle);
        mTimesEditText = (EditText) findViewById(R.id.showTimesNumber);
        mForeverRow = (TableRow) findViewById(R.id.foreverRow);
        mBottomRow = (TableRow) findViewById(R.id.bottomRow);
        mBottomView = findViewById(R.id.bottomView);
        mShowText = (TextView) findViewById(R.id.show);
        mTimesText = (TextView) findViewById(R.id.times);
        mImageWarningTime = (ImageView) findViewById(R.id.errorTime);
        mImageWarningDate = (ImageView) findViewById(R.id.errorDate);
        mImageWarningShow = (ImageView) findViewById(R.id.errorShow);

        Database database = new Database(this.getApplicationContext());
        Intent intent = getIntent();
        mNotification = database.getNotification(intent.getIntExtra("NOTIFICATION_ID", 0));
        database.close();

        // Assign notification values to views
        assignValues();

        // Assign default values and layout
        mCalendar.set(Calendar.SECOND, 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mForeverSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleTextColour();
            }
        });
    }

    public void assignValues() {
        mCalendar = DateAndTimeUtil.parseDateAndTime(mNotification.getDateAndTime());
        mTitleEditText.setText(mNotification.getTitle());
        mContentEditText.setText(mNotification.getContent());
        mTimeText.setText(DateAndTimeUtil.toStringReadableTime(mCalendar));
        mDateText.setText(DateAndTimeUtil.toStringReadableDate(mCalendar));
        mTimesEditText.setText(Integer.toString(mNotification.getNumberToShow()));
        mTimesShown = mNotification.getNumberShown();
        mRepeatType = mNotification.getRepeatType();
        mTimesText.setVisibility(View.VISIBLE);

        if (mNotification.getRepeatType() != 0) {
            mForeverRow.setVisibility(View.VISIBLE);
            mBottomRow.setVisibility(View.VISIBLE);
            mBottomView.setVisibility(View.VISIBLE);
            String[] mRepeatTexts = getResources().getStringArray(R.array.repeat_array);
            mRepeatText.setText(mRepeatTexts[mNotification.getRepeatType()]);
        }

        if (Boolean.parseBoolean(mNotification.getForeverState())) {
            mForeverSwitch.setChecked(true);
            toggleTextColour();
        }

        mShowText.setText(String.format(getResources().getString(R.string.times_shown_edit), mNotification.getNumberShown()));
        mTimesEditText.setText(Integer.toString(mNotification.getNumberToShow()));
    }

    public void timePicker(View view) {
        TimePickerDialog TimePicker = new TimePickerDialog(EditActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(android.widget.TimePicker timePicker, int hour, int minute) {
                mCalendar.set(Calendar.HOUR_OF_DAY, hour);
                mCalendar.set(Calendar.MINUTE, minute);
                mTimeText.setText(DateAndTimeUtil.toStringReadableTime(mCalendar));
            }
        }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true);
        TimePicker.show();
    }

    public void datePicker(View view) {
        DatePickerDialog DatePicker = new DatePickerDialog(EditActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker DatePicker, int year, int month, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, month);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mDateText.setText(DateAndTimeUtil.toStringReadableDate(mCalendar));
            }
        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        DatePicker.show();
    }

    public void repeatSelector(View view) {
        final String[] repeatArray = getResources().getStringArray(R.array.repeat_array);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(repeatArray, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    mForeverRow.setVisibility(View.GONE);
                    mBottomRow.setVisibility(View.GONE);
                    mBottomView.setVisibility(View.GONE);
                } else {
                    mForeverRow.setVisibility(View.VISIBLE);
                    mBottomRow.setVisibility(View.VISIBLE);
                    mBottomView.setVisibility(View.VISIBLE);
                }
                mRepeatType = which;
                mRepeatText.setText(repeatArray[which]);
            }
        });
        builder.create();
        builder.show();
    }

    public void saveNotification() {
        int id = mNotification.getId();
        String title = mTitleEditText.getText().toString();
        String content = mContentEditText.getText().toString();
        String forever = Boolean.toString(mForeverSwitch.isChecked());
        String date = DateAndTimeUtil.toStringDate(mCalendar);
        String time = DateAndTimeUtil.toStringTime(mCalendar);

        // Assign previous timesToShow number if no number was entered
        int timesToShow;
        if (mTimesEditText.getText().toString().isEmpty()) {
            timesToShow = mNotification.getNumberToShow();
        } else {
            timesToShow = Integer.parseInt(mTimesEditText.getText().toString());
        }

        // Increment timesToShow to allow notification to go off one more time if conditions are met
        if (Integer.parseInt(mTimesEditText.getText().toString()) == mTimesShown && !mForeverSwitch.isChecked()) {
            timesToShow++;
        }

        Database database = new Database(this.getApplicationContext());
        database.update(new Notification(id, title, content, date + time, mRepeatType, forever, timesToShow, mTimesShown));
        database.close();

        AlarmUtil.setAlarm(getApplicationContext(), mNotification.getId(), mCalendar);
        finish();
    }

    public void toggleSwitch(View view) {
        mForeverSwitch.toggle();
    }

    public void toggleTextColour() {
        if (mForeverSwitch.isChecked()) {
            mShowText.setTextColor(getResources().getColor(R.color.textLightGray));
            mTimesEditText.setTextColor(getResources().getColor(R.color.textLightGray));
            mTimesText.setTextColor(getResources().getColor(R.color.textLightGray));
        } else {
            mShowText.setTextColor(Color.BLACK);
            mTimesEditText.setTextColor(Color.BLACK);
            mTimesText.setTextColor(Color.BLACK);
        }
    }

    public void validateInput() {
        // Check if the number of times to show notification is empty
        String times;
        if (mTimesEditText.getText().toString().isEmpty()) {
            times = "0";
        } else {
            times = mTimesEditText.getText().toString();
        }

        if (DateAndTimeUtil.toLongDateAndTime(mCalendar) < DateAndTimeUtil.toLongDateAndTime(Calendar.getInstance())) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_past_date), Toast.LENGTH_SHORT).show();
            mImageWarningTime.setVisibility(View.VISIBLE);
            mImageWarningDate.setVisibility(View.VISIBLE);
        } else if ((times.isEmpty() || Integer.parseInt(times) < mTimesShown) && !mForeverSwitch.isChecked()) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_higher_number), Toast.LENGTH_SHORT).show();
            mImageWarningShow.setVisibility(View.VISIBLE);
        } else if ((mRepeatType != 0 && Integer.parseInt(times) == mTimesShown) && !mForeverSwitch.isChecked()) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_higher_number), Toast.LENGTH_SHORT).show();
            mImageWarningShow.setVisibility(View.VISIBLE);
        }  else if (mTitleEditText.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_title_empty), Toast.LENGTH_SHORT).show();
            AnimationUtil.shakeView(mTitleEditText, getApplicationContext());
        } else {
            saveNotification();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            validateInput();
        }
        return true;
    }
}