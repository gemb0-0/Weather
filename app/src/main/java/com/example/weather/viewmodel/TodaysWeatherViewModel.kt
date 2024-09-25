package com.example.weather.viewmodel

import WeatherResponse
import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationManager
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

class TodaysWeatherViewModel(
    var fusedLocation: FusedLocationProviderClient,
    var _repo: IRepository
) : ViewModel() {
    lateinit var locationManager: LocationManager

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    init {
        fusedLocationProviderClient = fusedLocation
    }

    private val _weather = MutableStateFlow<ApiResponse<WeatherResponse>>(ApiResponse.Loading)
    val weather: StateFlow<ApiResponse<WeatherResponse>> = _weather

    private val _hourlyWeather: MutableStateFlow<ApiResponse<List<Triple<String, String, String>>>> = MutableStateFlow(
        ApiResponse.Loading)
    val hourlyWeather: StateFlow<ApiResponse<List<Triple<String, String, String>>>> = _hourlyWeather

    private val _weeklyWeather: MutableStateFlow<ApiResponse<List<Triple<String, String, String>>>> = MutableStateFlow(ApiResponse.Loading)
    val weeklyWeather:  StateFlow<ApiResponse<List<Triple<String, String, String>>>> = _hourlyWeather


    fun getLocalWeather(location: Location) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _weather.value = ApiResponse.Loading
                 _repo.getWeather(
                    location.longitude.toString(),
                    location.latitude.toString(),
                    BuildConfig.OPEN_WEATHER_API_KEY,
                    "metric",
                    R.string.lang.toString()
                ).collect() { data ->
                    _weather.value = ApiResponse.Success(data)
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
                    R.string.lang.toString()
                ).collect() { data ->
                    val listWeekly: MutableList<Triple<String, String, String>> = mutableListOf()
                    for (i in data.list) {
                        val weatherdesc = i.weather[0].description
                        val temp = i.temp.min.toInt().toString() + "/" + i.temp.max.toInt() + "°C"
                        listWeekly.add(Triple(weatherdesc, temp, i.weather[0].icon))
                    }
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
                R.string.lang.toString()
            ).collect() { data ->
                val list: MutableList<Triple<String, String, String>> = mutableListOf()
                for (i in data.list.subList(2, data.list.size)) {
                    val time = i.dt_txt!!.split(" ")[1].substringBeforeLast(":")
                    val temp = i.main.temp.toInt().toString() + "°C" //handle this
                    list.add(Triple(time, temp, i.weather[0].icon))
                }
                _hourlyWeather.value = ApiResponse.Success(list)
            }
        } catch (e: Exception) {
            _hourlyWeather.value = ApiResponse.Error(e.message ?: "Unknown error")
        }



//
//            val res = _repo.getWeatherHourly(
//                location.longitude.toString(),
//                location.latitude.toString(),
//                BuildConfig.OPEN_WEATHER_API_KEY,
//                "metric",
//                R.string.lang.toString()
//            )
//            val list: MutableList<Triple<String, String, String>> = mutableListOf()
//            for (i in res.list.subList(2, res.list.size)) {
//                val time = i.dt_txt!!.split(" ")[1].substringBeforeLast(":")
//                val temp = i.main.temp.toInt().toString() + "°C" //handle this
//                list.add(Triple(time, temp, i.weather[0].icon))
//            }
//            _hourlyWeather.postValue(list)
//            _weather.postValue(res)
//
//            Log.i("HourlyWeather", res.toString())
//
//
//            val weekly = _repo.getWeeklyWeather(
//                location.longitude.toString(),
//                location.latitude.toString(),
//                BuildConfig.OPEN_WEATHER_API_KEY,
//                "metric",
//                R.string.lang.toString()
//            )
//
//            Log.i("WeeklyWeather", weekly.toString())
//            val listWeekly: MutableList<Triple<String, String, String>> = mutableListOf()
//
//            for (i in weekly.list) {
//                val weatherdesc = i.weather[0].description
//                val temp = i.temp.min.toInt().toString() + "/" + i.temp.max.toInt() + "°C"
//                listWeekly.add(Triple(weatherdesc, temp, i.weather[0].icon))
//            }
//
//            _weeklyWeather.postValue(listWeekly)
//
//
//

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
        var fusedLocation: FusedLocationProviderClient, var _repo: IRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TodaysWeatherViewModel(fusedLocation, _repo) as T
        }
    }

}


/*
//@SuppressLint("MissingPermission")
//fun getFreshLocation():Pair<Double,Double> {
//    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
//    fusedLocationProviderClient.requestLocationUpdates(
//        LocationRequest.Builder(0).apply { setPriority(Priority.PRIORITY_HIGH_ACCURACY) }
//            .build(),
//        object : LocationCallback() {
//            @RequiresApi(Build.VERSION_CODES.TIRAMISU) //why
//            override fun onLocationResult(locationResult: LocationResult) {
//                super.onLocationResult(locationResult)
//                textlat.text = locationResult.lastLocation?.latitude.toString()
//                textlong.text = locationResult.lastLocation?.longitude.toString()
//                Geocoder(this@MainActivity).getFromLocation(
//                    locationResult.lastLocation?.latitude!!,
//                    locationResult.lastLocation?.longitude!!,
//                    1,
//                    Geocoder.GeocodeListener {
//                        locDetails.text = it[0].locality+", "+it[0].countryName
//                    })
//
//            }
//        }, Looper.myLooper()
//
//    )
//    return Pair(textlat.text.toString().toDouble(),textlong.text.toString().toDouble())
//}
//
 */