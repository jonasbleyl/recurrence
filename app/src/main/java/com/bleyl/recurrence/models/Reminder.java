package com.bleyl.recurrence.models;

public class Reminder {
    private int mId;
    private String mTitle;
    private String mContent;
    private String mDateAndTime;
    private int mRepeatType;
    private String mForeverState;
    private int mNumberToShow;
    private int mNumberShown;
    private String mIcon;
    private String mColour;
    private boolean[] mDaysOfWeek;

    public int getId() {
        return mId;
    }

    public Reminder setId(int id) {
        mId = id;
        return this;
    }

    public String getTitle() {
        return mTitle;
    }

    public Reminder setTitle(String title) {
        mTitle = title;
        return this;
    }

    public String getContent() {
        return mContent;
    }

    public Reminder setContent(String content) {
        mContent = content;
        return this;
    }

    public String getDateAndTime() {
        return mDateAndTime;
    }

    public Reminder setDateAndTime(String dateAndTime) {
        mDateAndTime = dateAndTime;
        return this;
    }

    public String getDate() {
        return mDateAndTime.substring(0, 8);
    }

    public int getRepeatType() {
        return mRepeatType;
    }

    public Reminder setRepeatType(int repeatType) {
        mRepeatType = repeatType;
        return this;
    }

    public String getForeverState() {
        return mForeverState;
    }

    public Reminder setForeverState(String foreverState) {
        mForeverState = foreverState;
        return this;
    }

    public int getNumberToShow() {
        return mNumberToShow;
    }

    public Reminder setNumberToShow(int numberToShow) {
        mNumberToShow = numberToShow;
        return this;
    }

    public int getNumberShown() {
        return mNumberShown;
    }

    public Reminder setNumberShown(int numberShown) {
        mNumberShown = numberShown;
        return this;
    }

    public String getIcon() {
        return mIcon;
    }

    public Reminder setIcon(String icon) {
        mIcon = icon;
        return this;
    }

    public String getColour() {
        return mColour;
    }

    public Reminder setColour(String colour) {
        mColour = colour;
        return this;
    }

    public boolean[] getDaysOfWeek() {
        return mDaysOfWeek;
    }

    public Reminder setDaysOfWeek(boolean[] daysOfWeek) {
        mDaysOfWeek = daysOfWeek;
        return this;
    }
}