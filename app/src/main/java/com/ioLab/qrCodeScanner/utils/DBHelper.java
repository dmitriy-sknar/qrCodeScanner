package com.ioLab.qrCodeScanner.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by disknar on 12.08.2016.
 */
public class DBHelper extends SQLiteOpenHelper {
    private final String LOG_TAG = "ioLab";

    public DBHelper(Context context) {
        super(context, "scannedCodesDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "Create database");
        db.execSQL("create table codes ("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "format text,"
                + "comments text,"
                + "date integer" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
