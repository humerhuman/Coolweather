package com.theweather.app.theweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/7/9 0009.
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper {
    /**
     * proincev表建表语句
     */
    public static final String CREATE_POROVINCE="create table Province("+"id integer primary key autoincrement," +
            ""+"province_name text,"+"province_code text)";
    /**
     * city表建表语句
     */
    public static final String CREATE_CITY="create table City("+"id integer primary key autoincrement,"+"city_name text,"+"city_code,"+"province_id integer)";

    /**
     *country表建表语句
     */
    public static final String CREATE_COUNTY="create table County("+"id integer primary key autoincrement,"+"county_name text,"+"county_code text,"+"city_id integer)";

    public CoolWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_POROVINCE);//创建province表
        sqLiteDatabase.execSQL(CREATE_CITY);//创建city表
        sqLiteDatabase.execSQL(CREATE_COUNTY);//创建county表


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
