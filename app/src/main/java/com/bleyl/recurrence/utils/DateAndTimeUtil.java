package com.bleyl.recurrence.utils;

import android.content.Context;
import android.text.format.DateFormat;

import com.bleyl.recurrence.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateAndTimeUtil {

    private static final SimpleDateFormat DATE_AND_TIME_FORMAT = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
    private static final SimpleDateFormat DATE_AND_TIME_WITH_SECONDS_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
    private static final SimpleDateFormat READABLE_DAY_MONTH_FORMAT = new SimpleDateFormat("d MMMM", Locale.getDefault());
    private static final SimpleDateFormat READABLE_DAY_MONTH_YEAR_FORMAT = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
    private static final SimpleDateFormat READABLE_TIME_24_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static final SimpleDateFormat READABLE_TIME_FORMAT = new SimpleDateFormat("h:mm a", Locale.getDefault());
    private static final SimpleDateFormat WEEK_DAYS_FORMAT = new SimpleDateFormat("EEEE", Locale.getDefault());
    private static final SimpleDateFormat SHORT_WEEK_DAYS_FORMAT = new SimpleDateFormat("E", Locale.getDefault());

    public static String toStringReadableDate(Calendar calendar) {
        java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance(java.text.DateFormat.FULL, Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    public static String toStringReadableTime(Calendar calendar, Context context) {
        if (DateFormat.is24HourFormat(context)) {
            return READABLE_TIME_24_FORMAT.format(calendar.getTime());
        } else {
            return READABLE_TIME_FORMAT.format(calendar.getTime());
        }
    }

    public static Long toLongDateAndTime(Calendar calendar) {
        return Long.parseLong(DATE_AND_TIME_FORMAT.format(calendar.getTime()));
    }

    public static String toStringDateAndTime(Calendar calendar) {
        return DATE_AND_TIME_FORMAT.format(calendar.getTime());
    }

    public static String toStringDateTimeWithSeconds(Calendar calendar) {
        return DATE_AND_TIME_WITH_SECONDS_FORMAT.format(calendar.getTime());
    }

    public static String getAppropriateDateFormat(Context context, Calendar calendar) {
        if (isThisYear(calendar)) {
            if (isThisMonth(calendar) && isThisDayOfMonth(calendar)) {
                return context.getString(R.string.date_today);
            } else {
                return READABLE_DAY_MONTH_FORMAT.format(calendar.getTime());
            }
        } else {
            return READABLE_DAY_MONTH_YEAR_FORMAT.format(calendar.getTime());
        }
    }

    public static Calendar parseDateAndTime(String dateAndTime) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(DATE_AND_TIME_FORMAT.parse(dateAndTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return calendar;
    }

    public static String[] getWeekDays() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        String[] weekDays = new String[7];
        for (int i = 0; i < 7; i++) {
            weekDays[i] = WEEK_DAYS_FORMAT.format(calendar.getTime());
            calendar.add(Calendar.DATE, 1);
        }
        return weekDays;
    }

    public static String[] getShortWeekDays() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        String[] weekDays = new String[7];
        for (int i = 0; i < 7; i++) {
            weekDays[i] = SHORT_WEEK_DAYS_FORMAT.format(calendar.getTime());
            calendar.add(Calendar.DATE, 1);
        }
        return weekDays;
    }

    private static Boolean isThisYear(Calendar calendar) {
        Calendar nowCalendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR) == nowCalendar.get(Calendar.YEAR);
    }

    private static Boolean isThisMonth(Calendar calendar) {
        Calendar nowCalendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) == nowCalendar.get(Calendar.MONTH);
    }

    private static Boolean isThisDayOfMonth(Calendar calendar) {
        Calendar nowCalendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_MONTH) == nowCalendar.get(Calendar.DAY_OF_MONTH);
    }
}