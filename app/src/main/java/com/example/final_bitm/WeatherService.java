package com.example.final_bitm;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by saran on 12/31/2017.
 */

public interface WeatherService {
    @GET()
    Call<WeatherResponse> getWeatherRespose(@Url String urlString);
}
