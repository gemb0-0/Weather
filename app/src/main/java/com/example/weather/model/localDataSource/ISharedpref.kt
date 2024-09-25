package com.example.weather.model.localDataSource

import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow

interface ISharedpref {
    fun iniateSettings(sharedpref: SharedPreferences)
    fun updateSettings(sharedpref: SharedPreferences, UpdatedSettings: MutableMap<String,String>)
    fun getSettings(sharedpref: SharedPreferences): Flow<MutableList<String?>>

}