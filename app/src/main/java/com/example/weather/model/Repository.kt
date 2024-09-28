package com.example.weather.model

import ForcastResponse
import com.example.weather.model.localDataSource.Sharedpref
import WeatherResponse
import android.content.SharedPreferences
import com.example.weather.model.localDataSource.ISharedpref
import com.example.weather.model.remoteDataSource.IRemoteDataSource
import com.example.weather.model.remoteDataSource.RemoteDataSource
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Repository : IRepository {
    val remoteDataSource: IRemoteDataSource
    val localDataSource: ISharedpref

    init {
        remoteDataSource = RemoteDataSource.getInstance()
        localDataSource = Sharedpref()
    }


    override suspend fun getWeatherHourly(
        lon: String,
        lat: String,
        appid: String,
        units: String,
        lang: String
    ): Flow<WeatherResponse> {
           // val response = remoteDataSource.getWeatherHourly(lon, lat, appid, units, lang)
        return flow {
            val response = remoteDataSource.getWeatherHourly(lon, lat, appid, units, lang)
            emit(response)
        }
    }

    override suspend fun getWeeklyWeather(
        lon: String,
        lat: String,
        appid: String,
        units: String,
        lang: String
    ): Flow<ForcastResponse> {

        return flow {
            val response = remoteDataSource.getWeeklyWeather(lon, lat, appid, units, lang)
            emit(response)
        }
    }

    override suspend fun getWeather(
        lon: String,
        lat: String,
        appid: String,
        units: String,
        lang: String
    ): Flow<WeatherResponse> {

        return flow {
            val response = remoteDataSource.getWeather(lon, lat, appid, units, lang)
            emit(response)
        }

    }

    override fun updateSettings(sharedpref: SharedPreferences, updatedSettings: MutableMap<String,String>) {
        localDataSource.updateSettings(sharedpref, updatedSettings)
    }

    override fun iniateSettings(sharedpref: SharedPreferences) {
        localDataSource.getSettings(sharedpref)
    }

    override fun getSettings(sharedpref: SharedPreferences): Flow<MutableList<String?>> {
       return localDataSource.getSettings(sharedpref)
    }

    override fun saveWeatherResponse(get: SharedPreferences, data: MutableMap<String, String>) {
        localDataSource.saveWeatherResponse(get, data)
    }

    override fun saveWeeklyResponse(get: SharedPreferences, data: MutableList<Triple<String, String, String>>) {
        localDataSource.saveWeeklyResponse(get, data)
    }

    override fun saveHourlyResponse(
        get: SharedPreferences,
        list: MutableList<Triple<String, String, String>>
    ) {
        return localDataSource.saveHourlyResponse(get, list)
    }

    override fun getFromSharedPref(sharedPrefObj: MutableMap<String, SharedPreferences>)    : Flow<Triple<MutableMap<String, String>, List<Triple<String, String, String>>, List<Triple<String, String, String>>>>
    {

          return localDataSource.getFromSharedPref(sharedPrefObj)

    }

    override fun saveLocation(myLatLng: Pair<String, LatLng>, sharedpref: SharedPreferences) {
        localDataSource.saveLocation(myLatLng, sharedpref)
    }

    override fun getFavourites(sharedpref: SharedPreferences): Flow<MutableList<Pair<String, LatLng>>> {
        return localDataSource.getFavourites(sharedpref)
    }

    override fun deleteFavourite(city: LatLng, sharedpref: SharedPreferences) {
        localDataSource.deleteFavourite(city, sharedpref)
    }


}