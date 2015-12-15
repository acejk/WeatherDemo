package com.ace.weathertest.model;

/**
 * Created by Administrator on 2015/12/13 0013.
 */
public class County {
    private int id;
    private String countyName;
    private String countCode;
    private int cityId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getCountCode() {
        return countCode;
    }

    public void setCountCode(String countCode) {
        this.countCode = countCode;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
