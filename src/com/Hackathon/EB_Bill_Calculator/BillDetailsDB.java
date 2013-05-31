package com.Hackathon.EB_Bill_Calculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BillDetailsDB extends SQLiteOpenHelper{
    public static final String TABLE_NAME = "EBBillDetails";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_INPUT_JSON = "detailsJson";
    public static final String COLUMN_FROM_UNITS = "fromUnits";
    public static final String COLUMN_STATE_NAMES = "stateNames";
    public static final String COLUMN_IS_CURRENT_STATE = "isCurrentState";
    private final Context context;

    public BillDetailsDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_STATE_NAMES + " TEXT, "
                + COLUMN_INPUT_JSON + " TEXT, "
                + COLUMN_FROM_UNITS + " INTEGER DEFAULT 0, "
                + COLUMN_IS_CURRENT_STATE + " BOOL DEFAULT 0)");
        String[] states = context.getResources().getStringArray(R.array.States);
        String[] billJsons = context.getResources().getStringArray(R.array.billJsonArray);
        ContentValues contentValues = new ContentValues();
        for(int i=0;i < states.length;i++)
        {
           contentValues.put(COLUMN_STATE_NAMES,states[i]);
           contentValues.put(COLUMN_INPUT_JSON,billJsons[i]);
           sqLiteDatabase.insert(TABLE_NAME,null,contentValues);
        }
        updateFirstRowIsCurrentStateToOne(sqLiteDatabase);
    }

    private void updateFirstRowIsCurrentStateToOne(SQLiteDatabase sqLiteDatabase) {
        ContentValues contentValues=new ContentValues();
        contentValues.put(COLUMN_IS_CURRENT_STATE,1);
        sqLiteDatabase.update(TABLE_NAME,contentValues,COLUMN_ID + " = 1",null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + "");
        onCreate(sqLiteDatabase);

    }
}
