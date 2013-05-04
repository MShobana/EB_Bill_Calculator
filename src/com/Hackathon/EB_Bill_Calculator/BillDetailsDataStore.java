package com.Hackathon.EB_Bill_Calculator;
import static com.Hackathon.EB_Bill_Calculator.BillDetailsDB.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class BillDetailsDataStore {
    private static final String DATABASE_NAME = "bill_details.db";
    private static final int DB_VERSION = 1;
    private final SQLiteDatabase writableDatabase;

    public BillDetailsDataStore(Context context) {
        BillDetailsDB billDetailsDB = new BillDetailsDB(context, DATABASE_NAME, null, DB_VERSION);
        writableDatabase = billDetailsDB.getWritableDatabase();
    }

    public void insert(String slabRatesString) {
        ContentValues contentValues=new ContentValues();
        contentValues.put(COLUMN_INPUT_JSON,slabRatesString);
        writableDatabase.delete(TABLE_NAME,null,null);
        writableDatabase.insert(TABLE_NAME,null,contentValues);
    }

    public String getBillDetailsString() {
        Cursor cursor = writableDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        cursor.moveToFirst();
        int jsonColumnIndex = cursor.getColumnIndex(COLUMN_INPUT_JSON);
        String billDetails = cursor.getString(jsonColumnIndex);
        return billDetails;
    }
}
