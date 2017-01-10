package com.bleyl.recurrence.models;

import android.content.ContentValues;

import com.bleyl.recurrence.database.DatabaseHelper;

public class Colour {

    private int colour;
    private String dateAndTime;

    public Colour(int colour, String dateAndTime) {
        this.colour = colour;
        this.dateAndTime = dateAndTime;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_PICKER_COLOUR, colour);
        values.put(DatabaseHelper.COL_PICKER_DATE_AND_TIME, dateAndTime);
        return values;
    }
}