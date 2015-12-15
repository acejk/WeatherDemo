package com.ace.weathertest.util;

import android.text.TextUtils;

import com.ace.weathertest.db.CoolWeatherDB;
import com.ace.weathertest.model.City;
import com.ace.weathertest.model.County;
import com.ace.weathertest.model.Province;


/**
 * Created by Administrator on 2015/12/15 0015.
 */
public class Utility {
    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB, String response) {
        if(!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if(allProvinces != null && allProvinces.length > 0) {
                for(String p : allProvinces) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    coolWeatherDB.saveProvince(province);
                }

                return true;
            }
        }

        return false;
    }

    public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB, String response, int provinceId) {
        if(!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if(allCities != null && allCities.length > 0) {
                for(String c : allCities) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    coolWeatherDB.saveCity(city);
                }

                return true;
            }
        }

        return false;
    }

    public synchronized static boolean handleCountriesResponse(CoolWeatherDB coolWeatherDB, String response, int cityId) {
        if(!TextUtils.isEmpty(response)) {
            String[] allCountries = response.split(",");
            if(allCountries != null && allCountries.length > 0) {
                for(String c : allCountries) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    coolWeatherDB.saveCounty(county);
                }

                return true;
            }
        }

        return false;
    }
}
