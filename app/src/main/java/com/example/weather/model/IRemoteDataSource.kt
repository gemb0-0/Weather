package com.example.weather.model

import retrofit2.Response

interface IRemoteDataSource {
    suspend fun getWeather(lon: String, lat: String, appid: String,units:String,lang:String): Response<WeatherResponse>
}