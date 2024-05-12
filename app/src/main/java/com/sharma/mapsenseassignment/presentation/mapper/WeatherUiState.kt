package com.sharma.mapsenseassignment.presentation.mapper

import com.sharma.mapsenseassignment.domain.Resource
import com.sharma.mapsenseassignment.domain.model.weather.WeatherData

sealed class WeatherUiState {
    data object Idle: WeatherUiState()
    data object Loading: WeatherUiState()
    data class Success(val geoCodingResponse: WeatherData): WeatherUiState()
    data class Failure(val message: String): WeatherUiState()
}

fun Resource<WeatherData>.toUiState(): WeatherUiState {
    return when (this) {
        is Resource.Loading -> WeatherUiState.Loading
        is Resource.Success -> WeatherUiState.Success(this.data)
        is Resource.Failure -> WeatherUiState.Failure(this.errorMessage)
    }
}