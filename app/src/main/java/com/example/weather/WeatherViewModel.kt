package com.example.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _state = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val state: StateFlow<WeatherState> = _state

    fun fetchWeather(city: String) {
        viewModelScope.launch {
            _state.value = WeatherState.Loading
            try {
                val weather = repository.getWeather(city)
                _state.value = WeatherState.Success(weather)
            } catch (e: Exception) {
                _state.value = WeatherState.Error("Error fetching weather: ${e.message}")
            }
        }
    }
}

sealed class WeatherState {
    object Loading : WeatherState()
    data class Success(val weather: WeatherResponse) : WeatherState()
    data class Error(val message: String) : WeatherState()
}