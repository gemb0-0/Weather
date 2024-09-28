package com.example.weather.view.favourites

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather.model.IRepository
import com.example.weather.model.SharedConnctionStateViewModel
import com.example.weather.model.remoteDataSource.ApiResponse
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class FavouritesViewModel(val _repo: IRepository, val sharedpref: SharedPreferences): ViewModel() {

    private var _fav = MutableStateFlow<MutableList<Pair<String, LatLng>>>(mutableListOf())
    val fav: StateFlow<MutableList<Pair<String, LatLng>>> = _fav

    fun getFavourites() {
        viewModelScope.launch {
            _repo.getFavourites(sharedpref).collect { it ->
                _fav.value = it.toMutableList()
            }
        }
    }

    fun deleteFavourite(city: LatLng) {
        viewModelScope.launch {
            _repo.deleteFavourite(city, sharedpref)
            getFavourites()
        }
    }


    class FavouritesViewModelFactory(
             private val _repo: IRepository,
             private val sharedpref: SharedPreferences
         ) : ViewModelProvider.Factory {
             override fun <T : ViewModel> create(modelClass: Class<T>): T {
                 if (modelClass.isAssignableFrom(FavouritesViewModel::class.java)) {
                     return FavouritesViewModel(_repo, sharedpref) as T
                 }
                 throw IllegalArgumentException("Unknown ViewModel class")
             }
         }



     }