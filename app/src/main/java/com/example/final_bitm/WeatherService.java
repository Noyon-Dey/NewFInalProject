package com.example.final_bitm;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;


public interface WeatherService {
    @GET()
    Call<com.example.bitmproject.tourmate.WeatherResponse> getWeatherRespose(@Url String urlString);
}
