package com.sharma.mapsenseassignment.domain

sealed class Resource<out T> {
    data object Loading : Resource<Nothing>()
    data class Success<out T>(val data: T) : Resource<T>()
    data class Failure<out T>(val errorMessage: String) : Resource<T>()
}
