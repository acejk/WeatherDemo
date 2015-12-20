package com.ace.weathertest.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ace.weathertest.R;
import com.ace.weathertest.util.HttpCallBackListener;
import com.ace.weathertest.util.HttpUtil;
import com.ace.weathertest.util.Utility;

/**
 * Created by Administrator on 2015/12/20 0020.
 */
public class WeatherActivity extends Activity implements View.OnClickListener {
    private LinearLayout ll_weatherInfo;
    private TextView tv_cityName;
    private TextView tv_temp1;
    private TextView tv_temp2;
    private TextView tv_publishTime;
    private TextView tv_currentTime;
    private TextView tv_weatherDesp;
    private Button btn_switchCity;
    private Button btn_refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);

        initViews();



        String countyCode = getIntent().getStringExtra("county_code");
        if(!TextUtils.isEmpty(countyCode)) {
            tv_publishTime.setText("同步中.....");
            ll_weatherInfo.setVisibility(View.INVISIBLE);
            tv_cityName.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        } else {
            showWeather();
        }

        initEvents();

    }



    public void queryWeatherCode(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city" +
                countyCode + ".xml";
        queryFromServer(address, "countyCode");
    }

    private void queryFromServer(final String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onfinish(String response) {
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
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
                        tv_publishTime.setText("同步失败");
                    }
                });
            }
        });
    }

    private void showWeather() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        tv_cityName.setText(sharedPreferences.getString("city_name", ""));
        tv_publishTime.setText("今天" + sharedPreferences.getString("publish_time", "") + "发布");
        tv_weatherDesp.setText(sharedPreferences.getString("weather_desp", ""));
        tv_temp1.setText(sharedPreferences.getString("temp1", ""));
        tv_temp2.setText(sharedPreferences.getString("temp2", ""));
        tv_currentTime.setText(sharedPreferences.getString("current_time", ""));
        ll_weatherInfo.setVisibility(View.VISIBLE);
        tv_cityName.setVisibility(View.VISIBLE);
    }

    private void queryWeatherInfo(String weatherCode) {
        String address = "http://www.weather.com.cn/data/cityinfo/" +
                weatherCode + ".html";
        queryFromServer(address, "weatherCode");
    }

    private void initViews() {
        ll_weatherInfo = (LinearLayout) findViewById(R.id.ll_weatherInfo);
        btn_switchCity = (Button) findViewById(R.id.btn_switchCity);
        btn_refresh = (Button) findViewById(R.id.btn_refresh);
        tv_cityName = (TextView) findViewById(R.id.tv_cityName);
        tv_currentTime = (TextView) findViewById(R.id.tv_currentTime);
        tv_publishTime = (TextView) findViewById(R.id.tv_publish);
        tv_temp1 = (TextView) findViewById(R.id.tv_temp1);
        tv_temp2 = (TextView) findViewById(R.id.tv_temp2);
        tv_weatherDesp = (TextView) findViewById(R.id.tv_weatherdesp);
    }

    private void initEvents() {
        btn_switchCity.setOnClickListener(this);
        btn_refresh.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_switchCity:
                Intent intent = new Intent(this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_refresh:
                tv_publishTime.setText("同步中.....");
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode = sharedPreferences.getString("weather_code", "");
                if(!TextUtils.isEmpty(weatherCode)) {
                    queryWeatherInfo(weatherCode);
                }
                break;
        }
    }


}
