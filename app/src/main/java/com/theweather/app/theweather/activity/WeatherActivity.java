package com.theweather.app.theweather.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.theweather.app.theweather.R;
import com.theweather.app.theweather.util.HttpCallbackListener;
import com.theweather.app.theweather.util.HttpUtil;
import com.theweather.app.theweather.util.Utility;

/**
 * Created by Administrator on 2016/7/11 0011.
 */
public class WeatherActivity extends Activity {
    private LinearLayout weatherInfoLayout;
    /**
     * 用于显示城市名
     */
    private TextView cityText;
    /**
     * 用于显示发布时间
     */
    private TextView publishText;
    /**
     * 用于显示天气描述信息
     */
    private TextView weatherdespText;
    /**
     * 用于显示最低温度
     */
    private TextView temp1Text;
    /**
     * 用于显示最高温度
     */
    private TextView temp2Text;
    /**
     * 用于显示当前日期
     */
    private TextView currentDateText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
//        初始化控件
        weatherInfoLayout= (LinearLayout) findViewById(R.id.weather_info_layout);
        cityText= (TextView) findViewById(R.id.city_text);
        publishText= (TextView) findViewById(R.id.publish_text);
        weatherdespText= (TextView) findViewById(R.id.weatherdesp_text);
        temp1Text= (TextView) findViewById(R.id.temp1_text);
        temp2Text= (TextView) findViewById(R.id.temp2_text);
        currentDateText= (TextView) findViewById(R.id.currentdate_text);
        System.out.println("启动了");
        String countyCode = getIntent().getStringExtra("county_code");
        System.out.println(countyCode);
        if (!TextUtils.isEmpty(countyCode)){
            /**county_cody
             * 有县级代号就去查询天气
             */
            publishText.setText("同步中....");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);

        }else{
            /**
             * 没有县级代号就直接显示本地天气
             */
            showWeather();
        }
    }

    /**
     * 从sharedPreferences文件中读取数据并显示在界面上
     */
    private void showWeather() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        cityText.setText(preferences.getString("city_name","hHA"));
        System.out.println("当前城市名:"+preferences.getString("city_name"," "));
        temp1Text.setText(preferences.getString("temp1",""));
        temp2Text.setText(preferences.getString("temp2",""));
        weatherdespText.setText(preferences.getString("weather_desp",""));
        publishText.setText(preferences.getString("publis_time","发布"));
        currentDateText.setText(preferences.getString("current_date",""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityText.setVisibility(View.VISIBLE);

    }

    /**
     * 查询县级代号所对应的天气代号
     * @param countyCode
     */
    private void queryWeatherCode(String countyCode) {

        String address="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
        querYFromService(address,"countyCode");
    }

    /**
     * 查询天气代号所对应的天气
     * @param weatherCode
     */
    private void queryWeatherInfo(String weatherCode){
        String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
        querYFromService(address,"weatherCode");
    }

    /**
     * 根据传入的地址和类型去服务器查询天气代号或者天气
     * @param address
     * @param type
     */
    private void querYFromService(final String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if ("countyCode".equals(type)){
                    if (!TextUtils.isEmpty(response)){
//                        从服务器返回的数据解析出天气代号
                        String[] array = response.split("\\|");
                        if (array!=null&&array.length == 2){
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }

                }else if ("weatherCode".equals(type)){
                    //处理服务器返回的天气信息
                    Utility.handleWeatherResponse(WeatherActivity.this,response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");

                    }
                });

            }
        });

    }
}
