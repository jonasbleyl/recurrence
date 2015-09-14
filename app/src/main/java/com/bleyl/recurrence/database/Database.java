package com.bleyl.recurrence.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bleyl.recurrence.R;
import com.bleyl.recurrence.enums.NotificationsType;
import com.bleyl.recurrence.models.Icon;
import com.bleyl.recurrence.models.Notification;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {

    private Context mContext;

    private static final int DATABASE_VERSION = 2;
    private static final String DB_NAME = "RECURRENCE_DB";

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

    private static final String ICON_TABLE = "ICONS";
    private static final String COL_ICON_ID = "ID";
    private static final String COL_ICON_NAME = "NAME";
    private static final String COL_ICON_USE_FREQUENCY = "USE_FREQUENCY";

    private static final String DEFAULT_ICON = "ic_notifications_white_24dp";
    private static final String DEFAULT_COLOUR = "#9E9E9E";

    public Database(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public void onCreate(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE " + NOTIFICATION_TABLE + " ("
                + COL_ID + " INTEGER PRIMARY KEY, "
                + COL_TITLE + " TEXT, "
                + COL_CONTENT + " TEXT, "
                + COL_DATE_AND_TIME + " INTEGER, "
                + COL_REPEAT_TYPE + " INTEGER, "
                + COL_FOREVER + " BOOLEAN, "
                + COL_NUMBER_TO_SHOW + " INTEGER, "
                + COL_NUMBER_SHOWN + " INTEGER, "
                + COL_ICON + " TEXT, "
                + COL_COLOUR + " TEXT) ");

        database.execSQL("CREATE TABLE " + ICON_TABLE + " ("
                + COL_ICON_ID + " INTEGER PRIMARY KEY, "
                + COL_ICON_NAME + " TEXT, "
                + COL_ICON_USE_FREQUENCY + " INTEGER) ");

        addAllIcons(database);
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            database.execSQL("ALTER TABLE " + NOTIFICATION_TABLE + " ADD " + COL_ICON + " TEXT");
            database.execSQL("ALTER TABLE " + NOTIFICATION_TABLE + " ADD " + COL_COLOUR + " TEXT");
            database.execSQL("UPDATE " + NOTIFICATION_TABLE + " SET " + COL_ICON + " = '" + DEFAULT_ICON + "';");
            database.execSQL("UPDATE " + NOTIFICATION_TABLE + " SET " + COL_COLOUR + " = '" + DEFAULT_COLOUR + "';");
            database.execSQL("CREATE TABLE " + ICON_TABLE + " (" + COL_ICON_ID + " INTEGER PRIMARY KEY, " + COL_ICON_NAME + " TEXT, " + COL_ICON_USE_FREQUENCY + " INTEGER) ");
            addAllIcons(database);
        }
    }

    public void add(Notification notification) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ID, notification.getId());
        values.put(COL_TITLE, notification.getTitle());
        values.put(COL_CONTENT, notification.getContent());
        values.put(COL_DATE_AND_TIME, notification.getDateAndTime());
        values.put(COL_REPEAT_TYPE, notification.getRepeatType());
        values.put(COL_FOREVER, notification.getForeverState());
        values.put(COL_NUMBER_TO_SHOW, notification.getNumberToShow());
        values.put(COL_NUMBER_SHOWN, notification.getNumberShown());
        values.put(COL_ICON, notification.getIcon());
        values.put(COL_COLOUR, notification.getColour());
        database.insert(NOTIFICATION_TABLE, null, values);
    }

    public int getLastId() {
        int data = 0;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT " + COL_ID + " FROM " + NOTIFICATION_TABLE + " ORDER BY " + COL_ID + " DESC LIMIT 1", null);
        if (cursor != null && cursor.moveToFirst()) {
            data = cursor.getInt(0);
            cursor.close();
        }
        return data;
    }

    public List<Notification> getNotificationList(NotificationsType notificationsType) {
        List<Notification> notificationList = new ArrayList<>();
        String query;

        switch (notificationsType) {
            case ACTIVE:
            default:
                query = "SELECT * FROM " + NOTIFICATION_TABLE + " WHERE " + COL_NUMBER_SHOWN + " < " + COL_NUMBER_TO_SHOW + " OR " + COL_FOREVER + " = 'true' " + " ORDER BY " + COL_DATE_AND_TIME;
                break;
            case INACTIVE:
                query = "SELECT * FROM " + NOTIFICATION_TABLE + " WHERE " + COL_NUMBER_SHOWN + " = " + COL_NUMBER_TO_SHOW + " AND " + COL_FOREVER + " = 'false' " + " ORDER BY " + COL_DATE_AND_TIME + " DESC";
                break;
        }

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Notification notification = new Notification();
                notification.setId(cursor.getInt(0));
                notification.setTitle(cursor.getString(1));
                notification.setContent(cursor.getString(2));
                notification.setDateAndTime(cursor.getString(3));
                notification.setRepeatType(cursor.getInt(4));
                notification.setForeverState(cursor.getString(5));
                notification.setNumberToShow(cursor.getInt(6));
                notification.setNumberShown(cursor.getInt(7));
                notification.setIcon(cursor.getString(8));
                notification.setColour(cursor.getString(9));
                notificationList.add(notification);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notificationList;
    }

    public Notification getNotification(int id) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + NOTIFICATION_TABLE + " WHERE " + COL_ID + " = ? LIMIT 1", new String[] {String.valueOf(id)});

        cursor.moveToFirst();
        Notification notification = new Notification();
        notification.setId(id);
        notification.setTitle(cursor.getString(1));
        notification.setContent(cursor.getString(2));
        notification.setDateAndTime(cursor.getString(3));
        notification.setRepeatType(cursor.getInt(4));
        notification.setForeverState(cursor.getString(5));
        notification.setNumberToShow(cursor.getInt(6));
        notification.setNumberShown(cursor.getInt(7));
        notification.setIcon(cursor.getString(8));
        notification.setColour(cursor.getString(9));
        cursor.close();
        return notification;
    }

    public void update(Notification notification) {
        SQLiteDatabase database = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, notification.getTitle());
        values.put(COL_CONTENT, notification.getContent());
        values.put(COL_DATE_AND_TIME, notification.getDateAndTime());
        values.put(COL_REPEAT_TYPE, notification.getRepeatType());
        values.put(COL_FOREVER, notification.getForeverState());
        values.put(COL_NUMBER_TO_SHOW, notification.getNumberToShow());
        values.put(COL_NUMBER_SHOWN, notification.getNumberShown());
        values.put(COL_ICON, notification.getIcon());
        values.put(COL_COLOUR, notification.getColour());
        database.update(NOTIFICATION_TABLE, values, COL_ID + " = ?", new String[] {String.valueOf(notification.getId())});
    }

    public void delete(Notification notification) {
        SQLiteDatabase database = this.getReadableDatabase();
        database.delete(NOTIFICATION_TABLE, COL_ID + " = ?", new String[] {String.valueOf(notification.getId())});
    }

    public boolean isPresent(int id) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + NOTIFICATION_TABLE + " WHERE " + COL_ID + " = ? LIMIT 1", new String[]{String.valueOf(id)});
        boolean result = cursor.moveToFirst();
        cursor.close();
        return result;
    }

    private void addAllIcons(SQLiteDatabase database) {
        String[] icons = mContext.getResources().getStringArray(R.array.icons_string_array);
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
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + ICON_TABLE + " ORDER BY " + COL_ICON_USE_FREQUENCY + " DESC", null);

        if (cursor.moveToFirst()) {
            do {
                Icon icon = new Icon();
                icon.setId(cursor.getInt(0));
                icon.setName(cursor.getString(1));
                icon.setUseFrequency(cursor.getInt(2));
                iconList.add(icon);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return iconList;
    }

    public void updateIcon(Icon icon) {
        SQLiteDatabase database = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ICON_ID, icon.getId());
        values.put(COL_ICON_NAME, icon.getName());
        values.put(COL_ICON_USE_FREQUENCY, icon.getUseFrequency());
        database.update(ICON_TABLE, values, COL_ICON_ID + " = ?", new String[] {String.valueOf(icon.getId())});
    }
}