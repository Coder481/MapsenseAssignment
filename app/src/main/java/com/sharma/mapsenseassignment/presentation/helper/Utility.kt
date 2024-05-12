package com.sharma.mapsenseassignment.presentation.helper

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun kelvinToFahrenheit(kelvin: Double): Double {
    return kelvin * 9 / 5 - 459.67
}

fun kelvinToCelsius(kelvin: Double): Double {
    return kelvin - 273.15
}

fun roundToDecimal(value: Double, places: Int): Double {
    return String.format("%.${places}f", value).toDouble()
}

fun formatDateFromTimestamp(timestamp: Long, format: String): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat(format, Locale.getDefault())
    return formatter.format(date)
}
