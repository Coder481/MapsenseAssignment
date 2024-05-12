package com.sharma.mapsenseassignment

import com.sharma.mapsenseassignment.domain.Resource
import com.sharma.mapsenseassignment.domain.model.GeoCodingResponse
import com.sharma.mapsenseassignment.domain.model.weather.WeatherData
import com.sharma.mapsenseassignment.domain.repository.RemoteRepository
import com.sharma.mapsenseassignment.presentation.mapper.GeoUiState
import com.sharma.mapsenseassignment.presentation.mapper.WeatherUiState
import com.sharma.mapsenseassignment.presentation.viewModel.MainViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @Mock
    private lateinit var repository: RemoteRepository
    private lateinit var viewModel: MainViewModel

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        viewModel = MainViewModel(repository)
    }

    @Test
    fun `test geo state success`() = runTest {

        val cityName = ""
        val limit = 5
        val apiKey = ""

        val expected = Resource.Success(GeoCodingResponse())
        val res = flow {
            emit(expected)
        }

        Mockito.`when`(repository.getGeoLocation(cityName, limit, apiKey)).thenReturn(res)

        viewModel.getGeoData(cityName, limit, apiKey)
        val result = viewModel.geoUiState.value

        assertTrue(result is GeoUiState.Success)
        assertNotNull((result as GeoUiState.Success).geoCodingResponse)
    }

    @Test
    fun `test geo state failure`() = runTest {

        val cityName = ""
        val limit = 5
        val apiKey = ""

        val expected = Resource.Failure<GeoCodingResponse>("Network error")
        val res = flow {
            emit(expected)
        }

        Mockito.`when`(repository.getGeoLocation(cityName, limit, apiKey)).thenReturn(res)

        viewModel.getGeoData(cityName, limit, apiKey)
        val result = viewModel.geoUiState.value

        assertTrue(result is GeoUiState.Failure)
        assertNotNull((result as GeoUiState.Failure).message)
    }

    @Test
    fun `test weather state success`() = runTest {

        val apiKey = ""
        val lat = 72.2
        val lon = 72.2
        val expected = Resource.Success(WeatherData(null, 1, null, "", emptyList()))
        val res = flow {
            emit(expected)
        }

        Mockito.`when`(repository.getWeatherData(lat, lon, apiKey)).thenReturn(res)

        viewModel.getWeatherData(lat, lon, apiKey)
        val result = viewModel.weatherUiState.value

        assertTrue(result is WeatherUiState.Success)
        assertNotNull((result as WeatherUiState.Success).geoCodingResponse)
    }

    @Test
    fun `test weather state failure`() = runTest {

        val apiKey = ""
        val lat = 72.2
        val lon = 72.2
        val expected = Resource.Failure<WeatherData>("Network error")
        val res = flow {
            emit(expected)
        }

        Mockito.`when`(repository.getWeatherData(lat, lon, apiKey)).thenReturn(res)

        viewModel.getWeatherData(lat, lon, apiKey)
        val result = viewModel.weatherUiState.value

        assertTrue(result is WeatherUiState.Failure)
        assertNotNull((result as WeatherUiState.Failure).message)
    }
}