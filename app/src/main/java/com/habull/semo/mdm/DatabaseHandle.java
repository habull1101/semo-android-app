package com.habull.semo.mdm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DatabaseHandle extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "test.sqli";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "location";

    private static final String KEY_TIME = "time";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";

    public DatabaseHandle(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_table = String.format(
                "CREATE TABLE %s (%s TEXT, %s REAL, %s REAL)",
                TABLE_NAME, KEY_TIME, KEY_LATITUDE, KEY_LONGITUDE);

        db.execSQL(create_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String drop_table = String.format("DROP TABLE IF EXISTS %s", TABLE_NAME);
        db.execSQL(drop_table);

        onCreate(db);
    }

    public void addRecord(double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();

        DateFormat df = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());

        ContentValues values = new ContentValues();
        values.put(KEY_TIME, date);
        values.put(KEY_LATITUDE, latitude);
        values.put(KEY_LONGITUDE, longitude);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<String> getAllLocation() {
        ArrayList<String>  list = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        while(cursor.isAfterLast() == false) {
            String tmp = cursor.getString(0) + ", "
                        + String.valueOf(cursor.getDouble(1)) + ",  "
                        + String.valueOf(cursor.getDouble(2));
            list.add(tmp);
            cursor.moveToNext();
        }

        return list;
    }
}
