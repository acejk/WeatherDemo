package com.ace.weathertest.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ace.weathertest.R;
import com.ace.weathertest.db.CoolWeatherDB;
import com.ace.weathertest.model.City;
import com.ace.weathertest.model.County;
import com.ace.weathertest.model.Province;
import com.ace.weathertest.util.HttpCallBackListener;
import com.ace.weathertest.util.HttpUtil;
import com.ace.weathertest.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/12/15 0015.
 */
public class ChooseAreaActivity extends Activity {
    public static int LEVEL_PROVINCE = 0;
    public static int LEVEL_CITY = 1;
    public static int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView tv_title;
    private ListView lv_show;

    private ArrayAdapter<String> adapter;

    private CoolWeatherDB coolWeatherDB;

    private List<String> datas = new ArrayList<String>();

    private List<Province> provinces;
    private List<City> cities;
    private List<County> counties;

    private Province selectedProvince;
    private City selectedCity;

    private int currentLevel;

    private boolean isFromWeatherActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ChooseAreaActivity.this);
        if(sharedPreferences.getBoolean("city_selected", false) && !isFromWeatherActivity) {
            Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);





        initViews();

        initDatas();

        initEvents();
    }

    private void initEvents() {
        lv_show.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinces.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cities.get(position);
                    queryCounties();
                } else if(currentLevel == LEVEL_COUNTY) {
                    String countyCode = counties.get(position).getCountyCode();
                    Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                    intent.putExtra("county_code", countyCode);
                    startActivity(intent);
                    finish();
                }
            }
        });

        queryProvinces();
    }



    private void queryProvinces() {
        provinces = coolWeatherDB.loadProvinces();
        if (provinces.size() > 0) {
            datas.clear();

            for (Province province : provinces) {
                datas.add(province.getProvinceName());
            }

            adapter.notifyDataSetChanged();
            lv_show.setSelection(0);
            tv_title.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer(null, "province");
        }
    }

    private void queryCities() {
        cities = coolWeatherDB.loadCities(selectedProvince.getId());
        if (cities.size() > 0) {
            datas.clear();

            for (City city : cities) {
                datas.add(city.getCityName());
            }

            adapter.notifyDataSetChanged();
            lv_show.setSelection(0);
            tv_title.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    private void queryCounties() {
        counties = coolWeatherDB.loadCounties(selectedCity.getId());
        if(counties.size() > 0) {
            datas.clear();

            for(County county : counties) {
                datas.add(county.getCountyName());
            }

            adapter.notifyDataSetChanged();
            lv_show.setSelection(0);
            tv_title.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer(selectedCity.getCityCode(), "county");
        }


    }

    private void queryFromServer(final String code, final String type) {
        String address;
        if(!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }

        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onfinish(String response) {
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvincesResponse(coolWeatherDB, response);
                } else if ("city".equals(type)) {
                    result = Utility.handleCitiesResponse(coolWeatherDB, response, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountriesResponse(coolWeatherDB, response, selectedCity.getId());
                }

                if (result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
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

    private void showProgressDialog() {
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载.....");
            progressDialog.setCanceledOnTouchOutside(false);

        }

        progressDialog.show();
    }

    private void closeProgressDialog() {
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
    }





    private void initDatas() {
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datas);
        lv_show.setAdapter(adapter);

        coolWeatherDB = CoolWeatherDB.getInstance(this);

    }

    private void initViews() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        lv_show = (ListView) findViewById(R.id.lv_show);
    }

    @Override
    public void onBackPressed() {
        if(currentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if(currentLevel == LEVEL_CITY) {
            queryProvinces();
        } else {
            if(isFromWeatherActivity) {
                Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }
}
