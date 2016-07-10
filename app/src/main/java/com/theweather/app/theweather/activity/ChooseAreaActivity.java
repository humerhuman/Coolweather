package com.theweather.app.theweather.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.theweather.app.theweather.R;
import com.theweather.app.theweather.db.CoolWeatherDB;
import com.theweather.app.theweather.model.City;
import com.theweather.app.theweather.model.County;
import com.theweather.app.theweather.model.Province;
import com.theweather.app.theweather.util.HttpCallbackListener;
import com.theweather.app.theweather.util.HttpUtil;
import com.theweather.app.theweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/9 0009.
 */
public class ChooseAreaActivity extends AppCompatActivity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private CoolWeatherDB coolWeatherDB;
    private List<String> dataList = new ArrayList<String>();
    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countyList;
    /**
     * 被选中的省
     */
    private Province selectedProvince;
    /**
     * 被选中的市
     */
    private City selectedCity;
    /**
     * 当前选中的级别
     */
    private int currentLevel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);

        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.titile_text);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);

        Log.d("test","点击了1");

        listView.setAdapter(adapter);
        coolWeatherDB = CoolWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Log.d("test","点击了");
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(i);

                    queryCites();
//                    System.out.println("查询成功");
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(i);
                    queryCounties();
                }
            }
        });
        queryProvinces();//加载省级数据

    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查到再去服务器上查询。
     */
    private void queryProvinces() {
        provinceList = coolWeatherDB.loadProvinces();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province p : provinceList) {
                dataList.add(p.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromService(null, "province");
        }
    }


    /**
     * 查询选中省内所有的市，优先从数据库查，如果没有查到再到服务器去查。
     */
    private void queryCites() {
        System.out.println("点击了："+selectedProvince.getId());
        cityList=coolWeatherDB.loadcitys(selectedProvince.getId());
        if (cityList.size()>0){
            dataList.clear();
            for (City c:cityList){
                dataList.add(c.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }else {
            queryFromService(selectedProvince.getProvinceCode(),"City");
            System.out.println("点击了："+selectedProvince.getProvinceCode());
        }
    }

    /**
     * 查询选中市内所有的县，优先从数据库查，如果没有查到再到服务器上查。
     */
    private void queryCounties() {
        countyList=coolWeatherDB.loadCounties(selectedCity.getId());
        if (countyList.size()>0){
            dataList.clear();
            for (County c:countyList){
                dataList.add(c.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        }else {
            queryFromService(selectedCity.getCityCode(),"County");
        }
    }

    /**
     * 根据传入的代号和类型从服务器上查询省市县的数据
     * @param code 传入代号
     * @param type 传入类型
     */
    private void queryFromService(final String code, final String type) {
        String address;
        if (!TextUtils.isEmpty(code)){
            address ="http://www.weather.com.cn/data/list3/city"+code+".xml";

        }else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
//        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result =false;
                if ("province".equals(type)){
                    result = Utility.handleProvincesResponse(coolWeatherDB,response);
                }else if ("City".equals(type)){
                    result = Utility.handleCitesResponse(coolWeatherDB,response,selectedProvince.getId());

                }else if ("County".equals(type)){
                    result =Utility.handleCountyResponse(coolWeatherDB,response,selectedCity.getId());
                }
                if (result){
//                    回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCites();
                            }else if ("County".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

    }

    /**
     * 关闭进度条
     */
    private void closeProgressDialog() {
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    /**
     * 显示进度条
     */
    private void showProgressDialog() {
        if (progressDialog ==null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (currentLevel ==LEVEL_COUNTY){
            queryCites();
        }else if (currentLevel == LEVEL_CITY){
            queryProvinces();
        }else {
            finish();
        }
    }
}
