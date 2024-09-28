package com.example.weather.view.todaysWeather

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.R
import com.example.weather.databinding.FragmentTodaysWeatherBinding
import com.example.weather.model.remoteDataSource.ApiResponse
import com.example.weather.model.Repository
import com.example.weather.model.IRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import java.util.Locale

class TodaysWeather : Fragment() {
    lateinit var binding: FragmentTodaysWeatherBinding
    lateinit var viewModel: TodaysWeatherViewModel
    lateinit var adapter: TodaysWeatherAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTodaysWeatherBinding.inflate(inflater, container, false)
        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        val myLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recylerview.layoutManager = myLayoutManager
        binding.recylerview2.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val factory = TodaysWeatherViewModel.TodaysWeatherViewModelFactory(Repository(), sharedPrefObj())
        viewModel = ViewModelProvider(this, factory).get(TodaysWeatherViewModel::class.java)


        arguments?.let { bundle ->
            val city = bundle.getParcelable("city", LatLng::class.java)
            if (city != null) {
                (activity as AppCompatActivity).supportActionBar?.hide()
                viewModel.getLocalWeather( city )
                setTodayWeather()
                setHours()
                setTenDaysData()
            }

        }?: run {
            if (networkInfo == null) {
                viewModel.getFromSharedPref(sharedPrefObj())
                setTodayWeather()
                setHours()
                setTenDaysData()

            } else {

                if (CheckLocationPermission()) {
                    if (checkLocationEnabled()) {
                        viewModel.getFreshLocation(getFusedLocationProviderClient(requireContext()))
                        setTodayWeather()
                        setHours()
                        setTenDaysData()
//                        binding.swipeRefreshLayout.setOnClickListener {
//                            viewModel.getFreshLocation(getFusedLocationProviderClient(requireContext()))
//                            setTodayWeather()
//                            setHours()
//                            setTenDaysData()
//                        }
                    } else {
                        enableLocation()
                    }
                } else {
                    requestPermissions(
                        arrayOf(
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                        ), 505
                    )
                }
            }
        }


    }

    //check the location permission and location enabled
    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()


    }


    private fun sharedPrefObj(): MutableMap<String, SharedPreferences> {
        val sharedPrefMap: MutableMap<String, SharedPreferences> = mutableMapOf()
        sharedPrefMap["settings"] = requireActivity().getSharedPreferences("settings", MODE_PRIVATE)
        sharedPrefMap["weatherResponse"] =
            requireActivity().getSharedPreferences("weatherResponse", MODE_PRIVATE)
        sharedPrefMap["hourlyWeatherResponse"] =
            requireActivity().getSharedPreferences("hourlyWeatherResponse", MODE_PRIVATE)
        sharedPrefMap["weeklyWeatherResponse"] =
            requireActivity().getSharedPreferences("weeklyWeatherResponse", MODE_PRIVATE)
        return sharedPrefMap
    }

    private fun setTenDaysData() {
        lifecycleScope.launch {
            viewModel.weeklyWeather.collect { response ->
                when (response) {
                    is ApiResponse.Loading -> {
                        Log.i("WeeklyWeather", "loading: Loading")
                    }

                    is ApiResponse.Success -> {
                        Log.i("hourlyWeather", "sucess: ${response.data}")
                        adapter = TodaysWeatherAdapter(response.data, 10)
                        binding.recylerview2.adapter = adapter

                    }

                    is ApiResponse.Error -> {
                        Log.i("WeeklyWeather", "error: ${response.message}")

                    }
                }
            }
        }
    }

    private fun setHours() {
        lifecycleScope.launch {
            viewModel.hourlyWeather.collect { response ->
                when (response) {
                    is ApiResponse.Loading -> {
                        Log.i("hourlyWeather", "loading: Loading")
                    }

                    is ApiResponse.Success -> {
                        Log.i("hourlyyy", "sucess: ${response.data}")
                        adapter = TodaysWeatherAdapter(response.data, 24)
                        binding.recylerview.adapter = adapter

                    }

                    is ApiResponse.Error -> {
                        Log.i("TodaysWeather", "error: ${response.message}")

                    }


                }
            }
        }
    }

    private fun setTodayWeather() {
        lifecycleScope.launch {
            viewModel.weather.collect { response ->
                when (response) {
                    is ApiResponse.Loading -> {
                        Log.i("TodaysWeather", "loading: Loading")
                    }

                    is ApiResponse.Success -> {
                        setInfo(response)
                        setWeatherIcon(response.data["icon"]!!)
                    }

                    is ApiResponse.Error -> {
                        Log.i("TodaysWeather", "error: ${response.message}")

                    }

                }
            }
        }
    }

    private fun setInfo(response: ApiResponse.Success<MutableMap<String, String>>) {
        binding.pressureTV.text = response.data.get("pressure")
        binding.sunriseTV.text = response.data.get("sunrise")
        binding.sunsetTV.text = response.data.get("sunset")
        binding.humidityTV.text = response.data.get("humidity")
        binding.windTV.text = response.data.get("wind_speed")
        binding.visiviltyTV.text = response.data.get("visibility")
        binding.tempTV.text = response.data.get("temp")
        binding.feelsLikeTV.text = ContextCompat.getString(
            requireContext(),
            R.string.feels_like
        ) + " " + response.data.get("feels_like")
        binding.descriptionTV.text = response.data.get("description")
        binding.dayInfoTV.text = response.data.get("dayInfo")
        binding.dayInfoTV.text = response.data["dayInfo"]
        binding.citynameTV.text = response.data["city"]
    }

    fun setWeatherIcon( icon: String) {
        when (icon) {
           // "01d","01n" -> binding.weathericon.setImageResource(R.drawable.ic_01d)
//            "01n" -> binding.weathericon.setImageResource(R.drawable.ic_01n)
//            "02d" -> binding.weathericon.setImageResource(R.drawable.ic_02d)
//            "02n" -> binding.weathericon.setImageResource(R.drawable.ic_02n)
//            "03d","03" -> binding.weathericon.setImageResource(R.drawable.ic_03d)
//            "04d","04n" -> binding.weathericon.setImageResource(R.drawable.ic_04d)
//            "09d","09n" -> binding.weathericon.setImageResource(R.drawable.ic_09d)
//            "10d" -> binding.weathericon.setImageResource(R.drawable.ic_10d)
//            "10n" -> binding.weathericon.setImageResource(R.drawable.ic_10n)
//            "11d","11n" -> binding.weathericon.setImageResource(R.drawable.ic_11d)
//            "13d" , "13n"-> binding.weathericon.setImageResource(R.drawable.ic_13d)
//            "50n","50d" -> binding.weathericon.setImageResource(R.drawable.ic_50n)
        }

    }

    fun CheckLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            return true
        } else if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false

    }

    fun checkLocationEnabled(): Boolean {
        val locationManager =
            requireContext().getSystemService(android.content.Context.LOCATION_SERVICE) as android.location.LocationManager
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
    }

    @SuppressLint("SuspiciousIndentation")
    fun enableLocation() {
        Toast.makeText(
            context,
            getString(R.string.enable_the_location_to_use_the_app), Toast.LENGTH_LONG
        ).show()
        val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

}
