package com.example.weather.view.settings

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather.model.IRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(var _repo: IRepository, var sharedpref: SharedPreferences):ViewModel() {

    private val _Settings = MutableStateFlow<MutableList<String?>> (mutableListOf())
   val Settings: StateFlow<MutableList<String?>> = _Settings


    fun updateSettings(updatedSettings: MutableMap<String,String> ) {
        _repo.updateSettings(sharedpref, updatedSettings)
    }

    fun getSettings() {
        viewModelScope.launch {
            _repo.getSettings(sharedpref).collect{ it->
                _Settings.value = it
            }

        }

     //   _repo.getSettings(sharedpref)
    }


    class SettingsViewModelFactory(
        var _repo: IRepository, var sharedpref: SharedPreferences
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(_repo, sharedpref) as T
        }
    }

}