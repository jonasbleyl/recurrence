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

    private NumberPicker minutePicker;
    private NumberPicker secondPicker;
    private SharedPreferences sharedPreferences;

    public PreferenceNagTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.number_picker);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        setPersistent(false);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        setUpMinutePicker(view);
        setUpSecondPicker(view);
    }

    protected void setUpMinutePicker(View view) {
        minutePicker = (NumberPicker) view.findViewById(R.id.picker1);
        minutePicker.setMaxValue(MAX_VALUE);
        minutePicker.setMinValue(MIN_VALUE);
        minutePicker.setValue(sharedPreferences.getInt("nagMinutes", getContext().getResources().getInteger(R.integer.default_nag_minutes)));

        String[] minuteValues = new String[61];
        for (int i = 0; i < minuteValues.length; i++) {
            minuteValues[i] = String.format(getContext().getResources().getQuantityString(R.plurals.time_minute, i), i);
        }
        minutePicker.setDisplayedValues(minuteValues);
    }

    protected void setUpSecondPicker(View view) {
        secondPicker = (NumberPicker) view.findViewById(R.id.picker2);
        secondPicker.setMaxValue(MAX_VALUE);
        secondPicker.setMinValue(MIN_VALUE);
        secondPicker.setValue(sharedPreferences.getInt("nagSeconds", getContext().getResources().getInteger(R.integer.default_nag_seconds)));

        String[] secondValues = new String[61];
        for (int i = 0; i < secondValues.length; i++) {
            secondValues[i] = String.format(getContext().getResources().getQuantityString(R.plurals.time_second, i), i);
        }
        secondPicker.setDisplayedValues(secondValues);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("nagMinutes", minutePicker.getValue());
            editor.putInt("nagSeconds", secondPicker.getValue());
            editor.apply();
        }
    }
}