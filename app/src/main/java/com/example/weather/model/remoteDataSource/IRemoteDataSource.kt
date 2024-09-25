package com.example.weather.model.remoteDataSource

import ForcastResponse
import WeatherResponse

interface IRemoteDataSource {
   suspend fun getWeatherHourly(lon: String, lat: String, appid: String, units:String, lang:String): WeatherResponse
    suspend fun getWeeklyWeather(lon: String, lat: String, appid: String, units:String, lang:String): ForcastResponse
    suspend fun getWeather(lon: String, lat: String, appid: String, units:String, lang:String): WeatherResponse

}