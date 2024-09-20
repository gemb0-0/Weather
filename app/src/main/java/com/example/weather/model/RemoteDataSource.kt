package com.example.weather.model

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("weather?")
    suspend fun getWeather(
        @Query("lon") lon: String,
        @Query("lat") lat: String,
        @Query("appid") appid: String
    ): Response<WeatherResponse>

}



object RetrofitClient {
     const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }
}



class RemoteDataSource private constructor() {
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
    suspend fun getWeather(lon: String, lat: String, appid: String): Response<WeatherResponse> {
        return apiService.getWeather(lon, lat, appid)
    }

}

