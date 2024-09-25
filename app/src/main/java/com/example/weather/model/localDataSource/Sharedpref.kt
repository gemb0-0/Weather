package com.example.weather.model.localDataSource

import android.content.SharedPreferences
import com.example.weather.model.Utils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class Sharedpref:ISharedpref {

    override fun iniateSettings(sharedpref: SharedPreferences) {
        val language  = sharedpref.getString("language", "en")
        val temp = sharedpref.getString("temp", "°C")
        val wind= sharedpref.getString("wind", "km/h")
        val location = sharedpref.getString("location", "gps")
        val notification = sharedpref.getString("notification", "enable")


    }


    override fun updateSettings(sharedpref: SharedPreferences, UpdatedSettings: MutableMap<String,String>) {
        val editor = sharedpref.edit()
        editor.putString("language", UpdatedSettings["language"])
        editor.putString("temp", UpdatedSettings["temp"])
        editor.putString("wind", UpdatedSettings["wind"])
        editor.putString("location", UpdatedSettings["location"])
        editor.putString("notification", UpdatedSettings["notification"])
        editor.apply()

    }

    override fun getSettings(sharedpref: SharedPreferences): Flow<MutableList<String?>> {
        val language  = sharedpref.getString("language", "en")
        val temp = sharedpref.getString("temp", "°C")
        val wind= sharedpref.getString("wind", "km/h")
        val location = sharedpref.getString("location", "gps")
        val notification = sharedpref.getString("notification", "enable")
        val settings  = mutableListOf(language, temp, wind, location, notification)
        return flow {
            emit(settings)
        }
    }




}