package com.example.weather.view.mainActivity

import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.weather.R
import com.example.weather.Utils.NetworkChangeReceiver
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.model.IRepository
import com.example.weather.model.Repository
import com.example.weather.model.localDataSource.Sharedpref
import com.example.weather.model.remoteDataSource.RemoteDataSource
import kotlinx.coroutines.launch
import java.util.Locale


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainActivityViewModel
    private lateinit var networkChangeReceiver: NetworkChangeReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)


        val intentFilter: IntentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        networkChangeReceiver = NetworkChangeReceiver(binding.root)
        registerReceiver(networkChangeReceiver, intentFilter)
        val actionBar: ActionBar? = supportActionBar
        actionBar!!.setHomeAsUpIndicator(R.drawable.baseline_menu_24)
        actionBar.setDisplayHomeAsUpEnabled(true)

        val navController = findNavController(R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.navigationView, navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->

            when (destination.id) {
                R.id.mapsFragment -> {
                    actionBar.hide()
                }

                else -> {
                    actionBar.show()
                }
            }
        }

        val sharedpref: SharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val repo: IRepository = Repository(
            RemoteDataSource.getInstance(),
            Sharedpref()
        )
        val factory = MainActivityViewModel.MainActivityViewModelFactory(
            repo, sharedpref
        )
        viewModel = ViewModelProvider(this, factory).get(MainActivityViewModel::class.java)
        viewModel.getSettings()
        Log.i("MainActivity", "onCreate: ${Locale.getDefault().language}")
        changeLocaleWhenStartingUp()

    }

    private fun changeLocaleWhenStartingUp() {
        lifecycleScope.launch {
            viewModel.Settings.collect() {
                when (it[0]) {
                    "en" -> {
                        Locale.setDefault(Locale("en"))
                        val config = resources.configuration
                        config.setLocale(Locale("en"))
                        resources.updateConfiguration(config, resources.displayMetrics)

                    }

                    "ar" -> {
                        Locale.setDefault(Locale("ar"))
                        val config = resources.configuration
                        config.setLocale(Locale("ar"))
                        resources.updateConfiguration(config, resources.displayMetrics)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkChangeReceiver)
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
