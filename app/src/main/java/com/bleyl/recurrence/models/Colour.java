package com.bleyl.recurrence.models;

public class Colour {
    private int mColour;
    private String mDateAndTime;

    public Colour(int colour, String dateAndTime) {
        mColour = colour;
        mDateAndTime = dateAndTime;
    }

    public String getDateAndTime() {
        return mDateAndTime;
    }

    public int getColour() {
        return mColour;
    }
}
