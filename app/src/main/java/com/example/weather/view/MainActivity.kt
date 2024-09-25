package com.example.weather.view

import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.weather.R
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.model.IRepository
import com.example.weather.model.Repository
import com.example.weather.viewmodel.MainActivityViewModel
import com.example.weather.viewmodel.TodaysWeatherViewModel

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val actionBar: ActionBar? = supportActionBar
        actionBar!!.setHomeAsUpIndicator(R.drawable.baseline_menu_24)
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)
        val navController = findNavController(R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.navigationView, navController)

       val sharedpref :SharedPreferences= getSharedPreferences("settings", MODE_PRIVATE)
        val repo: IRepository = Repository()
        val factory = MainActivityViewModel.MainActivityViewModelFactory(
            repo,sharedpref
        )
        viewModel = ViewModelProvider(this, factory).get(MainActivityViewModel::class.java)
        viewModel.getSettings()


//
//        Geocoder(this@MainActivity).getFromLocation(
//            locationResult.lastLocation?.latitude!!,
//            locationResult.lastLocation?.longitude!!,
//            1,
//            Geocoder.GeocodeListener {
//                locDetails.text = it[0].locality+", "+it[0].countryName
//            })




////      //  search
//        Places.initialize(applicationContext, getString(R.string.YOUR_API_KEY))
//
//        val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
//
//        // Specify the types of place data to return.
//        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))
//
//        // Set up a PlaceSelectionListener to handle the response.
//        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
//            override fun onPlaceSelected(place: Place) {
//                Log.i("ggggggggggggggggggggggggggg", "Place: ${place.name}, ${place.id}")
//            }
//
//            override fun onError(status: Status) {
//
//                Log.i(TAG, "An error occurred: $status")
//            }
//        })


    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                binding.drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }

}