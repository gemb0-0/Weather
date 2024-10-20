package com.example.weather.model

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weather.model.localDatasource.FakeSharedPref
import com.example.weather.model.remoteDataSource.FakeRemoteDatasource
import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(AndroidJUnit4::class)
class RepositoryTest {

    private lateinit var repo: IRepository
    private lateinit var sharedPref: SharedPreferences

    @Before
    fun setUp() {
        sharedPref = ApplicationProvider.getApplicationContext<Context>().getSharedPreferences("test", Context.MODE_PRIVATE)
        repo = Repository(FakeRemoteDatasource(), FakeSharedPref(
            listOf(Pair("city", LatLng(0.0, 0.0))),
            sharedPref,
            mutableListOf()
        ))
    }

    @Test
    fun saveLocation_Location_Inserted_Successfully() {
        val location = Pair("city", LatLng(0.0, 0.0))
        repo.saveLocation(location, sharedPref)
        val x = sharedPref.getString("lat", "")
        val y = sharedPref.getString("lon", "")
        assertEquals("0.0", x)
        assertEquals("0.0", y)
    }
}
