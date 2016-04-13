package com.bleyl.recurrence.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bleyl.recurrence.R;
import com.bleyl.recurrence.models.Colour;
import com.bleyl.recurrence.models.Icon;
import com.bleyl.recurrence.models.Reminder;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper instance;
    private Context context;

    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "RECURRENCE_DB";

    private static final String NOTIFICATION_TABLE = "NOTIFICATIONS";
    private static final String COL_ID = "ID";
    private static final String COL_TITLE = "TITLE";
    private static final String COL_CONTENT = "CONTENT";
    private static final String COL_DATE_AND_TIME = "DATE_AND_TIME";
    private static final String COL_REPEAT_TYPE = "REPEAT_TYPE";
    private static final String COL_FOREVER = "FOREVER";
    private static final String COL_NUMBER_TO_SHOW = "NUMBER_TO_SHOW";
    private static final String COL_NUMBER_SHOWN = "NUMBER_SHOWN";
    private static final String COL_ICON = "ICON";
    private static final String COL_COLOUR = "COLOUR";
    private static final String COL_INTERVAL = "INTERVAL";

    private static final String ICON_TABLE = "ICONS";
    private static final String COL_ICON_ID = "ID";
    private static final String COL_ICON_NAME = "NAME";
    private static final String COL_ICON_USE_FREQUENCY = "USE_FREQUENCY";

    private static final String DAYS_OF_WEEK_TABLE = "DAYS_OF_WEEK";
    private static final String COL_SUNDAY = "SUNDAY";
    private static final String COL_MONDAY = "MONDAY";
    private static final String COL_TUESDAY = "TUESDAY";
    private static final String COL_WEDNESDAY = "WEDNESDAY";
    private static final String COL_THURSDAY = "THURSDAY";
    private static final String COL_FRIDAY = "FRIDAY";
    private static final String COL_SATURDAY = "SATURDAY";

    private static final String PICKER_COLOUR_TABLE = "PICKER_COLOURS";
    private static final String COL_PICKER_COLOUR = "COLOUR";
    private static final String COL_PICKER_DATE_AND_TIME = "DATE_AND_TIME";

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

    public void onCreate(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE " + NOTIFICATION_TABLE + " ("
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

        database.execSQL("CREATE TABLE " + ICON_TABLE + " ("
                + COL_ICON_ID + " INTEGER PRIMARY KEY, "
                + COL_ICON_NAME + " TEXT, "
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

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            database.execSQL("ALTER TABLE " + NOTIFICATION_TABLE + " ADD " + COL_ICON + " TEXT;");
            database.execSQL("ALTER TABLE " + NOTIFICATION_TABLE + " ADD " + COL_COLOUR + " TEXT;");
            database.execSQL("UPDATE " + NOTIFICATION_TABLE + " SET " + COL_ICON + " = '" + DEFAULT_ICON + "';");
            database.execSQL("UPDATE " + NOTIFICATION_TABLE + " SET " + COL_COLOUR + " = '" + DEFAULT_COLOUR + "';");
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
            database.execSQL("ALTER TABLE " + NOTIFICATION_TABLE + " ADD " + COL_INTERVAL + " INTEGER;");
            database.execSQL("UPDATE " + NOTIFICATION_TABLE + " SET " + COL_INTERVAL + " = 1;");
            database.execSQL("UPDATE " + NOTIFICATION_TABLE + " SET " + COL_COLOUR + " = '" + DEFAULT_COLOUR + "' WHERE "
                    + COL_COLOUR + " == '" + OLD_COLOUR + "';");
            database.execSQL("UPDATE " + NOTIFICATION_TABLE + " SET " + COL_REPEAT_TYPE + " = " + COL_REPEAT_TYPE + " + 1 WHERE "
                    + COL_REPEAT_TYPE + " != 0");
            database.execSQL("CREATE TABLE " + PICKER_COLOUR_TABLE + " ("
                    + COL_PICKER_COLOUR + " INTEGER PRIMARY KEY, "
                    + COL_PICKER_DATE_AND_TIME + " INTEGER);");

            addAllColours(database);
        }
    }

    public void addNotification(Reminder reminder) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ID, reminder.getId());
        values.put(COL_TITLE, reminder.getTitle());
        values.put(COL_CONTENT, reminder.getContent());
        values.put(COL_DATE_AND_TIME, reminder.getDateAndTime());
        values.put(COL_REPEAT_TYPE, reminder.getRepeatType());
        values.put(COL_FOREVER, reminder.getForeverState());
        values.put(COL_NUMBER_TO_SHOW, reminder.getNumberToShow());
        values.put(COL_NUMBER_SHOWN, reminder.getNumberShown());
        values.put(COL_ICON, reminder.getIcon());
        values.put(COL_COLOUR, reminder.getColour());
        values.put(COL_INTERVAL, reminder.getInterval());
        database.replace(NOTIFICATION_TABLE, null, values);
    }

    public int getLastNotificationId() {
        int data = 0;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT " + COL_ID + " FROM " + NOTIFICATION_TABLE + " ORDER BY " + COL_ID + " DESC LIMIT 1", null);
        if (cursor != null && cursor.moveToFirst()) {
            data = cursor.getInt(0);
            cursor.close();
        }
        return data;
    }

    public List<Reminder> getNotificationList(int remindersType) {
        List<Reminder> reminderList = new ArrayList<>();
        String query;

        switch (remindersType) {
            case Reminder.ACTIVE:
            default:
                query = "SELECT * FROM " + NOTIFICATION_TABLE + " WHERE " + COL_NUMBER_SHOWN + " < " + COL_NUMBER_TO_SHOW + " OR " + COL_FOREVER + " = 'true' " + " ORDER BY " + COL_DATE_AND_TIME;
                break;
            case Reminder.INACTIVE:
                query = "SELECT * FROM " + NOTIFICATION_TABLE + " WHERE " + COL_NUMBER_SHOWN + " = " + COL_NUMBER_TO_SHOW + " AND " + COL_FOREVER + " = 'false' " + " ORDER BY " + COL_DATE_AND_TIME + " DESC";
                break;
        }

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Reminder reminder = new Reminder();
                reminder.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                reminder.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)));
                reminder.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT)));
                reminder.setDateAndTime(cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE_AND_TIME)));
                reminder.setRepeatType(cursor.getInt(cursor.getColumnIndexOrThrow(COL_REPEAT_TYPE)));
                reminder.setForeverState(cursor.getString(cursor.getColumnIndexOrThrow(COL_FOREVER)));
                reminder.setNumberToShow(cursor.getInt(cursor.getColumnIndexOrThrow(COL_NUMBER_TO_SHOW)));
                reminder.setNumberShown(cursor.getInt(cursor.getColumnIndexOrThrow(COL_NUMBER_SHOWN)));
                reminder.setIcon(cursor.getString(cursor.getColumnIndexOrThrow(COL_ICON)));
                reminder.setColour(cursor.getString(cursor.getColumnIndexOrThrow(COL_COLOUR)));
                reminder.setInterval(cursor.getInt(cursor.getColumnIndexOrThrow(COL_INTERVAL)));

                if (reminder.getRepeatType() == Reminder.SPECIFIC_DAYS) {
                    getDaysOfWeek(reminder, database);
                }
                reminderList.add(reminder);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return reminderList;
    }

    public Reminder getNotification(int id) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + NOTIFICATION_TABLE + " WHERE " + COL_ID + " = ? LIMIT 1", new String[]{String.valueOf(id)});

        cursor.moveToFirst();
        Reminder reminder = new Reminder();
        reminder.setId(id);
        reminder.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)));
        reminder.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT)));
        reminder.setDateAndTime(cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE_AND_TIME)));
        reminder.setRepeatType(cursor.getInt(cursor.getColumnIndexOrThrow(COL_REPEAT_TYPE)));
        reminder.setForeverState(cursor.getString(cursor.getColumnIndexOrThrow(COL_FOREVER)));
        reminder.setNumberToShow(cursor.getInt(cursor.getColumnIndexOrThrow(COL_NUMBER_TO_SHOW)));
        reminder.setNumberShown(cursor.getInt(cursor.getColumnIndexOrThrow(COL_NUMBER_SHOWN)));
        reminder.setIcon(cursor.getString(cursor.getColumnIndexOrThrow(COL_ICON)));
        reminder.setColour(cursor.getString(cursor.getColumnIndexOrThrow(COL_COLOUR)));
        reminder.setInterval(cursor.getInt(cursor.getColumnIndexOrThrow(COL_INTERVAL)));
        cursor.close();

        if (reminder.getRepeatType() == Reminder.SPECIFIC_DAYS) {
            getDaysOfWeek(reminder, database);
        }
        return reminder;
    }

    public void deleteNotification(Reminder reminder) {
        SQLiteDatabase database = this.getWritableDatabase();
        if (reminder.getRepeatType() == Reminder.SPECIFIC_DAYS) {
            database.delete(DAYS_OF_WEEK_TABLE, COL_ID + " = ?", new String[]{String.valueOf(reminder.getId())});
        }
        database.delete(NOTIFICATION_TABLE, COL_ID + " = ?", new String[]{String.valueOf(reminder.getId())});
    }

    public boolean isNotificationPresent(int id) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + NOTIFICATION_TABLE + " WHERE " + COL_ID + " = ? LIMIT 1", new String[]{String.valueOf(id)});
        boolean result = cursor.moveToFirst();
        cursor.close();
        return result;
    }

    private void getDaysOfWeek(Reminder reminder, SQLiteDatabase database) {
        Cursor cursor = database.rawQuery("SELECT * FROM " + DAYS_OF_WEEK_TABLE + " WHERE " + COL_ID + " = ? LIMIT 1", new String[]{String.valueOf(reminder.getId())});
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
        for (int i = 0; i < icons.length; i++) {
            ContentValues values = new ContentValues();
            values.put(COL_ICON_ID, i);
            values.put(COL_ICON_NAME, icons[i]);
            values.put(COL_ICON_USE_FREQUENCY, 0);
            database.insert(ICON_TABLE, null, values);
        }
    }

    public List<Icon> getIconList() {
        List<Icon> iconList = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + ICON_TABLE + " ORDER BY " + COL_ICON_USE_FREQUENCY + " DESC", null);

        if (cursor.moveToFirst()) {
            do {
                Icon icon = new Icon();
                icon.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ICON_ID)));
                icon.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_ICON_NAME)));
                icon.setUseFrequency(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ICON_USE_FREQUENCY)));
                iconList.add(icon);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return iconList;
    }

    public void updateIcon(Icon icon) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ICON_ID, icon.getId());
        values.put(COL_ICON_NAME, icon.getName());
        values.put(COL_ICON_USE_FREQUENCY, icon.getUseFrequency());
        database.update(ICON_TABLE, values, COL_ICON_ID + " = ?", new String[]{String.valueOf(icon.getId())});
    }

    public int[] getColoursArray() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT " + COL_PICKER_COLOUR + " FROM " + PICKER_COLOUR_TABLE + " WHERE " + COL_PICKER_COLOUR + " != -7434610 ORDER BY " + COL_PICKER_DATE_AND_TIME + " DESC LIMIT 14", null);

        int[] colours;
        if (cursor.getCount() < 15) {
            colours = new int[cursor.getCount() + 1];
        } else {
            colours = new int[15];
        }

        int i = 0;
        colours[i] = -7434610;
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
        ContentValues values = new ContentValues();
        values.put(COL_PICKER_COLOUR, colour.getColour());
        values.put(COL_PICKER_DATE_AND_TIME, colour.getDateAndTime());
        database.replace(PICKER_COLOUR_TABLE, null, values);
    }

    private void addAllColours(SQLiteDatabase database) {
        int[] colours = context.getResources().getIntArray(R.array.int_colours_array);
        for(int colour : colours) {
            ContentValues values = new ContentValues();
            values.put(COL_PICKER_COLOUR, colour);
            values.put(COL_PICKER_DATE_AND_TIME, 0);
            database.insert(PICKER_COLOUR_TABLE, null, values);
        }
    }
}