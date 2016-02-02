package com.bleyl.recurrence.utils;

import android.content.Context;

import com.bleyl.recurrence.R;

public class TextFormatUtil {

    public static String formatDaysOfWeekText(Context context, boolean[] daysOfWeek) {
        final String[] shortWeekDays = DateAndTimeUtil.getShortWeekDays();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(context.getString(R.string.repeats_on));
        stringBuilder.append(" ");
        for (int i = 0; i < daysOfWeek.length; i++) {
            if (daysOfWeek[i]) {
                stringBuilder.append(shortWeekDays[i]);
                stringBuilder.append(" ");
            }
        }
        return stringBuilder.toString();
    }

    public static String formatAdvancedRepeatText(Context context, int repeatType, int interval) {
        String typeText;
        switch (repeatType) {
            default:
            case 1: typeText = context.getResources().getQuantityString(R.plurals.hour, interval); break;
            case 2: typeText = context.getResources().getQuantityString(R.plurals.day, interval); break;
            case 3: typeText = context.getResources().getQuantityString(R.plurals.week, interval); break;
            case 4: typeText = context.getResources().getQuantityString(R.plurals.month, interval); break;
            case 5: typeText = context.getResources().getQuantityString(R.plurals.year, interval); break;
        }
        return context.getString(R.string.repeats_every, interval, typeText);
    }
}