package com.example.bitmproject.tourmate;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;


public interface WeatherService {
    @GET()
    Call<WeatherResponse> getWeatherRespose(@Url String urlString);
}
