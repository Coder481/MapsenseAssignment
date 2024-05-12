package com.sharma.mapsenseassignment.presentation.mapper

import com.sharma.mapsenseassignment.domain.Resource
import com.sharma.mapsenseassignment.domain.model.GeoCodingResponse

sealed class GeoUiState {
    data object Idle: GeoUiState()
    data object Loading: GeoUiState()
    data class Success(val geoCodingResponse: GeoCodingResponse): GeoUiState()
    data class Failure(val message: String): GeoUiState()
}

fun Resource<GeoCodingResponse>.toUiState(): GeoUiState {
    return when (this) {
        is Resource.Loading -> GeoUiState.Loading
        is Resource.Success -> GeoUiState.Success(this.data)
        is Resource.Failure -> GeoUiState.Failure(this.errorMessage)
    }
}