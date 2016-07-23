package com.theweather.app.theweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.theweather.app.theweather.db.CoolWeatherDB;
import com.theweather.app.theweather.model.City;
import com.theweather.app.theweather.model.County;
import com.theweather.app.theweather.model.Province;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2016/7/9 0009.
 */
public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     * @param coolWeatherDB
     * @param response 服务器返回数据
     * @return
     */
    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB,String response){
        if (!TextUtils.isEmpty(response)){
            String[] allProvinces = response.split(",");
            if (allProvinces !=null&&allProvinces.length >0){
                for (String p : allProvinces){
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
//                    将解析出来的数据存储到province表
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }


        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     * @param coolWeatherDB
     * @param response
     * @param provinceId
     * @return
     */
    public static boolean handleCitesResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            String[] allCities = response.split(",");
            if (allCities!=null&&allCities.length >0){
                for (String c:allCities){
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvince_id(provinceId);
//                    将解析出来的数据存储到city类
                    System.out.println("获得市解析数据");
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     * @param coolWeatherDB
     * @param response
     * @param cityId
     * @return
     */
    public static boolean handleCountyResponse(CoolWeatherDB coolWeatherDB,String response,int cityId){

        if (!TextUtils.isEmpty(response)){
            String[] allCounties=response.split(",");
            if (allCounties!=null&&allCounties.length >0){
                for (String c:allCounties){

                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCity_id(cityId);
//                    将解析出来的数据存储到county类中
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false ;

    }

    /**
     * 解析服务器返回的json数据，并将解析出的数据存储到本地
     * @param context
     * @param response
     */
    public static  void handleWeatherResponse(Context context,String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherinfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherinfo.getString("city");
            String weatherCode = weatherinfo.getString("cityid");
            String temp1 = weatherinfo.getString("temp1");
            String temp2 = weatherinfo.getString("temp2");
            String weatherDesp = weatherinfo.getString("weather");
            String publishTime = weatherinfo.getString("ptime");
            System.out.println("最低温度："+temp1);
            saveWeatherinfo(context,cityName,weatherCode,weatherDesp,temp1,temp2,publishTime);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将服务器返回的所有天气数据存储到sharepreferences文件中
     * @param context
     * @param cityName
     * @param weatherCode
     * @param weatherDesp
     * @param temp1
     * @param temp2
     * @param publishTime
     */
    private static void saveWeatherinfo(Context context, String cityName, String weatherCode, String weatherDesp, String temp1, String temp2, String publishTime) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putBoolean("city_selected",true);
        edit.putString("city_name",cityName);
        edit.putString("weather_code",weatherCode);
        edit.putString("temp1",temp1);
        edit.putString("temp2",temp2);
        edit.putString("weather_desp",weatherDesp);
        edit.putString("publish_time",publishTime);
        edit.putString("current_date",sdf.format(new Date()));
        edit.commit();
    }

}
