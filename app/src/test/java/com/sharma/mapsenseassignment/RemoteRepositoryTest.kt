package com.sharma.mapsenseassignment

import com.sharma.mapsenseassignment.data.remote.APIs
import com.sharma.mapsenseassignment.data.repository.DefaultRemoteRepository
import com.sharma.mapsenseassignment.domain.Resource
import com.sharma.mapsenseassignment.domain.model.GeoCodingResponse
import com.sharma.mapsenseassignment.domain.model.weather.WeatherData
import com.sharma.mapsenseassignment.domain.repository.RemoteRepository
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class RemoteRepositoryTest {

    @Mock
    private lateinit var api: APIs
    private lateinit var repository: RemoteRepository

    @Before
    fun setUp() {
        repository = DefaultRemoteRepository(api)
    }

    @Test
    fun `getGeoLocation(), returns success`() = runBlocking {

        val cityName = ""
        val limit = 5
        val apiKey = ""
        val geoResponse = Response.success(GeoCodingResponse())


        // Mock the Call object returned by the API service
        val call: Call<GeoCodingResponse> = Mockito.mock(Call::class.java) as Call<GeoCodingResponse>
        Mockito.`when`(api.getGeoData(cityName, limit, apiKey)).thenReturn(call)
        Mockito.`when`(call.enqueue(Mockito.any())).thenAnswer {
            val callback = it.arguments[0] as Callback<GeoCodingResponse>
            callback.onResponse(call, geoResponse)
        }

        val result = repository.getGeoLocation(cityName, limit, apiKey).last()

        assertTrue(result is Resource.Success)
        assertEquals(geoResponse.body(), (result as Resource.Success).data)
    }

    @Test
    fun `getGeoLocation(), returns failure`() = runBlocking {

        val cityName = ""
        val limit = 5
        val apiKey = ""

        // Mock the Call object returned by the API service
        val call: Call<GeoCodingResponse> = Mockito.mock(Call::class.java) as Call<GeoCodingResponse>
        Mockito.`when`(api.getGeoData(cityName, limit, apiKey)).thenReturn(call)
        Mockito.`when`(call.enqueue(Mockito.any())).thenAnswer {
            val callback = it.arguments[0] as Callback<GeoCodingResponse>
            callback.onFailure(call, Throwable("Network error"))
        }

        val result = repository.getGeoLocation(cityName, limit, apiKey).last()

        assertTrue(result is Resource.Failure)
        assertNotNull((result as Resource.Failure).errorMessage)
    }

    @Test
    fun `getWeatherData(), returns success`() = runBlocking {

        val lat = 72.2
        val lon = 72.2
        val apiKey = ""
        val geoResponse = Response.success(WeatherData(
            null,1,null,"", emptyList()
        ))


        // Mock the Call object returned by the API service
        val call: Call<WeatherData> = Mockito.mock(Call::class.java) as Call<WeatherData>
        Mockito.`when`(api.getWeatherData(lat, lon, apiKey)).thenReturn(call)
        Mockito.`when`(call.enqueue(Mockito.any())).thenAnswer {
            val callback = it.arguments[0] as Callback<WeatherData>
            callback.onResponse(call, geoResponse)
        }

        val result = repository.getWeatherData(lat, lon, apiKey).last()

        assertTrue(result is Resource.Success)
        assertEquals(geoResponse.body(), (result as Resource.Success).data)
    }

    @Test
    fun `getWeatherData(), returns failure`() = runBlocking {

        val lat = 72.2
        val lon = 72.2
        val apiKey = ""


        // Mock the Call object returned by the API service
        val call: Call<WeatherData> = Mockito.mock(Call::class.java) as Call<WeatherData>
        Mockito.`when`(api.getWeatherData(lat, lon, apiKey)).thenReturn(call)
        Mockito.`when`(call.enqueue(Mockito.any())).thenAnswer {
            val callback = it.arguments[0] as Callback<WeatherData>
            callback.onFailure(call, Throwable("Network error"))
        }

        val result = repository.getWeatherData(lat, lon, apiKey).last()

        assertTrue(result is Resource.Failure)
        assertNotNull((result as Resource.Failure).errorMessage)
    }

}