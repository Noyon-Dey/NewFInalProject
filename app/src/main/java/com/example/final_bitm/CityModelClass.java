package com.example.final_bitm;


public class CityModelClass {

    int cityId;
    String cityName;

    public CityModelClass(int cityId, String cityName) {
        this.cityId = cityId;
        this.cityName = cityName;
    }

    public CityModelClass(String cityName) {
        this.cityName = cityName;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}

