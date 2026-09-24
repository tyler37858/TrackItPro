package com.example.trackitpro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "trackitpro.db";
    private static final int DATABASE_VERSION = 3;

    // users table
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "_id";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";

    // events table
    public static final String TABLE_EVENTS = "events";
    public static final String COL_EVENT_ID = "_id";
    public static final String COL_EVENT_USER_ID = "user_id";
    public static final String COL_EVENT_TITLE = "title";
    public static final String COL_EVENT_DATE = "date";
    public static final String COL_EVENT_TRIGGER = "trigger_time";
    public static final String COL_EVENT_NOTES = "notes";
    public static final String COL_EVENT_IMAGE_URI = "image_uri";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // creates tables first time DB is made
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsers = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT UNIQUE NOT NULL, " +
                COL_PASSWORD + " TEXT NOT NULL" +
                ");";

        String createEvents = "CREATE TABLE " + TABLE_EVENTS + " (" +
                COL_EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EVENT_USER_ID + " INTEGER NOT NULL, " +
                COL_EVENT_TITLE + " TEXT NOT NULL, " +
                COL_EVENT_DATE + " TEXT NOT NULL, " +
                COL_EVENT_TRIGGER + " INTEGER NOT NULL, " +
                COL_EVENT_NOTES + " TEXT, " +
                COL_EVENT_IMAGE_URI + "TEXT, " +
                "FOREIGN KEY(" + COL_EVENT_USER_ID + ") REFERENCES " +
                TABLE_USERS + "(" + COL_USER_ID + ")" +
                ");";

        db.execSQL(createUsers);
        db.execSQL(createEvents);
    }

    // if version changes, rebuild it (simple way)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // add image uri column when upgrading versions
        if (oldVersion < 3)
        {
            db.execSQL("ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN "
                    + COL_EVENT_IMAGE_URI + " TEXT");
        }
    }

    // create a new user (returns id or -1)
    public long registerUser(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);

        return db.insert(TABLE_USERS, null, values);
    }

    // checks username/password, returns userId or -1 if wrong
    public long authenticateUser(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();

        String[] columns = { COL_USER_ID };
        String selection = COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ?";
        String[] args = { username, password };

        try (Cursor cursor = db.query(TABLE_USERS, columns, selection, args, null,
                null, null)) {
            if (cursor.moveToFirst()) {
                return cursor.getLong(cursor.getColumnIndexOrThrow(COL_USER_ID));
            }
        }

        return -1;
    }

    // insert a new event for a user
    public long addEvent(long userId, String title, String date, long triggerTimeMillis) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_EVENT_USER_ID, userId);
        values.put(COL_EVENT_TITLE, title);
        values.put(COL_EVENT_DATE, date);
        values.put(COL_EVENT_TRIGGER, triggerTimeMillis);
        values.put(COL_EVENT_NOTES, ""); // start empty
        values.putNull(COL_EVENT_IMAGE_URI);

        return db.insert(TABLE_EVENTS, null, values);
    }

    // load all events for one user
    public List<Event> getEventsForUser(long userId) {
        List<Event> results = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] columns = {
                COL_EVENT_ID,
                COL_EVENT_USER_ID,
                COL_EVENT_TITLE,
                COL_EVENT_DATE,
                COL_EVENT_TRIGGER,
                COL_EVENT_NOTES
        };

        String selection = COL_EVENT_USER_ID + " = ?";
        String[] args = { String.valueOf(userId) };

        String orderBy = COL_EVENT_TRIGGER + " ASC";

        try (Cursor cursor = db.query(TABLE_EVENTS, columns, selection, args,
                null, null, orderBy)) {
            while (cursor.moveToNext()) {
                long eventId = cursor.getLong(cursor.getColumnIndexOrThrow(COL_EVENT_ID));
                long ownerId = cursor.getLong(cursor.getColumnIndexOrThrow(COL_EVENT_USER_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_TITLE));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_DATE));
                long trigger = cursor.getLong(cursor.getColumnIndexOrThrow(COL_EVENT_TRIGGER));
                String notes = cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_NOTES));
                if (notes == null) notes = "";

                results.add(new Event(eventId, ownerId, title, date, trigger, notes));
            }
        }

        return results;
    }

    //save the selected photo uri for event
    public boolean updateEventImageUri(long eventId, String imageUri)
    {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_EVENT_IMAGE_URI, imageUri);

        int rows = db.update(TABLE_EVENTS, values, COL_EVENT_ID + " = ?",
                new String[]{String.valueOf(eventId)});

        return rows > 0;
    }

    //get saved phoot uri for an event
    public String getEventImageUri(long eventId)
    {
        SQLiteDatabase db = getReadableDatabase();

        String[] columns = {COL_EVENT_IMAGE_URI};
        String selection = COL_EVENT_ID + " = ?";
        String[] args = {String.valueOf(eventId)};

        try (Cursor cursor = db.query(TABLE_EVENTS, columns, selection, args,
                null, null, null))
        {
            if (cursor.moveToFirst())
            {
                String imageUri = cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_IMAGE_URI));

                return imageUri == null ? "" : imageUri;
            }
        }
        return "";
    }

    // delete an event by id
    public boolean deleteEvent(long eventId) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_EVENTS, COL_EVENT_ID + " = ?",
                new String[]{ String.valueOf(eventId) });
        return rows > 0;
    }

    // updates notes for one event
    public boolean updateEventNotes(long eventId, String notes) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_EVENT_NOTES, notes);

        int rows = db.update(TABLE_EVENTS, values, COL_EVENT_ID + " = ?",
                new String[]{ String.valueOf(eventId) });
        return rows > 0;
    }

    // get notes for one event (used when opening detail screen)
    public String getEventNotes(long eventId) {
        SQLiteDatabase db = getReadableDatabase();

        String[] columns = { COL_EVENT_NOTES };
        String selection = COL_EVENT_ID + " = ?";
        String[] args = { String.valueOf(eventId) };

        try (Cursor cursor = db.query(TABLE_EVENTS, columns, selection, args, null,
                null, null)) {
            if (cursor.moveToFirst()) {
                String notes = cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_NOTES));
                return notes == null ? "" : notes;
            }
        }

        return "";
    }

}


