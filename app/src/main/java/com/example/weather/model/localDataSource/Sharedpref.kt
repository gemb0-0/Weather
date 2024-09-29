package com.example.weather.model.localDataSource


import android.content.SharedPreferences
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow


class Sharedpref : ISharedpref {

    override fun updateSettings(
        sharedpref: SharedPreferences,
        updatedSettings: MutableMap<String, String>
    ) {
        val editor = sharedpref.edit()
        editor.putString("language", updatedSettings["language"])
        editor.putString("temp", updatedSettings["temp"])
        editor.putString("wind", updatedSettings["wind"])
        editor.putString("location", updatedSettings["location"])
        editor.putString("notification", updatedSettings["notification"])
        editor.putString("mainLocation", updatedSettings["mainLocation"])
        editor.apply()
        Log.i("SharedPref", "updateSettings: $updatedSettings")
    }

    override fun getSettings(sharedpref: SharedPreferences): Flow<MutableList<String?>> {
        val language = sharedpref.getString("language", "en")
        val temp = sharedpref.getString("temp", "°C")
        val wind = sharedpref.getString("wind", "km/h")
        val location = sharedpref.getString("location", "gps")
        val notification = sharedpref.getString("notification", "enable")
        val mainLoc = sharedpref.getString("mainLocation", null)
        val settings = mutableListOf(language, temp, wind, location, notification, mainLoc)
        Log.i("SharedPref", "getSettings: $settings")
        return flow {
            emit(settings)
        }
    }

    override fun saveWeatherResponse(get: SharedPreferences, data: MutableMap<String, String>) {
        val editor = get.edit()
        editor.putString("weather", Gson().toJson(data))
        editor.apply()
        Log.i("SharedPref", "saveWeatherResponse: $data")
    }

    override fun saveWeeklyResponse(get: SharedPreferences, data: MutableList<Triple<String, String, String>>) {
        val editor = get.edit()
        editor.putString("weekly", Gson().toJson(data))
        editor.apply()
        Log.i("SharedPref", "saveWeeklyResponse: $data")
    }


    override fun saveHourlyResponse(get: SharedPreferences, data: MutableList<Triple<String, String, String>>) {
        val editor = get.edit()
        editor.putString("hourly", Gson().toJson(data))
        editor.apply()
        Log.i("SharedPref", "saveHourlyResponse: $data")

    }


    fun loadWeatherResponse(get: SharedPreferences): MutableMap<String, String>? {
        val json = get.getString("weather", null)
        return json?.let {
            Gson().fromJson(it, object : TypeToken<MutableMap<String, String>>() {}.type)
        }
    }


    fun loadWeeklyResponse(get: SharedPreferences): List<Triple<String, String, String>>? {
        val json = get.getString("weekly", null)
        return json?.let {
            Gson().fromJson(it, object : TypeToken<List<Triple<String, String, String>>>() {}.type)
        }
    }


    fun loadHourlyResponse(get: SharedPreferences): List<Triple<String, String, String>>? {
        val json = get.getString("hourly", null)
        return json?.let {
            Gson().fromJson(it, object : TypeToken<List<Triple<String, String, String>>>() {}.type)
        }
    }


    override fun getFromSharedPref(sharedPrefObj: MutableMap<String, SharedPreferences>)
            : Flow<Triple<MutableMap<String, String>, List<Triple<String, String, String>>, List<Triple<String, String, String>>>> {
        return flow {
            val weather = loadWeatherResponse(sharedPrefObj["weatherResponse"]!!)
            val weekly = loadWeeklyResponse(sharedPrefObj["weeklyWeatherResponse"]!!)
            val hourly = loadHourlyResponse(sharedPrefObj["hourlyWeatherResponse"]!!)
            emit(Triple(weather!!, weekly!!, hourly!!))
        }
    }

    override fun saveLocation(myLatLng: Pair<String, LatLng>, sharedpref: SharedPreferences) {

        val favourites = decodeLocation(sharedpref)
        favourites.add(myLatLng)
        val editor = sharedpref.edit()
        editor.putString("favourites", Gson().toJson(favourites))
        editor.apply()
//        sharedpref.registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
//            Log.i("SharedPref", "saveLocation: $key")
//
//        })
    }

    override fun getFavourites(sharedpref: SharedPreferences): Flow<MutableList<Pair<String, LatLng>>> {
        val favourites = decodeLocation(sharedpref)
        Log.i("SharedPref", "getFavourites: $favourites")
        return flow {
            emit(favourites)
        }
    }

    override fun deleteFavourite(city: LatLng, sharedpref: SharedPreferences) {
        val favourites = decodeLocation(sharedpref)
        val cityString = "${city.latitude},${city.longitude}"
        Log.i("SharedPref", "deleteFavourite: ${LatLng(city.latitude, city.longitude)}")
        val newFavourites = favourites.filter { it.second!= city }.toMutableList()
        val editor = sharedpref.edit()
        editor.putString("favourites", Gson().toJson(newFavourites))
        editor.apply()
    }

    override fun saveMainLocation(newLocation: Pair<String, LatLng>, mainLoc: SharedPreferences) {
        val editor = mainLoc.edit()
        editor.putString("mainLocation", Gson().toJson(newLocation))
        editor.apply()

    }

    override fun saveAlert(
        sharedpref: SharedPreferences,
        alertData: MutableMap<String, Pair<String, String>>
    ) {
        val editor = sharedpref.edit()
        editor.putString("alert", Gson().toJson(alertData))
        editor.apply()
    }


    private fun decodeLocation(sharedpref: SharedPreferences): MutableList<Pair<String, LatLng>> {
    val json = sharedpref.getString("favourites", null)
    return json?.let {
        Gson().fromJson(it, object : TypeToken<MutableList<Pair<String, LatLng>>>() {}.type)
    } ?: mutableListOf()
}

}
