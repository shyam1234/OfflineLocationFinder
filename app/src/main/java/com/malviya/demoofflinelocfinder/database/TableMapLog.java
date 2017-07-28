package com.malviya.demoofflinelocfinder.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.malviya.demoofflinelocfinder.model.MapLogDataModel;

import java.util.ArrayList;

/**
 * Created by 23508 on 6/19/2017.
 */

public class TableMapLog {
    public static final String TABLE_NAME = "table_map_log";
    public static String COL_IMG_PATH = "map_path";
    public static String COL_ACRE = "acre";
    public static String COL_DATE = "date";
    public static String COL_LOCATION = "location";
    public static final String CREATE_TABLE = " CREATE TABLE " + TABLE_NAME + " (" +
            COL_IMG_PATH + " TEXT , " +
            COL_ACRE + " TEXT NOT NULL, " +
            COL_LOCATION + " TEXT NOT NULL, " +
            COL_DATE + " TEXT  " + ");";

    private Context mContext;

    public TableMapLog(Context pContext) {
        mContext = pContext;
    }


    public void addMapLog(MapLogDataModel holder) {
        SQLiteDatabase mDB = null;
        try {
            mDB = new DatabaseManger(mContext).getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_IMG_PATH, holder.getFilePath());
            values.put(COL_ACRE, holder.getAcre());
            values.put(COL_LOCATION, holder.getLocation());
            values.put(COL_DATE, holder.getLastModified());
            long row = mDB.insert(TABLE_NAME, null, values);
            Log.d("Malviya", "insert value  Snap: " + holder.getFilePath() + " Acre: " + holder.getAcre() + " Date: " + holder.getLastModified());
            Log.d("Malviya", "inserted " + row);
        } catch (Exception e) {
            Log.e("Malviya", "Exception from addMapLog " + e.getMessage());
        } finally {
            mDB.close();
        }
    }


    public ArrayList<MapLogDataModel> getMapLogArray() {
        ArrayList<MapLogDataModel> list = new ArrayList<>();
        SQLiteDatabase mDB = null;
        try {
            mDB = new DatabaseManger(mContext).getReadableDatabase();
            Cursor cursor = mDB.rawQuery("select * from " + TABLE_NAME, null);
            if (cursor.moveToFirst()) {
                do {
                    MapLogDataModel holder = new MapLogDataModel();
                    holder.setFilePath(cursor.getString(cursor.getColumnIndex(COL_IMG_PATH)));
                    holder.setAcre(cursor.getString(cursor.getColumnIndex(COL_ACRE)));
                    holder.setLocation(cursor.getString(cursor.getColumnIndex(COL_LOCATION)));
                    holder.setLastModified(cursor.getString(cursor.getColumnIndex(COL_DATE)));
                    Log.d("Malviya","list+++ "+holder.getLastModified());
                    list.add(holder);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("Malviya", "Exception from getMapLogArray " + e.getMessage());
        } finally {
            mDB.close();
            return list;
        }
    }
}
