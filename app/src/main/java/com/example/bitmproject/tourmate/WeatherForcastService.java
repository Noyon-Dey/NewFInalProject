package com.example.bitmproject.tourmate;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;


public interface WeatherForcastService {
    @GET()
    Call<WeatherForcastResponse> getWeatherForcastRespose(@Url String string);
}
