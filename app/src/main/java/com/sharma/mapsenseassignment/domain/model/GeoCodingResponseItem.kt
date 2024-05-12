package com.sharma.mapsenseassignment.domain.model

data class GeoCodingResponseItem(
    val country: String?,
    val lat: Double,
    val lon: Double,
    val name: String,
    val state: String?
)