package com.bleyl.recurrence.models;

import android.content.ContentValues;
import android.database.Cursor;

import com.bleyl.recurrence.database.DatabaseHelper;

public class Icon {

    private int id;
    private String name;
    private int useFrequency;

    public static Icon createFromCursor(Cursor cursor) {
        Icon icon = new Icon();
        icon.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ICON_ID)));
        icon.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ICON_NAME)));
        icon.setUseFrequency(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ICON_USE_FREQUENCY)));
        return icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUseFrequency() {
        return useFrequency;
    }

    public void setUseFrequency(int useFrequency) {
        this.useFrequency = useFrequency;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_ICON_ID, getId());
        values.put(DatabaseHelper.COL_ICON_NAME, getName());
        values.put(DatabaseHelper.COL_ICON_USE_FREQUENCY, getUseFrequency());
        return values;
    }
}