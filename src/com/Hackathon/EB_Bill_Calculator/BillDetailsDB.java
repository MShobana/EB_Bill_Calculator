package com.Hackathon.EB_Bill_Calculator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BillDetailsDB extends SQLiteOpenHelper{
    public static final String TABLE_NAME = "EBBillDetails";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_INPUT_JSON = "detailsJson";

    public BillDetailsDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_INPUT_JSON + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + "");
        onCreate(sqLiteDatabase);

    }
}
