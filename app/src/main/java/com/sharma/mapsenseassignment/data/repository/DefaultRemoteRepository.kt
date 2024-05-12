package com.sharma.mapsenseassignment.data.repository

import com.sharma.mapsenseassignment.data.remote.APIs
import com.sharma.mapsenseassignment.domain.Resource
import com.sharma.mapsenseassignment.domain.model.GeoCodingResponse
import com.sharma.mapsenseassignment.domain.model.weather.WeatherData
import com.sharma.mapsenseassignment.domain.repository.RemoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class DefaultRemoteRepository(
    private val apis: APIs
): RemoteRepository {

    private val ioDispatcher = Dispatchers.IO

    override suspend fun getGeoLocation(
        cityName: String,
        limit: Int,
        apiKey: String
    ): Flow<Resource<GeoCodingResponse>> = callbackFlow<Resource<GeoCodingResponse>> {

        trySend(Resource.Loading)

        apis.getGeoData(cityName, limit, apiKey).enqueue(object : Callback<GeoCodingResponse>{
            override fun onResponse(
                call: Call<GeoCodingResponse>,
                response: Response<GeoCodingResponse>
            ) {

                if (response.isSuccessful) {
                    response.body()?.let {
                        trySend(Resource.Success(it))
                    } ?: kotlin.run {
                        trySend(Resource.Failure("No data found. Please try again!"))
                    }
                }
                close()
            }

            override fun onFailure(call: Call<GeoCodingResponse>, t: Throwable) {
                trySend(Resource.Failure(t.message ?: "Unknown Error!"))
                close()
            }
        })

        awaitClose {  }

    }.flowOn(ioDispatcher)

    override suspend fun getWeatherData(
        latitude: Double,
        longitude: Double,
        appId: String
    ): Flow<Resource<WeatherData>> = callbackFlow<Resource<WeatherData>> {

        trySend(Resource.Loading)

        apis.getWeatherData(latitude, longitude, appId).enqueue(object: Callback<WeatherData>{
            override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        trySend(Resource.Success(it))
                    } ?: kotlin.run {
                        trySend(Resource.Failure("No data found. Please try again!"))
                    }
                    close()
                }
            }

            override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                val errorMsg: String = when (t) {
                    is UnknownHostException -> "Looks like you are not connected to internet. Please check your internet and try again!"
                    is ConnectException, is SocketTimeoutException -> "Can't establish connection right now. Please try again!"
                    is IOException -> "Some network issue occurred. Please check your internet and try again!"
                    else -> t.message ?: "Unknown Error!"
                }
                trySend(Resource.Failure(errorMsg))
                close()
            }

        })
        awaitClose {  }
    }.flowOn(ioDispatcher)
}