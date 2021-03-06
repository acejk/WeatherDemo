package com.ace.weathertest.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ace.weathertest.model.City;
import com.ace.weathertest.model.County;
import com.ace.weathertest.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/12/13 0013.
 */
public class CoolWeatherDB {
    public static String DB_NAME = "weather";

    public static int VERSION = 2;

    public static CoolWeatherDB coolWeatherDB;

    public SQLiteDatabase db;

    private CoolWeatherDB(Context context) {
        CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    public synchronized static CoolWeatherDB getInstance(Context context) {
        if(coolWeatherDB == null) {
            coolWeatherDB = new CoolWeatherDB(context);
        }

        return coolWeatherDB;
    }

    public void saveProvince(Province province) {
        if(province != null) {
            ContentValues values = new ContentValues();
            values.put("province_name", province.getProvinceName());
            values.put("province_code", province.getProvinceCode());
            db.insert("province", null, values);
        }
    }

    public List<Province> loadProvinces() {
        List<Province> provinces = new ArrayList<Province>();
        Cursor cursor = db.query("province", null, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                provinces.add(province);
            } while(cursor.moveToNext());

        }

        return provinces;
    }

    public void saveCity(City city) {
        if(city != null) {
            ContentValues values = new ContentValues();
            values.put("city_name", city.getCityName());
            values.put("city_code", city.getCityCode());
            values.put("province_id", city.getProvinceId());
            db.insert("city", null, values);
        }
    }

    public List<City> loadCities(int provinceId) {
        List<City> cities = new ArrayList<City>();
        Cursor cursor = db.query("city", null, "province_id = ?", new String[] {String.valueOf(provinceId)}, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(provinceId);
                cities.add(city);

            } while(cursor.moveToNext());
        }


        return cities;

    }

    public void saveCounty(County county) {
        if(county != null) {
            ContentValues values = new ContentValues();
            values.put("county_name", county.getCountyName());
            values.put("county_code", county.getCountyCode());
            values.put("city_id", county.getCityId());
            db.insert("county", null, values);
        }
    }

    public List<County> loadCounties(int cityId) {
        List<County> counties = new ArrayList<County>();
        Cursor cursor = db.query("county", null, "city_id = ?", new String[] {String.valueOf(cityId)}, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityId(cityId);
                counties.add(county);
            } while(cursor.moveToNext());
        }


        return counties;

    }
}
