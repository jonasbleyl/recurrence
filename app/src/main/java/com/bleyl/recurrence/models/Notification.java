package com.bleyl.recurrence.models;

public class Notification {
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

    public Notification() {}

    public Notification(int id, String title, String content, String dateAndTime, int repeatType, String foreverState, int numberToShow, int numberShown, String icon, String colour) {
        mId = id;
        mTitle = title;
        mContent = content;
        mDateAndTime = dateAndTime;
        mRepeatType = repeatType;
        mForeverState = foreverState;
        mNumberToShow = numberToShow;
        mNumberShown = numberShown;
        mIcon = icon;
        mColour = colour;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getDateAndTime() {
        return mDateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        mDateAndTime = dateAndTime;
    }

    public String getDate() {
        return mDateAndTime.substring(0, 8);
    }

    public String getTime() {
        return mDateAndTime.substring(8, 12);
    }

    public int getRepeatType() {
        return mRepeatType;
    }

    public void setRepeatType(int repeatType) {
        mRepeatType = repeatType;
    }

    public String getForeverState() {
        return mForeverState;
    }

    public void setForeverState(String foreverState) {
        mForeverState = foreverState;
    }

    public int getNumberToShow() {
        return mNumberToShow;
    }

    public void setNumberToShow(int numberToShow) {
        mNumberToShow = numberToShow;
    }

    public int getNumberShown() {
        return mNumberShown;
    }

    public void setNumberShown(int numberShown) {
        mNumberShown = numberShown;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public String getColour() {
        return mColour;
    }

    public void setColour(String colour) {
        mColour = colour;
    }
}