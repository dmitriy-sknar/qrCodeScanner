package com.ioLab.qrCodeScanner.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by disknar on 12.08.2016.
 */
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

    public void insertCodeToDB(MyQRCode myQRCode){
        Log.d(LOG_TAG, "Insert code in db");
        ContentValues cv = new ContentValues();
        cv.put("name", myQRCode.getName());
        cv.put("format", myQRCode.getCodeType());
        cv.put("comments", myQRCode.getComments());
        cv.put("date", myQRCode.getDateOfScanning().getTime()/1000);
        // вставляем запись и получаем ее ID
        long rowID = db.insert("codes", null, cv);
        Log.d(LOG_TAG, "Code inserted, row ID = " + rowID);
    }

    public void deleteCodeFromDB(){
        //todo
    }

    public void clearDB(){
        Log.d(LOG_TAG, "Clear \"codes\" table");
        int clearCount = db.delete("codes", null, null);
        Log.d(LOG_TAG, "Deleted rows count = " + clearCount);
    }

    public void updateCodeInDB(){
        //todo
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
//            int idColIndex = cursor.getColumnIndex("id");
            int nameColIndex = cursor.getColumnIndex("name");
            int formatColIndex = cursor.getColumnIndex("format");
            int commentsColIndex = cursor.getColumnIndex("comments");
            int dateColIndex = cursor.getColumnIndex("date");

            do {
                MyQRCode myQRCode = new MyQRCode(context);
                myQRCode.setName(cursor.getString(nameColIndex));
                myQRCode.setCodeType(cursor.getString(formatColIndex));
                myQRCode.setComments(cursor.getString(commentsColIndex));
                myQRCode.setDateOfScanning(new Date (cursor.getLong(dateColIndex)*1000));
                myQRCode.setName(cursor.getString(nameColIndex));
                myQRCodes.add(myQRCode);
            } while (cursor.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        cursor.close();
        return myQRCodes.size()!= 0 ? myQRCodes : null;
    }

    public MyQRCode getCodeFromDB(int position){
        MyQRCode myQRCode;
        if (myQRCodes.size() != 0) {
            myQRCode = myQRCodes.get(position);
        }
        else{
            getAllCodesFromDB();
            myQRCode = myQRCodes.get(position);
        }
        return myQRCode;
    }

    public void close(){
        dbHelper.close();
    }
}
