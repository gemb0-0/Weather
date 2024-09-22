package com.example.weather.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.R
import com.example.weather.databinding.FragmentTodaysWeatherBinding
import com.example.weather.model.IRepository
import com.example.weather.model.Repository
import com.example.weather.viewmodel.TodaysWeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient

class TodaysWeather : Fragment() {
    lateinit var binding: FragmentTodaysWeatherBinding
    lateinit var viewModel: TodaysWeatherViewModel
     lateinit var fusedLocationProviderClient: FusedLocationProviderClient
     lateinit var adapter: HourlyWeatherAdapter


    //check the location permission and location enabled
    override fun onStart() {
        super.onStart()
        if (CheckLocationPermission()) {
            if (checkLocationEnabled()) {
                //  getFreshLocation()
                fusedLocationProviderClient =
                    com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(
                        requireContext()
                    )
                val repo:IRepository=Repository()
                val factory = TodaysWeatherViewModel.TodaysWeatherViewModelFactory(fusedLocationProviderClient,repo)
                viewModel = ViewModelProvider(this, factory).get(TodaysWeatherViewModel::class.java)
                viewModel.getFreshLocation()
                val myLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                binding.recylerview.layoutManager = myLayoutManager


                viewModel.weather.observe(viewLifecycleOwner) {

                }
                viewModel.hourlyWeather.observe(viewLifecycleOwner) {
                    adapter = HourlyWeatherAdapter(it)
                    binding.recylerview.adapter = adapter
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
//        viewModel = ViewModelProvider(this).get(TodaysWeatherViewModel::class.java)
//        val factory = TodaysWeatherViewModelFactory(fusedLocationProviderClient)
//        viewModel = ViewModelProvider(this, factory).get(TodaysWeatherViewModel::class.java)
//        viewModel.getFreshLocation()





        //viewModel.getweather()
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