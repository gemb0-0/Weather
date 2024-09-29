package com.example.weather.view.map

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.model.IRepository
import com.google.android.gms.maps.model.LatLng

class MapsViewModel(
    val _repo: IRepository,
    val sharedpref: SharedPreferences,
   val  mainLoc: SharedPreferences?,
) : ViewModel() {
    fun saveLocation(myLatLng: Pair<String, LatLng>) {
        _repo.saveLocation(myLatLng, sharedpref)

    }

    fun saveMainLocation(favLocation: Pair<String, LatLng>) {
        _repo.saveMainLocation(favLocation, mainLoc!!)
    }


    class MapsViewModelFactory(
        val _repo: IRepository, val sharedpref: SharedPreferences, val mainLoc: SharedPreferences? =null
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MapsViewModel(
                _repo,
                sharedpref,mainLoc
            ) as T
        }
    }
}