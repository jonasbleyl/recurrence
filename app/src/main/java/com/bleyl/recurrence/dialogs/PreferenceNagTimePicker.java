package com.bleyl.recurrence.dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

import com.bleyl.recurrence.R;

public class PreferenceNagTimePicker extends DialogPreference{

    public static final int MAX_VALUE = 60;
    public static final int MIN_VALUE = 0;

    private NumberPicker mMinutePicker;
    private NumberPicker mSecondPicker;
    private SharedPreferences mSharedPreferences;

    public PreferenceNagTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.pref_nag_number_picker);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        setPersistent(false);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        setUpMinutePicker(view);
        setUpSecondPicker(view);
    }

    protected void setUpMinutePicker(View view) {
        mMinutePicker = (NumberPicker) view.findViewById(R.id.minutes_picker);
        mMinutePicker.setMaxValue(MAX_VALUE);
        mMinutePicker.setMinValue(MIN_VALUE);
        mMinutePicker.setValue(mSharedPreferences.getInt("nagMinutes", getContext().getResources().getInteger(R.integer.default_nag_minutes)));

        String[] minuteValues = new String[61];
        for (int i = 0; i < minuteValues.length; i++) {
            minuteValues[i] = String.format(getContext().getResources().getQuantityString(R.plurals.time_minute, i), i);
        }
        mMinutePicker.setDisplayedValues(minuteValues);
    }

    protected void setUpSecondPicker(View view) {
        mSecondPicker = (NumberPicker) view.findViewById(R.id.seconds_picker);
        mSecondPicker.setMaxValue(MAX_VALUE);
        mSecondPicker.setMinValue(MIN_VALUE);
        mSecondPicker.setValue(mSharedPreferences.getInt("nagSeconds", getContext().getResources().getInteger(R.integer.default_nag_seconds)));

        String[] secondValues = new String[61];
        for (int i = 0; i < secondValues.length; i++) {
            secondValues[i] = String.format(getContext().getResources().getQuantityString(R.plurals.time_second, i), i);
        }
        mSecondPicker.setDisplayedValues(secondValues);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("nagMinutes", mMinutePicker.getValue());
        editor.putInt("nagSeconds", mSecondPicker.getValue());
        editor.apply();
    }
}