package com.example.final_bitm;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;


public interface WeatherForcastService {
    @GET()
    Call<WeatherForcastResponse> getWeatherForcastRespose(@Url String string);
}
