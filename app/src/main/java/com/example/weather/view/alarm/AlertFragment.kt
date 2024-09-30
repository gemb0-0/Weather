package com.example.weather.view.alarm

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.weather.R
import com.example.weather.Utils.AlertWorker
import com.example.weather.databinding.FragmentAlertBinding
import com.example.weather.model.Repository
import com.example.weather.model.localDataSource.Sharedpref
import com.example.weather.model.remoteDataSource.RemoteDataSource
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class AlertFragment : Fragment() {
    lateinit var binding: FragmentAlertBinding
    private lateinit var dateTextView: TextView
    private lateinit var timeTextView: TextView
    private var selectedCalendar: Calendar = Calendar.getInstance()
    lateinit var viewModel: AlertViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedpref = requireActivity().getSharedPreferences("notifications", MODE_PRIVATE)
        viewModel = AlertViewModel.AlertViewModelFactory(Repository(RemoteDataSource.getInstance(),
            Sharedpref()
        ), sharedpref).create(AlertViewModel::class.java)
        binding.floatingActionButton.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {
        val dialogView = layoutInflater.inflate(R.layout.alarm, null)

        dateTextView = dialogView.findViewById(R.id.dateTV)
        timeTextView = dialogView.findViewById(R.id.TimeTv)
        val alarm: RadioButton = dialogView.findViewById(R.id.radio_alarm)
        val notification: RadioButton = dialogView.findViewById(R.id.radio_notifications)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.addalaram))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.set)) { dialog, which ->

                if (alarm.isChecked && System.currentTimeMillis() - 1000 < selectedCalendar.timeInMillis) {
                    val formattedDate = selectedCalendar.timeInMillis.toLong()
                    viewModel.initializeAlarm(getFusedLocationProviderClient(requireContext()),selectedCalendar.timeInMillis)


                  scheduleNotification(requireContext(), formattedDate, Alert("55", getString(R.string.alarm) ,"alarm", formattedDate))

                }
                else if (notification.isChecked&&System.currentTimeMillis() - 1000 < selectedCalendar.timeInMillis&&selectedCalendar.timeInMillis<System.currentTimeMillis()+900000) {
                    viewModel.initializeAlarm(getFusedLocationProviderClient(requireContext()),selectedCalendar.timeInMillis)

                    scheduleNotification(requireContext(), unixToAPicConversion(), Alert("55", getString(R.string.notification), "notification", unixToAPicConversion()))

                }

            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }
            .create()

        updateDateTimeViews()

        dateTextView.setOnClickListener {
            showDatePicker()
        }

        timeTextView.setOnClickListener {
            showTimePicker()
        }


        dialog.setOnShowListener {
            val titleView = dialog.findViewById<TextView>(android.R.id.title)
            titleView?.setTextColor(resources.getColor(android.R.color.white))

            dialog.window?.setBackgroundDrawableResource(R.drawable.alram_style)

            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setTextColor(resources.getColor(android.R.color.black))

            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            negativeButton.setTextColor(resources.getColor(android.R.color.black))
        }

        dialog.show()
    }

    private fun unixToAPicConversion(): Long {
        selectedCalendar.set(Calendar.MINUTE, 0)
        selectedCalendar.set(Calendar.SECOND, 0)
        selectedCalendar.set(Calendar.MILLISECOND, 0)
        val unixTime = selectedCalendar.timeInMillis / 1000
        val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val formattedDate = utcFormat.format(selectedCalendar.time)

        Log.i("Alarm", "Alarm is set at = Unix: $unixTime, UTC: $formattedDate")
        Log.i("Alarm", "Alarm is set at = ${selectedCalendar.timeInMillis}")
        return unixTime
    }

    private fun showDatePicker() {
        val year = selectedCalendar.get(Calendar.YEAR)
        val month = selectedCalendar.get(Calendar.MONTH)
        val day = selectedCalendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), R.style.datepicker, { _, selectedYear, selectedMonth, selectedDay ->
            selectedCalendar.set(Calendar.YEAR, selectedYear)
            selectedCalendar.set(Calendar.MONTH, selectedMonth)
            selectedCalendar.set(Calendar.DAY_OF_MONTH, selectedDay)
            updateDateTimeViews()
        }, year, month, day)

        val maxCalendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 4)
        }

        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.datePicker.maxDate = maxCalendar.timeInMillis
        datePickerDialog.show()

    }
    private fun showTimePicker() {
        val hour = selectedCalendar.get(Calendar.HOUR_OF_DAY)
        val minute = selectedCalendar.get(Calendar.MINUTE)

        TimePickerDialog(requireContext(), R.style.CustomTimePicker,{ _, selectedHour, selectedMinute ->
            selectedCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
            selectedCalendar.set(Calendar.MINUTE, selectedMinute)
            updateDateTimeViews()
        }, hour, minute, true).show()
    }

    private fun updateDateTimeViews() {
        val dateFormat = android.text.format.DateFormat.getMediumDateFormat(requireContext())
        val amPmFormat = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
        timeTextView.text = amPmFormat.format(selectedCalendar.time)
        dateTextView.text = dateFormat.format(selectedCalendar.time)
    }


    fun scheduleNotification(context: Context, alarmTime: Long, alert: Alert): String {
        val currentTime = System.currentTimeMillis()
        val delay = alarmTime - currentTime

        val data = Data.Builder()
            .putString("id",alert.deleteId)
            .putString("message", alert.message)
            .putString("type", alert.type)
            .build()
        Log.i("deleteAfterWork", "scheduleNotification: id ${alert.deleteId}")
        val notificationWork = OneTimeWorkRequestBuilder<AlertWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueue(notificationWork)
        return notificationWork.id.toString()
    }


}
data class Alert(
    val deleteId: String,
    val message: String,
    val type: String,
    val time: Long,
)