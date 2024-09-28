package com.example.weather.model

import ForcastResponse
import WeatherResponse
import android.content.SharedPreferences
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow


interface IRepository {
    suspend fun getWeatherHourly(lon: String, lat: String, appid: String, units:String, lang:String): Flow<WeatherResponse>
    suspend fun getWeeklyWeather(lon: String, lat: String, appid: String, units:String, lang:String): Flow<ForcastResponse>
    suspend fun getWeather(lon: String, lat: String, appid: String, units:String, lang:String): Flow<WeatherResponse>
    fun updateSettings(sharedpref: SharedPreferences, updatedSettings: MutableMap<String,String>)
    fun iniateSettings(sharedpref: SharedPreferences)
    fun getSettings(sharedpref: SharedPreferences): Flow<MutableList<String?>>
    fun saveWeatherResponse(get: SharedPreferences, data: MutableMap<String, String>)
    fun saveWeeklyResponse(get: SharedPreferences, data: MutableList<Triple<String, String, String>>)
    fun saveHourlyResponse(get: SharedPreferences, list: MutableList<Triple<String, String, String>>)
    fun getFromSharedPref(sharedPrefObj: MutableMap<String, SharedPreferences>)
: Flow<Triple<MutableMap<String, String>, List<Triple<String, String, String>>, List<Triple<String, String, String>>>>

    fun saveLocation(myLatLng: Pair<String, LatLng>, sharedpref: SharedPreferences)
    fun getFavourites(sharedpref: SharedPreferences): Flow<MutableList<Pair<String,LatLng>>>
     fun deleteFavourite(city: LatLng, sharedpref: SharedPreferences)

}