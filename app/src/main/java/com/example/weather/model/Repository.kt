package com.example.weather.model

import ForcastResponse
import WeatherResponse
import android.content.SharedPreferences
import com.example.weather.model.localDataSource.ISharedpref
import com.example.weather.model.localDataSource.Sharedpref
import com.example.weather.model.remoteDataSource.IRemoteDataSource
import com.example.weather.model.remoteDataSource.RemoteDataSource
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

    override fun UpdateSettings(sharedpref: SharedPreferences, updatedSettings: MutableMap<String,String>) {
        localDataSource.updateSettings(sharedpref, updatedSettings)
    }

    override fun iniateSettings(sharedpref: SharedPreferences) {
        localDataSource.iniateSettings(sharedpref)
    }

    override fun getSettings(sharedpref: SharedPreferences): Flow<MutableList<String?>> {
       return localDataSource.getSettings(sharedpref)
    }


}