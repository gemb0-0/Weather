package com.example.weather.view.alert

import WeatherResponse
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather.BuildConfig
import com.example.weather.model.IRepository
import com.example.weather.model.remoteDataSource.ApiResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class AlertViewModel(val _repo: IRepository, val sharedpref: SharedPreferences) : ViewModel() {




    fun setAlarm(timeInMillis: Long, location: Location) {
        viewModelScope.launch(Dispatchers.IO) {
            _repo.getWeatherHourly(
                location.longitude.toString(),
                location.latitude.toString(),
                BuildConfig.OPEN_WEATHER_API_KEY,
                "metric",
                Locale.getDefault().toString()
            ).collect() { data ->

                for (i in data.list) {
                    Log.i("Timeeeeeeee", i.dt.toString())
                    if (i.dt == timeInMillis) {
                        Log.i("Alertyyyyy", "Alert")

                        val alertData = mutableMapOf<String, Pair<String,String>>()
                        alertData[i.dt.toString()] = Pair(i.main!!.temp.toInt().toString(), i.weather[0].description)
                        _repo.saveAlert(sharedpref, alertData)
                    }
                }
            }
        }

    }

    @SuppressLint("MissingPermission")
    fun initializeAlarm(
        fusedLocation: FusedLocationProviderClient,
        timeInMillis: Long,

    ) {
        fusedLocation.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                Log.i("Location", location.toString())
                val loc = LatLng(location.latitude, location.longitude)
                setAlarm(timeInMillis, location)
            }
        }

    }

    class AlertViewModelFactory(
        private val _repo: IRepository,
        private val sharedpref: SharedPreferences
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AlertViewModel::class.java)) {
                return AlertViewModel(_repo, sharedpref) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}