package com.example.weather.view

import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weather.databinding.FragmentSettingsBinding
import com.example.weather.model.IRepository
import com.example.weather.model.Repository
import com.example.weather.model.Utils
import com.example.weather.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
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
        val repo: IRepository = Repository()
        val factory = SettingsViewModel.SettingsViewModelFactory(repo, sharedpref)
        viewModel = ViewModelProvider(this, factory).get(SettingsViewModel::class.java)

        viewModel.getSettings()
        showSettings()
        binding.buttonUpdateSettings.setOnClickListener {
         val updated:MutableList<String> =   updateSettings()
            viewModel.updateSettings(updated)

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
                        binding.radioButtonMetersPerHour.isChecked = true
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

    private fun updateSettings() : MutableList<String> {
        val updated:MutableList<String> = mutableListOf()
        val selectedLanguageId = binding.radioGroupLanguage.checkedRadioButtonId
        when (selectedLanguageId) {
            binding.radioButtonEnglish.id -> {
               updated.add("en")
                val locale = Locale("en")
                Locale.setDefault(locale)

                val config = Configuration(context?.resources?.configuration ?: Configuration())
                config.setLocale(locale)

                context?.resources?.updateConfiguration(config, requireContext().resources.displayMetrics)
                activity?.recreate()
            }

            binding.radioButtonArabic.id -> {
                updated.add("ar")
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
                updated.add("°C")
            }

            binding.radioButtonFahrenheit.id -> {
                updated.add("°F")
            }

            binding.radioButtonKelvin.id -> {
                updated.add("°K")
            }
        }

        val selectedWindId = binding.radioGroupWindSpeed.checkedRadioButtonId
        when (selectedWindId) {
            binding.radioButtonMetersPerHour.id -> {
                updated.add("km/h")
            }

            binding.radioButtonMilesPerHour.id -> {
                updated.add("miles/h")
            }
        }
        val selectedLocationId = binding.radioGroupLocation.checkedRadioButtonId
        when (selectedLocationId) {
            binding.radioButtonGps.id -> {
                updated.add("gps")
            }

            binding.radioButtonMap.id -> {
                updated.add("location")
            }
        }
        val selectedNotificationId = binding.radioGroupNotifications.checkedRadioButtonId
        when (selectedNotificationId) {
            binding.radioButtonEnableNotifications.id -> {
                updated.add("enable")
            }

            binding.radioButtonDisableNotifications.id -> {
                updated.add("disable")
            }
        }
        return updated
    }

    }
