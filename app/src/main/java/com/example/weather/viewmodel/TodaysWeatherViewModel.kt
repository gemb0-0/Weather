package com.example.weather.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.BuildConfig
import com.example.weather.R
import com.example.weather.model.IRepository
import com.example.weather.model.RemoteDataSource
import com.example.weather.model.Repository
import com.example.weather.model.WeatherResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
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

    private val _weather = MutableLiveData<WeatherResponse>()
    val weather: LiveData<WeatherResponse> = _weather

    private val _hourlyWeather: MutableLiveData<List<Triple<String, String, String>>> = MutableLiveData()
    val hourlyWeather: LiveData<List<Triple<String, String, String>>> = _hourlyWeather


    fun getweather() {

    }

    fun getLocalWeather(location: Location) {
        GlobalScope.launch(Dispatchers.IO) {
            val res = _repo.getWeather(
                location.longitude.toString(),
                location.latitude.toString(),
                BuildConfig.OPEN_WEATHER_API_KEY,
                "metric",
                "en"
            )
            val list: MutableList<Triple<String, String, String>> = mutableListOf()
            for (i in res.list.subList(3, res.list.size)) {
                val time = i.dt_txt.split(" ")[1].substringBeforeLast(":")
                val temp = i.main.temp.toInt().toString() + "°C" //handle this
                list.add(Triple(time, temp, i.weather[0].icon))
            }
            _hourlyWeather.postValue(list)
            Log.i("HourlyWeather", _hourlyWeather.toString())


            _weather.postValue(res)
        }
    }

    @SuppressLint("MissingPermission")
    fun getFreshLocation() {
        fusedLocation.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                Log.i("Locationnnnnnnnnn", "Lat: ${location.latitude}, Long: ${location.longitude}")
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