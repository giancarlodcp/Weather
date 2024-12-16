package com.example.weather
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String = "19d2f8ff402cb933c656702db6221fd2",
        @Query("units") units: String = "metric"
    ): WeatherResponse
}