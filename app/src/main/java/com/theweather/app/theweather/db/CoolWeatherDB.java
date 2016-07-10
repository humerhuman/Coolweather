package com.theweather.app.theweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.theweather.app.theweather.model.City;
import com.theweather.app.theweather.model.County;
import com.theweather.app.theweather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/9 0009.
 */
public class CoolWeatherDB {
    /**
     * 数据库名
     */
    public static String DB_NAME="cool_weather";
    /**
     * 数据库版本号
     */
    public static final int VERASION = 1;
    /**
     * 将构造方法私有化
     */
    private SQLiteDatabase db;
    private static CoolWeatherDB coolWeatherDB;

    private CoolWeatherDB(Context context){
        CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context, DB_NAME,null, VERASION);
        db = dbHelper.getWritableDatabase();
    }
    /**
     * 获取coolweather实例
     */
    public synchronized static CoolWeatherDB getInstance(Context context) {
        if (coolWeatherDB==null) {
            coolWeatherDB = new CoolWeatherDB(context);
        }
        return coolWeatherDB;
    }
    /**
     * 将province实例存储到数据库中
     */
    public void saveProvince(Province province){
        if (province!=null){
            ContentValues values = new ContentValues();
            values.put("province_name",province.getProvinceName());
            values.put("province_code",province.getProvinceCode());
            db.insert("Province",null,values);
        }
    }

    /**
     * 从数据库读取全国所有省份的天气信息
     * @return province数据集合
     */
    public List<Province> loadProvinces(){
        List<Province> list= new ArrayList<>();
        Cursor cursor = db.query("Province", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));

                list.add(province);
            } while (cursor.moveToNext());
        }
            if (cursor != null) {
                cursor.close();
            }
            return list;


    }

    /**
     * 将city实例存储到数据库
     * @param city
     */
    public void saveCity(City city){
        if (city!=null){
            ContentValues values = new ContentValues();
            values.put("city_name",city.getCityName());
            values.put("city_code",city.getCityCode());
            values.put("province_id",city.getProvince_id());
            db.insert("City",null,values);
        }
    }

    /**
     * 从数据库读取某省下所有城市信息
     * @return
     */
    public List<City> loadcitys(int provinceId){
        List<City> list = new ArrayList<>();
        Cursor cursor = db.query("City", null, "province_id=?", new String[]{String.valueOf(provinceId)}, null, null, null);
        if (cursor.moveToFirst()){
            do {


            City city = new City();
            city.setId(cursor.getInt(cursor.getColumnIndex("id")));
            city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
            city.setProvince_id(provinceId);
            list.add(city);
        }while (cursor.moveToNext());
        }
        if (cursor!=null){
            cursor.close();
        }
        return list;
    }

    /**
     * 将county实例存储到数据库
     * @param county
     */
    public void saveCounty(County county){
        if (county!=null){
            ContentValues values = new ContentValues();
            values.put("county_name",county.getCountyName());
            values.put("county_code",county.getCountyCode());
            values.put("city_id",county.getCity_id());
            db.insert("County",null,values);
        }
    }

    /**
     * 从数据库中某城市下读取县的信息
     * @param cityId
     * @return
     */
    public List<County> loadCounties(int cityId){
        List<County> list = new ArrayList<>();
        Cursor cursor = db.query("County", null, "city_id=?", new String[]{String.valueOf(cityId)}, null, null, null);
        if (cursor.moveToFirst()){
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCity_id(cityId);

                list.add(county);
            }while (cursor.moveToNext());
        }
        if (cursor!=null){
            cursor.close();
        }
        return list;
    }

}
