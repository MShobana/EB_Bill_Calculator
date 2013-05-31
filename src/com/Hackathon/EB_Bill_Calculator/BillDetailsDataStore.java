package com.Hackathon.EB_Bill_Calculator;
import static com.Hackathon.EB_Bill_Calculator.BillDetailsDB.*;

import android.*;
import android.R;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class BillDetailsDataStore {
    private static final String DATABASE_NAME = "bill_details.db";
    private static final int DB_VERSION = 4;
    private final SQLiteDatabase writableDatabase;

    public BillDetailsDataStore(Context context) {
        BillDetailsDB billDetailsDB = new BillDetailsDB(context, DATABASE_NAME, null, DB_VERSION);
        writableDatabase = billDetailsDB.getWritableDatabase();
    }

    public String getBillDetailsString() {
        Cursor cursor =writableDatabase.query(
                TABLE_NAME, new String[]{COLUMN_INPUT_JSON}, COLUMN_IS_CURRENT_STATE + " = 1", null, null, null, null);
        cursor.moveToFirst();
        int jsonColumnIndex = cursor.getColumnIndex(COLUMN_INPUT_JSON);
        String billDetails = cursor.getString(jsonColumnIndex);
        return billDetails;
    }

    public int getFromUnitsForSelectedState() {
        Cursor cursor = writableDatabase.query(
                TABLE_NAME,new String[]{COLUMN_FROM_UNITS},COLUMN_IS_CURRENT_STATE +" = 1",null,null,null,null);
        if(cursor == null && cursor.getCount()==0) {
            return 0;
        }
        cursor.moveToFirst();
        int ColumnIndex = cursor.getColumnIndex(COLUMN_FROM_UNITS);
        int fromUnits = cursor.getInt(ColumnIndex);
        return fromUnits;
    }
    public int getCount() {
        Cursor cursor = writableDatabase.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
        cursor.moveToFirst();
        int rowsCount = cursor.getInt(cursor.getColumnIndex("COUNT(*)"));
        return rowsCount;
    }

    public void updateFromUnits(Integer toUnits) {
        ContentValues contentValues=new ContentValues();
        contentValues.put(COLUMN_FROM_UNITS, toUnits);
        writableDatabase.update(TABLE_NAME,contentValues,COLUMN_IS_CURRENT_STATE + " = 1",null);
    }

    public void updateSelectedState(String selectedState) {
        ContentValues contentValues=new ContentValues();
        contentValues.put(COLUMN_IS_CURRENT_STATE, 1);
        writableDatabase.update(TABLE_NAME,contentValues,COLUMN_STATE_NAMES + " = \'"+selectedState+"\'",null);
    }

    public void unselectOldState() {
        ContentValues contentValues=new ContentValues();
        contentValues.put(COLUMN_IS_CURRENT_STATE, 0);
        writableDatabase.update(TABLE_NAME,contentValues,COLUMN_IS_CURRENT_STATE +" = 1",null);    }

    public void updateBillDetailsJson(String billDetails) {
        ContentValues contentValues=new ContentValues();
        contentValues.put(COLUMN_INPUT_JSON, billDetails);
        writableDatabase.update(TABLE_NAME,contentValues,COLUMN_IS_CURRENT_STATE + " = 1",null);
    }

    public String getSelectedState() {
        Cursor cursor = writableDatabase.query(
                TABLE_NAME,new String[]{COLUMN_STATE_NAMES},COLUMN_IS_CURRENT_STATE +" = 1",null,null,null,null);
        cursor.moveToFirst();
        int stateColumnIndex = cursor.getColumnIndex(COLUMN_STATE_NAMES);
        String stateName = cursor.getString(stateColumnIndex);
        return stateName;
    }
}
