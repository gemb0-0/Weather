package com.example.weather.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.R
import com.example.weather.databinding.FragmentTodaysWeatherBinding
import com.example.weather.model.remoteDataSource.ApiResponse
import com.example.weather.model.Repository
import com.example.weather.model.IRepository
import com.example.weather.model.Utils
import com.example.weather.viewmodel.TodaysWeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class TodaysWeather : Fragment() {
    lateinit var binding: FragmentTodaysWeatherBinding
    lateinit var viewModel: TodaysWeatherViewModel
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var adapter: TodaysWeatherAdapter


    //check the location permission and location enabled
    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()
        if (CheckLocationPermission()) {
            if (checkLocationEnabled()) {
                //  getFreshLocation()
                viewModel.getFreshLocation()
                val myLayoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                binding.recylerview.layoutManager = myLayoutManager
                binding.recylerview2.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)




                lifecycleScope.launch {
                    viewModel.weather.collect { response ->
                        when (response) {
                            is ApiResponse.Loading -> {
                                Log.i("TodaysWeather", "loading: Loading")
                            }

                            is ApiResponse.Success -> {

                                Log.i("TodaysWeatherrr", "sucess: ${response.data}")
                                binding.humidityTV.text =
                                    response.data.main!!.humidity.toString() + "%"
                                binding.windTV.text =
                                    response.data.wind!!.speed.toString()
                                binding.visiviltyTV.text = response.data.visibility.toString() + "m"
                                binding.tempTV.text =
                                    response.data.main.temp.toInt()
                                        .toString() // + Utils.temp       // "°C"
                                //binding.highLowTV.text = response.data.main.temp_max.toInt().toString() + "°C" + " / " + response.data.main.temp_min.toInt().toString() + "°C"
                                binding.highLowTV.text =
                                    getString(R.string.feels_like) + response.data.main.feels_like.toInt()
                                        .toString() //+  Utils.temp  //"°C"

                                binding.descriptionTV.text = response.data.weather!![0].description

                                // Assuming this is within a method or a coroutine scope
                                val date = Date(response.data.dt!! * 1000) // Convert seconds to milliseconds

// Create a SimpleDateFormat for month and year in English
                                val monthYearFormat = SimpleDateFormat(" yyyy", Locale.ENGLISH)
// Create a SimpleDateFormat for the day in the default locale
                                val dayFormat = SimpleDateFormat("MMMM", Locale.getDefault())

// Format the date components
                                val monthYear = monthYearFormat.format(date)
                                val day = dayFormat.format(date)

// Combine the results and set to TextView
                                binding.dayInfoTV.text = getString(R.string.today) + " " + day + " " + monthYear
                                  binding.pressureTV.text = response.data.main.pressure.toString()+"hPa"

                                binding.sunriseTV.text =  convertUnixToTime(response.data.sys!!.sunrise!!.toLong(), response.data.timezone!!)
                                binding.sunsetTV.text = convertUnixToTime(response.data.sys.sunset!!.toLong(), response.data.timezone!!)

                            }

                            is ApiResponse.Error -> {
                                Log.i("TodaysWeather", "error: ${response.message}")

                            }
                        }
                    }
                }

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

                lifecycleScope.launch {
                    viewModel.weeklyWeather.collect { response ->
                        when (response) {
                            is ApiResponse.Loading -> {
                                Log.i("WeeklyWeather", "loading: Loading")
                            }

                            is ApiResponse.Success -> {
                                Log.i("WeeklyWeather", "sucess: ${response.data}")
                                adapter = TodaysWeatherAdapter(response.data, 10)
                                binding.recylerview2.adapter = adapter
                            }

                            is ApiResponse.Error -> {
                                Log.i("WeeklyWeather", "error: ${response.message}")

                            }
                        }
                    }

//                }

                }
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTodaysWeatherBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationProviderClient =
            com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(
                requireContext()
            )

        val repo: IRepository = Repository()
        val factory = TodaysWeatherViewModel.TodaysWeatherViewModelFactory(
            fusedLocationProviderClient,
            repo
        )
        viewModel = ViewModelProvider(this, factory).get(TodaysWeatherViewModel::class.java)


    }

    fun convertUnixToTime(unixTime: Long, timezoneOffset: Int): String {
        // Create a date object from the Unix timestamp
        val date = Date(unixTime * 1000) // Convert seconds to milliseconds
        // Create a SimpleDateFormat to format the time
        val sdf = SimpleDateFormat("HH:mm", Locale.ENGLISH)

        // Set the timezone based on the offset from UTC
        sdf.timeZone = TimeZone.getTimeZone("GMT+${timezoneOffset / 3600}")
        return sdf.format(date)
    }



    fun CheckLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            return true
            //change the settings of the app
        } else if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            return true
            //change the settings of the app
        }
        return false

    }

    fun checkLocationEnabled(): Boolean {
        val locationManager =
            //  getSystemService(android.content.Context.LOCATION_SERVICE) as android.location.LocationManager
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