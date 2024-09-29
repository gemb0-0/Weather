package com.example.weather.model.remoteDataSource

import ForcastResponse
import WeatherResponse
import com.example.weather.model.remoteDataSource.IRemoteDataSource

class FakeRemoteDatasource():IRemoteDataSource {
    override suspend fun getWeatherHourly(
        lon: String,
        lat: String,
        appid: String,
        units: String,
        lang: String
    ): WeatherResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getWeeklyWeather(
        lon: String,
        lat: String,
        appid: String,
        units: String,
        lang: String
    ): ForcastResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getWeather(
        lon: String,
        lat: String,
        appid: String,
        units: String,
        lang: String
    ): WeatherResponse {
        TODO("Not yet implemented")
    }
}