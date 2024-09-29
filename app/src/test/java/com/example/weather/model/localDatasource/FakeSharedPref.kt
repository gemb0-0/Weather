package com.example.weather.model.localDatasource

import android.content.SharedPreferences
import com.example.weather.model.localDataSource.ISharedpref
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

class FakeSharedPref(val list :List<Pair<String,LatLng>>,sharedpref: SharedPreferences,weekly: MutableList<Triple<String, String, String>>):ISharedpref {
    override fun updateSettings(
        sharedpref: SharedPreferences,
        updatedSettings: MutableMap<String, String>
    ) {
        TODO("Not yet implemented")
    }

    override fun getSettings(sharedpref: SharedPreferences): Flow<MutableList<String?>> {
        TODO("Not yet implemented")
    }

    override fun saveWeatherResponse(get: SharedPreferences, data: MutableMap<String, String>) {
        TODO("Not yet implemented")
    }

    override fun saveWeeklyResponse(
        get: SharedPreferences,
        data: MutableList<Triple<String, String, String>>
    ) {
     get.edit().putString("weekly",data.toString()).apply()
    }

    override fun saveHourlyResponse(
        get: SharedPreferences,
        list: MutableList<Triple<String, String, String>>
    ) {
        TODO("Not yet implemented")
    }

    override fun getFromSharedPref(sharedPrefObj: MutableMap<String, SharedPreferences>): Flow<Triple<MutableMap<String, String>, List<Triple<String, String, String>>, List<Triple<String, String, String>>>> {
        TODO("Not yet implemented")
    }

    override fun saveLocation(myLatLng: Pair<String, LatLng>, sharedpref: SharedPreferences) {
        sharedpref.edit().putString("lat",myLatLng.second.latitude.toString()).apply()
        sharedpref.edit().putString("lon",myLatLng.second.longitude.toString()).apply()

    }

    override fun getFavourites(sharedpref: SharedPreferences): Flow<MutableList<Pair<String, LatLng>>> {
        TODO("Not yet implemented")
    }

    override fun deleteFavourite(city: LatLng, sharedpref: SharedPreferences) {
      sharedpref.edit().remove("lat").apply()
        sharedpref.edit().remove("lon").apply()

    }

    override fun saveMainLocation(favLocation: Pair<String, LatLng>, mainLoc: SharedPreferences) {
        TODO("Not yet implemented")
    }

    override fun saveAlert(
        sharedpref: SharedPreferences,
        alertData: MutableMap<String, Pair<String, String>>
    ) {
        TODO("Not yet implemented")
    }
}