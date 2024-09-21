package com.example.weather.model

class Repository : IRepository {
    val remoteDataSource: IRemoteDataSource
    init {
        remoteDataSource= RemoteDataSource.getInstance()
    }


    override suspend fun getWeather(
        lon: String,
        lat: String,
        appid: String,
        units: String,
        lang: String
    ): WeatherResponse {
        return remoteDataSource.getWeather(lon, lat, appid, units, lang).body()!!
    }

}