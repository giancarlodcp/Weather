package com.example.weather

class WeatherRepository(private val service: WeatherService) {
    suspend fun getWeather(city: String): WeatherResponse {
        return service.getWeather(city)
    }
}