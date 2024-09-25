package com.example.weather.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather.model.IRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainActivityViewModel(var _repo: IRepository, var sharedpref: SharedPreferences) :
    ViewModel() {

    private val _Settings = MutableStateFlow<MutableList<String?>> (mutableListOf())
    val Settings: StateFlow<MutableList<String?>> = _Settings

    fun getSettings() {
        viewModelScope.launch {
            _repo.getSettings(sharedpref).collect{ it->
                _Settings.value = it
            }

        }

    }

    class MainActivityViewModelFactory(
        var _repo: IRepository, var sharedpref: SharedPreferences
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainActivityViewModel(_repo, sharedpref) as T
        }
    }
}
