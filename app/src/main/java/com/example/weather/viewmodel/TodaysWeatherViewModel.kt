package com.example.weather.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

class TodaysWeatherViewModel() :ViewModel() {
    val repo:IRepository
    lateinit var locationManager: LocationManager

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    init {
      repo = Repository()
    }
    private val _weather = MutableLiveData<WeatherResponse>()
    val weather : LiveData<WeatherResponse> = _weather
    fun getweather() {

    }

    fun getLocalWeather() :Pair<Double,Double>{

    }
}








//
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
//
//
//
