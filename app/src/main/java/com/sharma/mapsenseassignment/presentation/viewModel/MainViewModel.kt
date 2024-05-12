package com.sharma.mapsenseassignment.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sharma.mapsenseassignment.domain.repository.RemoteRepository
import com.sharma.mapsenseassignment.presentation.mapper.GeoUiState
import com.sharma.mapsenseassignment.presentation.mapper.WeatherUiState
import com.sharma.mapsenseassignment.presentation.mapper.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository
): ViewModel() {

    private val _geoUiState = MutableStateFlow<GeoUiState>(GeoUiState.Idle)
    val geoUiState: StateFlow<GeoUiState> = _geoUiState.asStateFlow()

    fun getGeoData(cityName: String, limit: Int, apiKey: String) = viewModelScope.launch {
        remoteRepository.getGeoLocation(cityName, limit, apiKey).collect { res ->
            _geoUiState.update {
                res.toUiState()
            }
        }
    }

    private val _weatherUiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val weatherUiState: StateFlow<WeatherUiState> = _weatherUiState.asStateFlow()

    fun getWeatherData(latitude: Double, longitude: Double, appId: String) = viewModelScope.launch {
        remoteRepository.getWeatherData(latitude, longitude, appId).collect { res ->
            _weatherUiState.update { res.toUiState() }
        }
    }

    fun resetGeoUiState() = _geoUiState.update { GeoUiState.Idle }
    fun resetWeatherUiState() = _weatherUiState.update { WeatherUiState.Idle }
}