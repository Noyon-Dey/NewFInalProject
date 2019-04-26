package com.example.bitmproject.tourmate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;


public class CitySource {

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;

    public CitySource(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public void open(){
        db = databaseHelper.getWritableDatabase();
    }
    public void close(){
        db.close();
    }
    public boolean incertCityData(CityModelClass cityModelClass){

        this.open();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.CITY_COL_NAME,cityModelClass.getCityName());
        long incertedRow =  db.insert(DatabaseHelper.TABLE_CITY,null,contentValues);
        if (incertedRow>0)
            return true;
        else return false;
    }

    public ArrayList<String> getCities() {
        db = databaseHelper.getReadableDatabase();
        ArrayList<String> cities = new ArrayList<>();

        Cursor fullTableData = db.query(DatabaseHelper.TABLE_CITY, new String[]{DatabaseHelper.CITY_COL_NAME},null,null,null,null,null);

        if (fullTableData !=null&&fullTableData.getCount()>0){
            fullTableData.moveToFirst();
            do {
                String cityName = fullTableData.getString(fullTableData.getColumnIndex(DatabaseHelper.CITY_COL_NAME));
                cities.add(cityName);
            }
            while (fullTableData.moveToNext());
        }
        fullTableData.close();
        this.close();

        return cities;
    }
}
