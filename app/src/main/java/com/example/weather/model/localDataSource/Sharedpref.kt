package com.example.weather.model.localDataSource

import android.content.SharedPreferences
import com.example.weather.model.Utils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class Sharedpref:ISharedpref {

    override fun iniateSettings(sharedpref: SharedPreferences) {
        val language  = sharedpref.getString("language", "en")
        val temp = sharedpref.getString("temp", "celsius")
        val wind= sharedpref.getString("wind", "km/hour")
        val location = sharedpref.getString("location", "gps")
        val notification = sharedpref.getString("notification", "enable")
        UpdateUtils(language, temp, wind, location)


    }


    override fun updateSettings(sharedpref: SharedPreferences, UpdatedSettings: MutableList<String>) {
        val editor = sharedpref.edit()
        editor.putString("language", UpdatedSettings[0])
        editor.putString("temp", UpdatedSettings[1])
        editor.putString("wind", UpdatedSettings[2])
        editor.putString("location", UpdatedSettings[3])
         editor.putString("notification", UpdatedSettings[4])
        editor.apply()

        UpdateUtils(UpdatedSettings[0], UpdatedSettings[1], UpdatedSettings[2], UpdatedSettings[3])

    }

    override fun getSettings(sharedpref: SharedPreferences): Flow<MutableList<String?>> {
        val language  = sharedpref.getString("language", "en")
        val temp = sharedpref.getString("temp", "celsius")
        val wind= sharedpref.getString("wind", "km/hour")
        val location = sharedpref.getString("location", "gps")
        val notification = sharedpref.getString("notification", "enable")
        val settings  = mutableListOf(language, temp, wind, location, notification)
        return flow {
            emit(settings)
        }
    }


    private fun UpdateUtils(
        language: String?,
        temp: String?,
        wind: String?,
        location: String?
    ) {
        when (language) {
            "en" -> {
                Utils.setLanguage("en")
            }

            "ar" -> {
                Utils.setLanguage("ar")
            }
        }
        when (temp) {
            "celsius" -> {
                Utils.setTemp("°C")
            }

            "fahrenheit" -> {
                Utils.setTemp("°F")
            }

            "kelvin" -> {
                Utils.setTemp("K")
            }
        }
        when (wind) {
            "miles/hour" -> {
                Utils.setWind("m/h")
            }

            "kilometers/hour" -> {
                Utils.setWind("km/h")
            }
        }
        when (location) {
            "gps" -> {
                Utils.setLocation("gps")
            }

            "location" -> {
                Utils.setLocation("location")
            }
        }
    }

}