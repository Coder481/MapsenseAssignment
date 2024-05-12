package com.sharma.mapsenseassignment.domain.model.weather

data class WeatherData(
    val coord: Coord?,
    val id: Int?,
    val main: Main?,
    val name: String?,
    val weather: List<Weather>?,
)