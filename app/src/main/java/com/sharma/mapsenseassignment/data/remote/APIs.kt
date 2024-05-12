package com.sharma.mapsenseassignment.data.remote

import com.sharma.mapsenseassignment.domain.model.GeoCodingResponse
import com.sharma.mapsenseassignment.domain.model.weather.WeatherData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface APIs {
    @GET("data/2.5/weather")
     fun getWeatherData(
        @Query(value = "lat") latitude: Double,
        @Query(value = "lon") longitude: Double,
        @Query(value = "appid") apiKey: String
    ): Call<WeatherData>

    @GET("geo/1.0/direct")
     fun getGeoData(
        @Query(value = "q") cityName: String,
        @Query(value = "limit") limit: Int,
        @Query(value = "appid") apiKey: String
    ): Call<GeoCodingResponse>
}