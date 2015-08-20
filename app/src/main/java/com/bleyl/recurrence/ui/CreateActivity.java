package com.bleyl.recurrence.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bleyl.recurrence.model.Notification;
import com.bleyl.recurrence.R;
import com.bleyl.recurrence.database.Database;
import com.bleyl.recurrence.util.AlarmUtil;
import com.bleyl.recurrence.util.AnimationUtil;
import com.bleyl.recurrence.util.DateAndTimeUtil;

import java.util.Calendar;

public class CreateActivity extends AppCompatActivity {

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
    private ImageView mImageWarningTime;
    private ImageView mImageWarningDate;
    private Calendar mCalendar;
    private int mRepeatType;
    private int mId;

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
        mImageWarningTime = (ImageView) findViewById(R.id.errorTime);
        mImageWarningDate = (ImageView) findViewById(R.id.errorDate);
        mCalendar = Calendar.getInstance();

        // Set default values
        mCalendar.set(Calendar.SECOND, 0);
        mTimesEditText.setText("1");
        mRepeatType = 0;

        Database database = new Database(this.getApplicationContext());
        mId = database.getLastId() + 1;
        database.close();

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

    public void timePicker(View view) {
        TimePickerDialog TimePicker = new TimePickerDialog(CreateActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                mCalendar.set(Calendar.HOUR_OF_DAY, hour);
                mCalendar.set(Calendar.MINUTE, minute);
                mTimeText.setText(DateAndTimeUtil.toStringReadableTime(mCalendar));
            }
        }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true);
        TimePicker.show();
    }

    public void datePicker(View view) {
        DatePickerDialog DatePicker = new DatePickerDialog(CreateActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker DatePicker, int year, int month, int dayOfMonth) {
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
        Calendar nowCalendar = Calendar.getInstance();
        String title = mTitleEditText.getText().toString();
        String content = mContentEditText.getText().toString();
        String forever = Boolean.toString(mForeverSwitch.isChecked());

        // Assign time depending on whether a value was selected
        String time;
        if (mTimeText.getText().equals(getResources().getString(R.string.time_now))) {
            time = DateAndTimeUtil.toStringTime(nowCalendar);
        } else {
            time = DateAndTimeUtil.toStringTime(mCalendar);
        }

        // Assign date depending on whether a value was selected
        String date;
        if (mDateText.getText().equals(getResources().getString(R.string.date_today))) {
            date = DateAndTimeUtil.toStringDate(nowCalendar);
        } else {
            date = DateAndTimeUtil.toStringDate(mCalendar);
        }

        // Assign previous timesToShow number if no number was entered
        int timesToShow;
        if (mTimesEditText.getText().toString().isEmpty() || Integer.parseInt(mTimesEditText.getText().toString()) == 0) {
            timesToShow = 1;
        } else {
            timesToShow = Integer.parseInt(mTimesEditText.getText().toString());
        }

        Database database = new Database(this.getApplicationContext());
        Notification notification = new Notification(mId, title, content, date + time, mRepeatType, forever, timesToShow, 0);
        database.add(notification);
        database.close();

        AlarmUtil.setAlarm(getApplicationContext(), notification.getId(), mCalendar);
        finish();
    }

    public void toggleSwitch(View view) {
        mForeverSwitch.toggle();
    }

    public void toggleTextColour() {
        if (mForeverSwitch.isChecked()) {
            mShowText.setTextColor(getResources().getColor(R.color.textLightGray));
            mTimesEditText.setTextColor(getResources().getColor(R.color.textLightGray));
        } else {
            mShowText.setTextColor(Color.BLACK);
            mTimesEditText.setTextColor(Color.BLACK);
        }
    }

    public void validateInput() {
        Calendar nowCalendar = Calendar.getInstance();
        Boolean isTimeNow = mTimeText.getText().equals(getResources().getString(R.string.time_now));
        Boolean isToday = mDateText.getText().equals(getResources().getString(R.string.date_today));

        if (isTimeNow) {
            mCalendar.set(Calendar.HOUR_OF_DAY, nowCalendar.get(Calendar.HOUR_OF_DAY));
            mCalendar.set(Calendar.MINUTE, nowCalendar.get(Calendar.MINUTE));
        }

        if (isToday) {
            mCalendar.set(Calendar.YEAR, nowCalendar.get(Calendar.YEAR));
            mCalendar.set(Calendar.MONTH, nowCalendar.get(Calendar.MONTH));
            mCalendar.set(Calendar.DAY_OF_MONTH, nowCalendar.get(Calendar.DAY_OF_MONTH));
        }

        // Check if selected date is before today's date
        if (DateAndTimeUtil.toLongDateAndTime(mCalendar) < DateAndTimeUtil.toLongDateAndTime(nowCalendar)) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_past_date), Toast.LENGTH_SHORT).show();
            mImageWarningTime.setVisibility(View.VISIBLE);
            mImageWarningDate.setVisibility(View.VISIBLE);
        } else if (mTitleEditText.getText().toString().trim().isEmpty()) {
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