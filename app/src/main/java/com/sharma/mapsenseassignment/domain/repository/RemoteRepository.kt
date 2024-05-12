package com.sharma.mapsenseassignment.domain.repository

import com.sharma.mapsenseassignment.domain.Resource
import com.sharma.mapsenseassignment.domain.model.GeoCodingResponse
import com.sharma.mapsenseassignment.domain.model.weather.WeatherData
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Query

interface RemoteRepository {
    suspend fun getGeoLocation(cityName: String, limit: Int, apiKey: String): Flow<Resource<GeoCodingResponse>>
    suspend fun getWeatherData(latitude: Double, longitude: Double, appId: String): Flow<Resource<WeatherData>>
}