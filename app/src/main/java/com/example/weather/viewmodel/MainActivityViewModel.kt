package com.example.weather.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.model.IRepository

class MainActivityViewModel(var _repo: IRepository, var sharedpref: SharedPreferences) :
    ViewModel() {


    fun getSettings() {
        _repo.iniateSettings(sharedpref)
    }


    class MainActivityViewModelFactory(
        var _repo: IRepository, var sharedpref: SharedPreferences
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainActivityViewModel(_repo, sharedpref) as T
        }
    }
}
