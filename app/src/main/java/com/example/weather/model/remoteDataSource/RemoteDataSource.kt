package com.example.weather.model.remoteDataSource

import ForcastResponse
import WeatherResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("forecast/hourly?")
    suspend fun getWeatherHourly(
        @Query("lon") lon: String,
        @Query("lat") lat: String,
        @Query("appid") appid: String,
        @Query("units") units: String ,
        @Query("lang") lang: String
    ): WeatherResponse

    @GET("forecast/daily?")
    suspend fun getWeatherWeekly(
        @Query("lon") lon: String,
        @Query("lat") lat: String,
        @Query("appid") appid: String,
        @Query("units") units: String ,
        @Query("lang") lang: String,
        @Query("cnt") cnt: Int = 11,
        ):ForcastResponse


    @GET("weather?")
    suspend fun getWeather(
        @Query("lon") lon: String,
        @Query("lat") lat: String,
        @Query("appid") appid: String,
        @Query("units") units: String ,
        @Query("lang") lang: String,
        ):WeatherResponse
}



object RetrofitClient {
    const val BASE_URL = "https://pro.openweathermap.org/data/2.5/"

    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}


class RemoteDataSource private constructor(): IRemoteDataSource {
    private val apiService: WeatherService = RetrofitClient.getInstance().create(WeatherService::class.java)
    companion object {
        @Volatile
        private var instance: RemoteDataSource? = null
        fun getInstance(): RemoteDataSource {
            return instance ?: synchronized(this) {
                instance ?: RemoteDataSource().also { instance = it }
            }
        }
    }



    override suspend fun getWeatherHourly(lon: String, lat: String, appid: String, units:String, lang:String): WeatherResponse{
        return apiService.getWeatherHourly(lon, lat, appid,units,lang )
    }

    override suspend fun getWeeklyWeather  (
        lon: String,
        lat: String,
        appid: String,
        units: String, lang: String): ForcastResponse {
        return apiService.getWeatherWeekly(lon, lat, appid,units,lang )
    }

    override suspend fun getWeather(
        lon: String,
        lat: String,
        appid: String,
        units: String,
        lang: String
    ): WeatherResponse {
        return apiService.getWeather(lon, lat, appid,units,lang )
    }

}

