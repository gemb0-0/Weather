package com.example.weather.viewmodel

import WeatherResponse
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather.BuildConfig
import com.example.weather.R
import com.example.weather.model.remoteDataSource.ApiResponse
import com.example.weather.model.IRepository
import com.example.weather.model.Utils
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class TodaysWeatherViewModel(
    var fusedLocation: FusedLocationProviderClient,
    var _repo: IRepository,
    var sharedpref: SharedPreferences

) : ViewModel() {
    lateinit var locationManager: LocationManager

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    init {
        fusedLocationProviderClient = fusedLocation
    }

    private val _weather = MutableStateFlow<ApiResponse<MutableMap<String,String>>>(ApiResponse.Loading)
    val weather: StateFlow<ApiResponse<MutableMap<String,String>>> = _weather


    private val _hourlyWeather: MutableStateFlow<ApiResponse<List<Triple<String, String, String>>>> =
        MutableStateFlow(
            ApiResponse.Loading
        )
    val hourlyWeather: StateFlow<ApiResponse<List<Triple<String, String, String>>>> = _hourlyWeather

    private val _weeklyWeather: MutableStateFlow<ApiResponse<List<Triple<String, String, String>>>> =
        MutableStateFlow(ApiResponse.Loading)
    val weeklyWeather: StateFlow<ApiResponse<List<Triple<String, String, String>>>> = _weeklyWeather

    private val _Settings = MutableStateFlow<MutableList<String?>>(mutableListOf())
    fun getSettings() {
        viewModelScope.launch {
            _repo.getSettings(sharedpref).collect { it ->
                _Settings.value = it
            }

        }

    }


    fun convertTemp(temp: Double): String {
        when (_Settings.value[1]) {
            "°C" -> return temp.toInt().toString() + "°C"
            "°F" -> return Utils.celsiusToFahrenheit(temp).toInt().toString() + "°F"
            else -> return Utils.celsiusToKelvin(temp).toInt().toString() + "°K"
        }
    }
    fun convertWindSpeed(speed: Double): String {
        when (_Settings.value[2]) {
            "km/h" -> return speed.toInt().toString() + "km/h"
            else-> return Utils.kmToMiles(speed).toInt().toString() + "miles/h"

        }
    }


    fun getLocalWeather(location: Location) {
        getSettings()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _weather.value = ApiResponse.Loading
                _repo.getWeather(
                    location.longitude.toString(),
                    location.latitude.toString(),
                    BuildConfig.OPEN_WEATHER_API_KEY,
                    "metric",
                    Locale.getDefault().toString()
                ).collect() { data ->
                    Log.i("WeatherdataResponse", data.toString())
                  var list: MutableMap<String,String> = mutableMapOf()
                    list["temp"] = convertTemp(data.main!!.temp)
                    list["feels_like"] = convertTemp(data.main.feels_like)
                    list["temp_max"] = convertTemp(data.main.temp_max)
                    list["temp_min"] = convertTemp(data.main.temp_min)
                    list["description"] = data.weather!![0].description
                    list["pressure"] = data.main.pressure.toString()+" hPa"
                    list["visibility"] = data.visibility.toString() + " m"
                    list["sunrise"] = Utils.convertUnixToTime(data.sys!!.sunrise!!.toLong(), data.timezone!!)
                    list["sunset"] = Utils.convertUnixToTime(data.sys.sunset!!.toLong(), data.timezone!!)
                    list["wind_speed"] =  convertWindSpeed(data.wind!!.speed)
                    list["humidity"] = data.main.humidity.toString() + "%"

                    _weather.value = ApiResponse.Success(list)
                }
            } catch (e: Exception) {
                _weather.value = ApiResponse.Error(e.message ?: "Unknown error")
            }

            try {
                val weekly = _repo.getWeeklyWeather(
                    location.longitude.toString(),
                    location.latitude.toString(),
                    BuildConfig.OPEN_WEATHER_API_KEY,
                    "metric",
                    Locale.getDefault().toString()
                ).collect() { data ->
                    Log.i("WeeklyWeatherResponse", data.toString())
                    val listWeekly: MutableList<Triple<String, String, String>> = mutableListOf()
                    for (i in data.list) {
                        val weatherdesc = i.weather[0].main
                        val temp = convertTemp(i.temp.min) + "/" + convertTemp(i.temp.max)
                        listWeekly.add(Triple(weatherdesc, temp, i.weather[0].icon))
                    }
                    Log.i("WeeklyWeatherList", listWeekly.toString())
                    _weeklyWeather.value = ApiResponse.Success(listWeekly)
                }
            } catch (e: Exception) {
                _weeklyWeather.value = ApiResponse.Error(e.message ?: "Unknown error")
            }

            try {
                val res = _repo.getWeatherHourly(
                    location.longitude.toString(),
                    location.latitude.toString(),
                    BuildConfig.OPEN_WEATHER_API_KEY,
                    "metric",
                    Locale.getDefault().toString()
                ).collect() { data ->
                    val list: MutableList<Triple<String, String, String>> = mutableListOf()
                    for (i in data.list.subList(2, data.list.size)) {
                        val time = i.dt_txt!!.split(" ")[1].substringBeforeLast(":")
                        val temp = convertTemp(i.main!!.temp)
                        list.add(Triple(time, temp, i.weather[0].icon))
                    }
                    _hourlyWeather.value = ApiResponse.Success(list)
                }
            } catch (e: Exception) {
                _hourlyWeather.value = ApiResponse.Error(e.message ?: "Unknown error")
            }


        }


    }

    @SuppressLint("MissingPermission")
    fun getFreshLocation() {
        fusedLocation.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                // Log.i("Locationnnnnnnnnn", "Lat: ${location.latitude}, Long: ${location.longitude}")
                getLocalWeather(location)
            }
        }

    }


    class TodaysWeatherViewModelFactory(
        var fusedLocation: FusedLocationProviderClient,
        var _repo: IRepository,
        var sharedpref: SharedPreferences
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TodaysWeatherViewModel(fusedLocation, _repo, sharedpref) as T
        }
    }

}
