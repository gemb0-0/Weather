package com.example.weather.model

interface IRepository {
    suspend fun getWeather(lon: String, lat: String, appid: String, units:String, lang:String): WeatherResponse
}