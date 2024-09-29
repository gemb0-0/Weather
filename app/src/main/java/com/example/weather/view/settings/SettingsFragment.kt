package com.example.weather.view.settings

import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.weather.R
import com.example.weather.databinding.FragmentSettingsBinding
import com.example.weather.model.IRepository
import com.example.weather.model.Repository
import com.example.weather.model.localDataSource.Sharedpref
import com.example.weather.model.remoteDataSource.RemoteDataSource
import kotlinx.coroutines.launch
import java.util.Locale


class SettingsFragment : Fragment() {
lateinit var binding: FragmentSettingsBinding
lateinit var sharedpref: SharedPreferences
    lateinit var viewModel: SettingsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedpref = requireActivity().getSharedPreferences("settings", MODE_PRIVATE)
        val repo: IRepository = Repository(
            RemoteDataSource.getInstance(),
            Sharedpref()
        )
        val factory = SettingsViewModel.SettingsViewModelFactory(repo, sharedpref)
        viewModel = ViewModelProvider(this, factory).get(SettingsViewModel::class.java)

        viewModel.getSettings()
        showSettings()
        binding.buttonUpdateSettings.setOnClickListener {
            viewModel.updateSettings(updateSettings())
         if (binding.radioButtonMap.isChecked) {
                val bundle = Bundle()
                bundle.putString("location", "gps")
                view.findNavController().navigate(R.id.mapsFragment, bundle)

            }

        }
    }

    private fun showSettings() {
        lifecycleScope.launch {
            viewModel.Settings.collect() {
                Log.i("SettingsFragment", "onViewCreated: $it")
                when (it[0]) {
                    "en" -> {
                        binding.radioButtonEnglish.isChecked = true
                    }

                    "ar" -> {
                        binding.radioButtonArabic.isChecked = true
                    }
                }
                when (it[1]) {
                    "°C" -> {
                        binding.radioButtonCelsius.isChecked = true
                    }

                    "°F" -> {
                        binding.radioButtonFahrenheit.isChecked = true
                    }

                    "°K" -> {
                        binding.radioButtonKelvin.isChecked = true
                    }
                }
                when (it[2]) {
                    "km/h" -> {
                        binding.radioButtonKilometersPerHour.isChecked = true
                    }

                    "miles/h" -> {
                        binding.radioButtonMilesPerHour.isChecked = true
                    }
                }
                when (it[3]) {
                    "gps" -> {
                        binding.radioButtonGps.isChecked = true
                    }

                    "location" -> {
                        binding.radioButtonMap.isChecked = true
                    }
                }
                when (it[4]) {
                    "enable" -> {
                        binding.radioButtonEnableNotifications.isChecked = true
                    }

                    "disable" -> {
                        binding.radioButtonDisableNotifications.isChecked = true
                    }
                }
            }

        }
    }

    private fun updateSettings() : MutableMap<String,String> {
        val updated:MutableMap<String,String> = mutableMapOf()
        val selectedLanguageId = binding.radioGroupLanguage.checkedRadioButtonId
        when (selectedLanguageId) {
            binding.radioButtonEnglish.id -> {
                updated["language"] = "en"
                val locale = Locale("en")
                Locale.setDefault(locale)

                val config = Configuration(context?.resources?.configuration ?: Configuration())
                config.setLocale(locale)

                context?.resources?.updateConfiguration(config, requireContext().resources.displayMetrics)
                activity?.recreate()
            }

            binding.radioButtonArabic.id -> {
                updated["language"] = "ar"
                val locale = Locale("ar")
                Locale.setDefault(locale)


                val config = Configuration(context?.resources?.configuration ?: Configuration())
                config.setLocale(locale)

                context?.resources?.updateConfiguration(config, requireContext().resources.displayMetrics)

                activity?.recreate()
            }
        }
        val selectedTempId = binding.radioGroupTemperature.checkedRadioButtonId
        when (selectedTempId) {
            binding.radioButtonCelsius.id -> {
                updated["temp"] = "°C"
            }

            binding.radioButtonFahrenheit.id -> {
                updated["temp"] = "°F"
            }

            binding.radioButtonKelvin.id -> {
                updated["temp"] = "°K"
            }
        }

        val selectedWindId = binding.radioGroupWindSpeed.checkedRadioButtonId
        when (selectedWindId) {
            binding.radioButtonKilometersPerHour.id -> {
                updated["wind"] = "km/h"
            }

            binding.radioButtonMilesPerHour.id -> {
                updated["wind"] = "miles/h"
            }
        }
        val selectedLocationId = binding.radioGroupLocation.checkedRadioButtonId
        when (selectedLocationId) {
            binding.radioButtonGps.id -> {
                updated["location"] = "gps"
            }

            binding.radioButtonMap.id -> {
                updated["location"] = "location"
            }
        }
        val selectedNotificationId = binding.radioGroupNotifications.checkedRadioButtonId
        when (selectedNotificationId) {
            binding.radioButtonEnableNotifications.id -> {
                updated["notification"] = "enable"
            }

            binding.radioButtonDisableNotifications.id -> {
                updated["notification"] = "disable"
            }
        }
        return updated
    }

    }
