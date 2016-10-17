package com.ioLab.qrCodeScanner.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class History {
    private final String LOG_TAG = "ioLabLog";
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private Context context;
    private List<MyQRCode> myQRCodes;

    public History(Context context){
        // create object to manage DB
        dbHelper = new DBHelper(context);
        this.context = context;
        db = dbHelper.getWritableDatabase();
    }

    public long insertCodeToDB(MyQRCode myQRCode){
        Log.d(LOG_TAG, "Insert code in db");
        ContentValues cv = new ContentValues();
        cv.put("name", myQRCode.getName());
        cv.put("format", myQRCode.getCodeType());
        cv.put("comments", myQRCode.getComments());
        cv.put("date", myQRCode.getDateOfScanning().getTime()/1000);
        // insert data in DB and get it's ID
        long rowID = db.insert("codes", null, cv);
        Log.d(LOG_TAG, "Code inserted, row ID = " + rowID);
        return rowID;
    }

    public void deleteCodeFromDB(String id){
        int delCount = db.delete("codes", "id = " + id, null);
        Log.d(LOG_TAG, "deleted rows count = " + delCount);
    }

    public void clearDB(){
        Log.d(LOG_TAG, "Clear \"codes\" table");
        int clearCount = db.delete("codes", null, null);
        Log.d(LOG_TAG, "Deleted rows count = " + clearCount);
    }

    public void updateCodeInDB(MyQRCode myQRCode){
        ContentValues cv = new ContentValues();
        cv.put("name", myQRCode.getName());
        cv.put("format", myQRCode.getCodeType());
        cv.put("comments", myQRCode.getComments());
        cv.put("date", myQRCode.getDateOfScanning().getTime()/1000);
        cv.put("path", myQRCode.getPath());
     //    refresh by id
        int updCount = db.update("codes", cv, "id = ?", new String[] {myQRCode.getId()});
        Log.d(LOG_TAG, "updated rows count (must be 1) = " + updCount);
    }

    public List<MyQRCode> getAllCodesFromDB(){
        myQRCodes = new ArrayList<>();
        // get all data from table codes - got Cursor
        cursor = db.query("codes", null, null, null, null, null, null);
        Log.d(LOG_TAG, "Rows in \"codes\" table: " + cursor.getCount());

        // place cursor to first row
        // if cursor is empty returns false
        if (cursor.moveToFirst()) {

            // get the columns indexes
            int idColIndex = cursor.getColumnIndex("id");
            int nameColIndex = cursor.getColumnIndex("name");
            int formatColIndex = cursor.getColumnIndex("format");
            int commentsColIndex = cursor.getColumnIndex("comments");
            int dateColIndex = cursor.getColumnIndex("date");
            int pathColIndex = cursor.getColumnIndex("path");

            do {
                MyQRCode myQRCode = new MyQRCode(context);
                myQRCode.setId(cursor.getString(idColIndex));
                myQRCode.setName(cursor.getString(nameColIndex));
                myQRCode.setCodeType(cursor.getString(formatColIndex));
                myQRCode.setComments(cursor.getString(commentsColIndex));
                myQRCode.setDateOfScanning(new Date (cursor.getLong(dateColIndex)*1000));
                myQRCode.setName(cursor.getString(nameColIndex));
                myQRCode.setPath(cursor.getString(pathColIndex));
                myQRCodes.add(myQRCode);
            } while (cursor.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        cursor.close();
        return myQRCodes.size()!= 0 ? myQRCodes : null;
    }

    public MyQRCode getCodeFromDBbyPosition(int position){
        MyQRCode myQRCode;
        if (myQRCodes.size() != 0) {
            myQRCode = myQRCodes.get(position);
        }
        else{
            myQRCodes = getAllCodesFromDB();
            myQRCode = myQRCodes.get(position);
        }
        return myQRCode;
    }

    public MyQRCode getCodeById(long id){
        myQRCodes = getAllCodesFromDB();
        for(MyQRCode code : myQRCodes){
            if(Long.parseLong(code.getId()) == id){
                return code;
            }
        }
        return null;
    }

    public void close(){
        dbHelper.close();
    }
}
