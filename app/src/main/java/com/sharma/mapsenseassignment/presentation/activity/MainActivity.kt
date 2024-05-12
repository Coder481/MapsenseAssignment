package com.sharma.mapsenseassignment.presentation.activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Window
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.image.image
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.style
import com.sharma.mapsenseassignment.R
import com.sharma.mapsenseassignment.databinding.ActivityMainBinding
import com.sharma.mapsenseassignment.databinding.CitiesDetailsLayoutBinding
import com.sharma.mapsenseassignment.databinding.CityDetailItemBinding
import com.sharma.mapsenseassignment.databinding.WeatherDetailsLayoutBinding
import com.sharma.mapsenseassignment.domain.model.GeoCodingResponse
import com.sharma.mapsenseassignment.domain.model.weather.WeatherData
import com.sharma.mapsenseassignment.presentation.helper.askForPermissionDialog
import com.sharma.mapsenseassignment.presentation.helper.collectLifeCycleAware
import com.sharma.mapsenseassignment.presentation.helper.gone
import com.sharma.mapsenseassignment.presentation.helper.kelvinToCelsius
import com.sharma.mapsenseassignment.presentation.helper.kelvinToFahrenheit
import com.sharma.mapsenseassignment.presentation.helper.loadImage
import com.sharma.mapsenseassignment.presentation.helper.roundToDecimal
import com.sharma.mapsenseassignment.presentation.helper.showMessageDialog
import com.sharma.mapsenseassignment.presentation.helper.showToast
import com.sharma.mapsenseassignment.presentation.helper.visible
import com.sharma.mapsenseassignment.presentation.mapper.GeoUiState
import com.sharma.mapsenseassignment.presentation.mapper.WeatherUiState
import com.sharma.mapsenseassignment.presentation.viewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var progressDialog: Dialog? = null
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getLocationPermission()
        binding.currentLocation.setOnClickListener {
            getLocationPermission()
        }
        binding.searchEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                p0?.let {
                    binding.submitButton.isEnabled = it.isNotEmpty()
                }
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
        binding.submitButton.setOnClickListener {
            val cityName = binding.searchEditText.text?.toString()
            val limit = 5
            cityName?.let {
                viewModel.getGeoData(it, limit, OPEN_WEATHER_MAP_APP_KEY)
            }
        }

        addListeners()
    }

    private fun addListeners() {
        viewModel.geoUiState.collectLifeCycleAware(this) {
            when(it) {
                is GeoUiState.Idle -> {}
                is GeoUiState.Loading -> {
                    showLoader()
                }
                is GeoUiState.Success -> {
                    if (it.geoCodingResponse.isNotEmpty()) {
                        if (it.geoCodingResponse.size > 1) {
                            hideDialog()
                            showCitiesAvailable(it.geoCodingResponse)
                        } else {
                            val geo = it.geoCodingResponse[0]
                            viewModel.getWeatherData(geo.lat, geo.lon, OPEN_WEATHER_MAP_APP_KEY)
                            loadMap(geo.lat, geo.lon)
                        }
                    } else {
                        hideDialog()
                        showMessageDialog("No cities found with this name. Please try with a different name.")
                    }
                    viewModel.resetGeoUiState()
                }
                is GeoUiState.Failure -> {
                    hideDialog()
                    showMessageDialog(it.message)
                    viewModel.resetGeoUiState()
                }
            }
        }

        viewModel.weatherUiState.collectLifeCycleAware(this) {
            when(it) {
                is WeatherUiState.Idle -> {}
                is WeatherUiState.Loading -> {showLoader()}
                is WeatherUiState.Success -> {
                    viewModel.resetWeatherUiState()
                    hideDialog()
                    showWeatherReport(it.geoCodingResponse)
                }
                is WeatherUiState.Failure -> {
                    hideDialog()
                    showMessageDialog(it.message)
                    viewModel.resetWeatherUiState()
                }
            }
        }
    }

    private fun showCitiesAvailable(geoCodingResponse: GeoCodingResponse) {

        val dialog = AlertDialog.Builder(this).create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val cityDetailsBinding = CitiesDetailsLayoutBinding.inflate(layoutInflater)
        dialog.apply {
            setView(cityDetailsBinding.root)
            show()
        }
        geoCodingResponse.forEach { geo ->
            val b = CityDetailItemBinding.inflate(layoutInflater)
            val state = geo.state?.let { ", $it" } ?: ""
            val country = geo.country?.let { ", $it" } ?: ""
            val cityName = "${geo.name}$state$country"
            b.cityNameText.text = cityName
            b.root.setOnClickListener {
                dialog.dismiss()
                viewModel.getWeatherData(geo.lat, geo.lon, OPEN_WEATHER_MAP_APP_KEY)
            }
            cityDetailsBinding.citiesLayout.addView(b.root)
        }
    }

    private fun showWeatherReport(weather: WeatherData) {
        val dialog = AlertDialog.Builder(this).create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val binding = WeatherDetailsLayoutBinding.inflate(layoutInflater)
        dialog.apply {
            setView(binding.root)
            show()
        }
        weather.coord?.let {
            loadMap(it.lat, it.lon)
        }
        binding.apply {
            val name = weather.name ?: "NA"
            cityTitle.text = name
            weather.main?.temp?.let {
                val fahrenheit = roundToDecimal(kelvinToFahrenheit(it), 1)
                val celsius = roundToDecimal(kelvinToCelsius(it), 1)
                val temp = "$celsius°C / $fahrenheit°F"
                temperatureTitle.text = temp
                temperatureTitle.visible()
                temperatureText.visible()
            }

            if (weather.weather?.isNotEmpty() == true) {
                val w = weather.weather[0]
                weatherTitle.text = w.main
                weatherTitle.visible()
                weatherText.visible()
                try {
                    if (w.icon.isNullOrEmpty().not()) {
                        lifecycleScope.launch {
                            val bitmap = loadImage("$OPEN_MAP_ICON_BASE_URL${w.icon}.png", this@MainActivity)
                            bitmap?.let {
                                weatherIcon.setImageBitmap(bitmap)
                                weatherIcon.visible()
                            } ?: weatherIcon.gone()
                        }
                    } else {
                        weatherIcon.gone()
                    }
                } catch (e: Exception) {
                    weatherIcon.gone()
                }

                descriptionTitle.text = w.description
                descriptionTitle.visible()
                descriptionText.visible()
            }
        }

    }

    private fun showLoader() {
        if (progressDialog != null && progressDialog?.isShowing == true) return
        progressDialog = Dialog(this)
        progressDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        progressDialog?.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.progress_dialog)
            setCancelable(false)
            show()
        }
    }
    private fun hideDialog() {
        progressDialog?.let {
            if (progressDialog?.isShowing == true) progressDialog?.dismiss()
        }
    }

    private fun loadMap(latitude: Double, longitude: Double) {
        binding.mapView.mapboxMap.also {
            it.setCamera(
                CameraOptions.Builder()
                    .center(Point.fromLngLat(longitude, latitude))
                    .zoom(8.0)
                    .build()
            )
        }.loadStyle(
            styleExtension = style(Style.STANDARD) {
                // prepare blue marker from resources
                +image(
                    BLUE_ICON_ID,
                    ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_location)!!.toBitmap()
                )
                +geoJsonSource(SOURCE_ID) {
                    geometry(Point.fromLngLat(longitude, latitude))
                }
                +symbolLayer(LAYER_ID, SOURCE_ID) {
                    iconImage(BLUE_ICON_ID)
                    iconAnchor(IconAnchor.BOTTOM)
                }
            }
        )
    }

    /**
     * Permission launcher to ask for permission
     */
    private val requestLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission granted, proceed with location collection
                collectUserLocation()
            } else {
                // Permission denied, handle accordingly
//                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
                askForPermissionDialog()
            }
        }


    /**
     * Checks whether the location (only COARSE Location) is granted or not
     * If not granted then ask user, else proceed to start collecting location
     */
    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, proceed with location collection
            collectUserLocation()
        } else {
            // Permission is not granted, request it using contract
            requestLocationPermission.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    /**
     * First checks if the user has granted the location permission, then ask if not granted
     * If permission is granted then call location service to get the location
     */
    private fun collectUserLocation() {
        // Checking for permission
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
            return
        }


        // check if GPS is enabled or not
        if (isLocationEnabled().not()) {
            showMessageDialog("Looks like the location service is disabled. Please turn location ON to proceed.")
            return
        }


        // Use LocationManager or FusedLocationProviderClient to get the user's location
        // For example, using FusedLocationProviderClient:
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location
                if (location != null) {

                    // load map
                    loadMap(location.latitude, location.longitude)

                    // get weather data
                    viewModel.getWeatherData(location.latitude, location.longitude, OPEN_WEATHER_MAP_APP_KEY)
                } else {
                    val msg = "Can't access location. Please try again after restarting the app.\nIt may take some time to active the service."
                    showMessageDialog(msg)
                }
            }.addOnFailureListener {
                // Failed to get the location data
                showToast("Error: ${it.message}")
            }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    companion object {
        private const val OPEN_WEATHER_MAP_APP_KEY = "ac8e7b97ca9ba86dc96b9ee9baa7282b"
        private const val OPEN_MAP_ICON_BASE_URL = "https://openweathermap.org/img/wn/"
        private const val BLUE_ICON_ID = "blue"
        private const val SOURCE_ID = "source_id"
        private const val LAYER_ID = "layer_id"
    }

}