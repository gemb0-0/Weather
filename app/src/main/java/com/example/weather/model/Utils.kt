package com.example.weather.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object Utils {


    fun celsiusToFahrenheit(temp: Double): Double {
        return (temp * 9 / 5) + 32
    }

    fun celsiusToKelvin(temp: Double): Double {
        return temp + 273.15
    }

    fun kmToMiles(speed: Double): Double {
        return speed * 0.621371
    }


    fun convertUnixToTime(unixTime: Long, timezoneOffset: Int): String {
        // Create a date object from the Unix timestamp
        val date = Date(unixTime * 1000) // Convert seconds to milliseconds
        // Create a SimpleDateFormat to format the time
        val sdf = SimpleDateFormat("HH:mm", Locale.ENGLISH)

        // Set the timezone based on the offset from UTC
        sdf.timeZone = TimeZone.getTimeZone("GMT+${timezoneOffset / 3600}")
        return sdf.format(date)
    }

}

