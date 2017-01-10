package com.bleyl.recurrence.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;

import com.bleyl.recurrence.R;
import com.bleyl.recurrence.models.Colour;
import com.bleyl.recurrence.models.Icon;
import com.bleyl.recurrence.models.Reminder;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    @SuppressLint("StaticFieldLeak")
    private static DatabaseHelper instance;
    private Context context;

    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "RECURRENCE_DB";

    private static final String REMINDER_TABLE = "NOTIFICATIONS";
    public static final String COL_ID = "ID";
    public static final String COL_TITLE = "TITLE";
    public static final String COL_CONTENT = "CONTENT";
    public static final String COL_DATE_AND_TIME = "DATE_AND_TIME";
    public static final String COL_REPEAT_TYPE = "REPEAT_TYPE";
    public static final String COL_FOREVER = "FOREVER";
    public static final String COL_NUMBER_TO_SHOW = "NUMBER_TO_SHOW";
    public static final String COL_NUMBER_SHOWN = "NUMBER_SHOWN";
    public static final String COL_ICON = "ICON";
    public static final String COL_COLOUR = "COLOUR";
    public static final String COL_INTERVAL = "INTERVAL";

    private static final String ICON_TABLE = "ICONS";
    public static final String COL_ICON_ID = "ID";
    public static final String COL_ICON_NAME = "NAME";
    public static final String COL_ICON_USE_FREQUENCY = "USE_FREQUENCY";
    private static final String COL_ICON_INDEX = "NAME_INDEX";

    private static final String DAYS_OF_WEEK_TABLE = "DAYS_OF_WEEK";
    private static final String COL_SUNDAY = "SUNDAY";
    private static final String COL_MONDAY = "MONDAY";
    private static final String COL_TUESDAY = "TUESDAY";
    private static final String COL_WEDNESDAY = "WEDNESDAY";
    private static final String COL_THURSDAY = "THURSDAY";
    private static final String COL_FRIDAY = "FRIDAY";
    private static final String COL_SATURDAY = "SATURDAY";

    private static final String PICKER_COLOUR_TABLE = "PICKER_COLOURS";
    public static final String COL_PICKER_COLOUR = "COLOUR";
    public static final String COL_PICKER_DATE_AND_TIME = "DATE_AND_TIME";

    private static final String DEFAULT_ICON = "ic_notifications_white_24dp";
    private static final String DEFAULT_COLOUR = "#8E8E8E";
    private static final String OLD_COLOUR = "#9E9E9E";

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Constructor is private to prevent direct instantiation
     * Call made to static method getInstance() instead
     */
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE " + REMINDER_TABLE + " ("
                + COL_ID + " INTEGER PRIMARY KEY, "
                + COL_TITLE + " TEXT, "
                + COL_CONTENT + " TEXT, "
                + COL_DATE_AND_TIME + " INTEGER, "
                + COL_REPEAT_TYPE + " INTEGER, "
                + COL_FOREVER + " TEXT, "
                + COL_NUMBER_TO_SHOW + " INTEGER, "
                + COL_NUMBER_SHOWN + " INTEGER, "
                + COL_ICON + " TEXT, "
                + COL_COLOUR + " TEXT, "
                + COL_INTERVAL + " INTEGER) ");

        database.execSQL("CREATE TABLE "+ ICON_TABLE + " ("
                + COL_ICON_ID + " INTEGER PRIMARY KEY, "
                + COL_ICON_NAME + " TEXT UNIQUE, "
                + COL_ICON_USE_FREQUENCY + " INTEGER) ");

        database.execSQL("CREATE TABLE " + DAYS_OF_WEEK_TABLE + " ("
                + COL_ID + " INTEGER PRIMARY KEY, "
                + COL_SUNDAY + " TEXT, "
                + COL_MONDAY + " TEXT, "
                + COL_TUESDAY + " TEXT, "
                + COL_WEDNESDAY + " TEXT, "
                + COL_THURSDAY + " TEXT, "
                + COL_FRIDAY + " TEXT, "
                + COL_SATURDAY + " TEXT) ");

        database.execSQL("CREATE TABLE " + PICKER_COLOUR_TABLE + " ("
                + COL_PICKER_COLOUR + " INTEGER PRIMARY KEY, "
                + COL_PICKER_DATE_AND_TIME + " INTEGER) ");

        addAllIcons(database);
        addAllColours(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            database.execSQL("ALTER TABLE " + REMINDER_TABLE
                    + " ADD " + COL_ICON + " TEXT;");
            database.execSQL("ALTER TABLE " + REMINDER_TABLE
                    + " ADD " + COL_COLOUR + " TEXT;");
            database.execSQL("UPDATE " + REMINDER_TABLE
                    + " SET " + COL_ICON + " = '" + DEFAULT_ICON + "';");
            database.execSQL("UPDATE " + REMINDER_TABLE
                    + " SET " + COL_COLOUR + " = '" + DEFAULT_COLOUR + "';");
            database.execSQL("CREATE TABLE " + ICON_TABLE + " ("
                    + COL_ICON_ID + " INTEGER PRIMARY KEY, "
                    + COL_ICON_NAME + " TEXT, "
                    + COL_ICON_USE_FREQUENCY + " INTEGER) ");

            addAllIcons(database);
        }

        if (oldVersion < 3) {
            database.execSQL("CREATE TABLE " + DAYS_OF_WEEK_TABLE + " ("
                    + COL_ID + " INTEGER PRIMARY KEY, "
                    + COL_SUNDAY + " TEXT, "
                    + COL_MONDAY + " TEXT, "
                    + COL_TUESDAY + " TEXT, "
                    + COL_WEDNESDAY + " TEXT, "
                    + COL_THURSDAY + " TEXT, "
                    + COL_FRIDAY + " TEXT, "
                    + COL_SATURDAY + " TEXT) ");
        }

        if (oldVersion < 4) {
            database.execSQL("ALTER TABLE " + REMINDER_TABLE
                    + " ADD " + COL_INTERVAL + " INTEGER;");
            database.execSQL("UPDATE " + REMINDER_TABLE
                    + " SET " + COL_INTERVAL + " = 1;");
            database.execSQL("UPDATE " + REMINDER_TABLE
                    + " SET " + COL_COLOUR + " = '" + DEFAULT_COLOUR + "'"
                    + " WHERE " + COL_COLOUR + " == '" + OLD_COLOUR + "';");
            database.execSQL("UPDATE " + REMINDER_TABLE
                    + " SET " + COL_REPEAT_TYPE + " = " + COL_REPEAT_TYPE + " + 1"
                    + " WHERE " + COL_REPEAT_TYPE + " != 0");
            database.execSQL("CREATE TABLE " + PICKER_COLOUR_TABLE + " ("
                    + COL_PICKER_COLOUR + " INTEGER PRIMARY KEY, "
                    + COL_PICKER_DATE_AND_TIME + " INTEGER);");

            addAllColours(database);
        }

        if (oldVersion < 5) {
            database.execSQL("CREATE UNIQUE INDEX "
                    + COL_ICON_INDEX + " ON "
                    + ICON_TABLE  + " ("
                    + COL_ICON_NAME + ")");

            addAllIcons(database);
        }
    }

    public void addReminder(Reminder reminder) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.replace(REMINDER_TABLE, null, reminder.toContentValues());
    }

    public int getLastReminderId() {
        int data = 0;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT " + COL_ID + " FROM " + REMINDER_TABLE
                + " ORDER BY " + COL_ID + " DESC LIMIT 1", null);
        if (cursor != null && cursor.moveToFirst()) {
            data = cursor.getInt(0);
            cursor.close();
        }
        return data;
    }

    public List<Reminder> getReminderList(int remindersType) {
        List<Reminder> reminderList = new ArrayList<>();
        String query;

        switch (remindersType) {
            case Reminder.ACTIVE:
            default:
                query = "SELECT * FROM " + REMINDER_TABLE
                        + " WHERE " + COL_NUMBER_SHOWN + " < " + COL_NUMBER_TO_SHOW
                        + " OR " + COL_FOREVER + " = 'true' "
                        + " ORDER BY " + COL_DATE_AND_TIME;
                break;
            case Reminder.INACTIVE:
                query = "SELECT * FROM " + REMINDER_TABLE
                        + " WHERE " + COL_NUMBER_SHOWN + " = " + COL_NUMBER_TO_SHOW
                        + " AND " + COL_FOREVER + " = 'false' "
                        + " ORDER BY " + COL_DATE_AND_TIME + " DESC";
                break;
        }

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Reminder reminder = Reminder.createFromCursor(cursor);
                if (reminder.getRepeatType() == Reminder.SPECIFIC_DAYS)
                    getDaysOfWeek(reminder, database);
                reminderList.add(reminder);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return reminderList;
    }

    public Reminder getReminder(int id) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + REMINDER_TABLE
                + " WHERE " + COL_ID + " = ? LIMIT 1", new String[]{String.valueOf(id)});

        cursor.moveToFirst();
        Reminder reminder = Reminder.createFromCursor(cursor);
        if (reminder.getRepeatType() == Reminder.SPECIFIC_DAYS)
            getDaysOfWeek(reminder, database);
        cursor.close();
        return reminder;
    }

    public void deleteReminder(Reminder reminder) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(DAYS_OF_WEEK_TABLE, COL_ID + " = ?", new String[]{String.valueOf(reminder.getId())});
        database.delete(REMINDER_TABLE, COL_ID + " = ?", new String[]{String.valueOf(reminder.getId())});
    }

    public boolean isReminderPresent(int id) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + REMINDER_TABLE
                + " WHERE " + COL_ID + " = ? LIMIT 1", new String[]{String.valueOf(id)});
        boolean result = cursor.moveToFirst();
        cursor.close();
        return result;
    }

    private void getDaysOfWeek(Reminder reminder, SQLiteDatabase database) {
        Cursor cursor = database.rawQuery("SELECT * FROM " + DAYS_OF_WEEK_TABLE
                + " WHERE " + COL_ID + " = ? LIMIT 1", new String[]{String.valueOf(reminder.getId())});
        cursor.moveToFirst();
        boolean[] daysOfWeek = new boolean[7];
        for (int i = 0; i < 7; i++) {
            daysOfWeek[i] = Boolean.parseBoolean(cursor.getString(i + 1));
        }
        reminder.setDaysOfWeek(daysOfWeek);
        cursor.close();
    }

    public void addDaysOfWeek(Reminder reminder) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ID, reminder.getId());
        values.put(COL_SUNDAY, Boolean.toString(reminder.getDaysOfWeek()[0]));
        values.put(COL_MONDAY, Boolean.toString(reminder.getDaysOfWeek()[1]));
        values.put(COL_TUESDAY, Boolean.toString(reminder.getDaysOfWeek()[2]));
        values.put(COL_WEDNESDAY, Boolean.toString(reminder.getDaysOfWeek()[3]));
        values.put(COL_THURSDAY, Boolean.toString(reminder.getDaysOfWeek()[4]));
        values.put(COL_FRIDAY, Boolean.toString(reminder.getDaysOfWeek()[5]));
        values.put(COL_SATURDAY, Boolean.toString(reminder.getDaysOfWeek()[6]));
        database.replace(DAYS_OF_WEEK_TABLE, null, values);
    }

    private void addAllIcons(SQLiteDatabase database) {
        String[] icons = context.getResources().getStringArray(R.array.icons_string_array);
        for (String icon : icons) {
            ContentValues values = new ContentValues();
            values.put(COL_ICON_NAME, icon);
            values.put(COL_ICON_USE_FREQUENCY, 0);
            database.insertWithOnConflict(ICON_TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        }
    }

    public List<Icon> getIconList() {
        List<Icon> iconList = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + ICON_TABLE
                + " ORDER BY " + COL_ICON_USE_FREQUENCY + " DESC", null);

        if (cursor.moveToFirst()) {
            do {
                iconList.add(Icon.createFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return iconList;
    }

    public void updateIcon(Icon icon) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.update(ICON_TABLE, icon.toContentValues(), COL_ICON_ID + " = ?",
                new String[]{String.valueOf(icon.getId())});
    }

    public int[] getColoursArray() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT " + COL_PICKER_COLOUR + " FROM " + PICKER_COLOUR_TABLE
                + " WHERE " + COL_PICKER_COLOUR + " != " + Color.parseColor(DEFAULT_COLOUR)
                + " ORDER BY " + COL_PICKER_DATE_AND_TIME + " DESC LIMIT 14", null);

        int[] colours;
        if (cursor.getCount() < 15)
            colours = new int[cursor.getCount() + 1];
        else
            colours = new int[15];

        int i = 0;
        colours[i] = Color.parseColor(DEFAULT_COLOUR);
        if (cursor.moveToFirst()) {
            do {
                colours[++i] = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PICKER_COLOUR));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return colours;
    }

    public void addColour(Colour colour) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.replace(PICKER_COLOUR_TABLE, null, colour.toContentValues());
    }

    private void addAllColours(SQLiteDatabase database) {
        String[] colours = context.getResources().getStringArray(R.array.colours_array);
        for(String colour : colours) {
            ContentValues values = new ContentValues();
            values.put(COL_PICKER_COLOUR, Color.parseColor(colour));
            values.put(COL_PICKER_DATE_AND_TIME, 0);
            database.insert(PICKER_COLOUR_TABLE, null, values);
        }
    }
}