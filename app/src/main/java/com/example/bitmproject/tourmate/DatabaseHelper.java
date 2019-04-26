package com.example.bitmproject.tourmate;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "library_db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_CITY = "tbl_city";
    public static final String CITY_COL_ID = "city_id";
    public static final String CITY_COL_NAME = "city_name";

    public static final String CREATE_TABLE_BOOK = "CREATE TABLE "+TABLE_CITY+"("+
            CITY_COL_ID+" INTEGER PRIMARY KEY, "+
            CITY_COL_NAME+" TEXT);";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BOOK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+TABLE_CITY);
        onCreate(db);

    }
}
