package com.example.weather.view.todaysWeather

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather.BuildConfig
import com.example.weather.R
import com.example.weather.model.remoteDataSource.ApiResponse
import com.example.weather.model.IRepository
import com.example.weather.Utils.Utils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TodaysWeatherViewModel(
    val _repo: IRepository,
    val sharedpref: MutableMap<String, SharedPreferences>

) : ViewModel() {
    lateinit var locationManager: LocationManager

    private var _weather =
        MutableStateFlow<ApiResponse<MutableMap<String, String>>>(ApiResponse.Loading)
    val weather: StateFlow<ApiResponse<MutableMap<String, String>>> = _weather


    private var _hourlyWeather: MutableStateFlow<ApiResponse<List<Triple<String, String, String>>>> =
        MutableStateFlow(
            ApiResponse.Loading
        )
    val hourlyWeather: StateFlow<ApiResponse<List<Triple<String, String, String>>>> = _hourlyWeather

    private var _weeklyWeather: MutableStateFlow<ApiResponse<List<Triple<String, String, String>>>> =
        MutableStateFlow(ApiResponse.Loading)
    val weeklyWeather: StateFlow<ApiResponse<List<Triple<String, String, String>>>> = _weeklyWeather

    private val _Settings = MutableStateFlow<MutableList<String?>>(mutableListOf())
    val Settings: StateFlow<MutableList<String?>> = _Settings

    fun getFromSharedPref(sharedPrefObj: MutableMap<String, SharedPreferences>) {
        viewModelScope.launch {
            _repo.getFromSharedPref(sharedPrefObj).collect() { it ->
                Log.i("SharedPrefW", it.first.toString())
                Log.i("SharedPrefH", it.second.toString())
                Log.i("SharedPrefW", it.third.toString())
                _weather.value = ApiResponse.Success(it.first)
                _hourlyWeather.value = ApiResponse.Success(it.second)
                _weeklyWeather.value = ApiResponse.Success(it.third)


            }
        }
    }

    fun getLocalWeather( location: LatLng) {
        getSettings()

//        if (_Settings.value[3] == "map" && _Settings.value[5] != null) {
//            val json = _Settings.value[5]
//            val mainLocation = Gson().fromJson<Pair<String, LatLng>>(json, object : TypeToken<Pair<String, LatLng>>() {}.type)
//            Log.i("MainLocationnn map", mainLocation.toString())
//            if (mainLocation != null) {
//            location = mainLocation.second
//            }
//        }

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
                    val list: MutableMap<String, String> = mutableMapOf()
                    list["temp"] = convertTemp(data.main!!.temp)
                    list["feels_like"] = convertTemp(data.main.feels_like)
                    list["temp_max"] = convertTemp(data.main.temp_max)
                    list["temp_min"] = convertTemp(data.main.temp_min)
                    list["description"] = data.weather!![0].description
                    list["pressure"] = data.main.pressure.toString() + " hPa"
                    list["visibility"] = data.visibility.toString() + " m"
                    list["sunrise"] =
                        Utils.convertUnixToTime(data.sys!!.sunrise!!.toLong(), data.timezone!!)
                    list["sunset"] =
                        Utils.convertUnixToTime(data.sys.sunset!!.toLong(), data.timezone)
                    list["wind_speed"] = convertWindSpeed(data.wind!!.speed)
                    list["humidity"] = data.main.humidity.toString() + "%"
                    list["dayInfo"] = formatDateFromTimestamp(data.dt!!.toLong())
                    list["icon"] = data.weather[0].icon
                    list["city"] = data.name.toString()
                    _weather.value = ApiResponse.Success(list)
                    _repo.saveWeatherResponse(sharedpref["weatherResponse"]!!, list)

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
                        val weatherDesc = i.weather[0].description
                        val temp = convertTemp(i.temp.min) + "/" + convertTemp(i.temp.max)
                        listWeekly.add(Triple(weatherDesc, temp, i.weather[0].icon))
                    }
                    Log.i("WeeklyWeatherList", listWeekly.toString())
                    _weeklyWeather.value = ApiResponse.Success(listWeekly)
                    _repo.saveWeeklyResponse(sharedpref.get("weeklyWeatherResponse")!!, listWeekly)

                }
            } catch (e: Exception) {
                _weeklyWeather.value = ApiResponse.Error(e.message ?: "Unknown error")
            }

            try {
                _repo.getWeatherHourly(
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
                    _repo.saveHourlyResponse(sharedpref.get("hourlyWeatherResponse")!!, list)

                }
            } catch (e: Exception) {
                _hourlyWeather.value = ApiResponse.Error(e.message ?: "Unknown error")
            }
        }


    }

    fun getSettings() {
        viewModelScope.launch {
            _repo.getSettings(sharedpref["settings"]!!).collect { it ->
                _Settings.value = it
                Log.i("Settings", it.toString())


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
            "km/h" -> return speed.toInt().toString() + " km/h"
            else -> return Utils.kmToMiles(speed).toInt().toString() + " miles/h"

        }
    }

    fun formatDateFromTimestamp(timestamp: Long): String {
        val date = Date(timestamp * 1000L)

        val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())

        return dateFormat.format(date)
    }

    @SuppressLint("MissingPermission")
    fun getFreshLocation( fusedLocation: FusedLocationProviderClient) {
        fusedLocation.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                 Log.i("Locationnnnnnnnnn", "Lat: ${location.latitude}, Long: ${location.longitude}")
                getLocalWeather(LatLng(location.latitude, location.longitude))

            }
        }

    }


    class TodaysWeatherViewModelFactory(
        var _repo: IRepository,
        var sharedpref: MutableMap<String, SharedPreferences>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TodaysWeatherViewModel( _repo, sharedpref) as T
        }
    }

}
