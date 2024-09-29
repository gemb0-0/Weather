package com.example.weather.view.map

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather.model.IRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapsViewModel(
    val _repo: IRepository,
    val sharedpref: SharedPreferences,
) : ViewModel() {


    fun saveLocation(myLatLng: Pair<String, LatLng>) {
        _repo.saveLocation(myLatLng, sharedpref)

    }

    fun saveMainLocation(favLocation: Pair<String, LatLng>, mainLoc: SharedPreferences) {
        _repo.saveMainLocation(favLocation, mainLoc)
    }


    class MapsViewModelFactory(
        val _repo: IRepository, val sharedpref: SharedPreferences
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MapsViewModel(
                _repo,
                sharedpref
            ) as T
        }
    }
}