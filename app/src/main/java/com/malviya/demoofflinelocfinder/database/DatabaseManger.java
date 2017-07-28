package com.malviya.demoofflinelocfinder.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 23508 on 6/19/2017.
 */

public class DatabaseManger extends SQLiteOpenHelper {
    private final static String DB_NAME = "com.malviya.map";
    private final static int VERSION = 1;

    public DatabaseManger(Context context) {
        super(context, DB_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TableMapLog.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TableMapLog.TABLE_NAME);
        onCreate(db);
    }
}
