package com.example.weather.view.alert

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import com.example.weather.R
import com.example.weather.databinding.FragmentAlertBinding
import java.util.Calendar

class AlertFragment : Fragment() {
    lateinit var binding: FragmentAlertBinding
    private lateinit var dateTextView: TextView
    private lateinit var timeTextView: TextView
    private var selectedCalendar: Calendar = Calendar.getInstance()

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
        binding.floatingActionButton.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {
        val dialogView = layoutInflater.inflate(R.layout.alarm, null)

        dateTextView = dialogView.findViewById(R.id.dateTV)
        timeTextView = dialogView.findViewById(R.id.TimeTv)
        val alram: RadioButton = dialogView.findViewById(R.id.radio_alarm)
        val notification: RadioButton = dialogView.findViewById(R.id.radio_notifications)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.addalaram))
            .setView(dialogView)
            .setPositiveButton("Set") { dialog, which ->
               if (alram.isChecked) {
               }
                else if (notification.isChecked) {

                }

            }
            .setNegativeButton("Cancel") { dialog, which ->
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

        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000 // Set minimum date to today

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
}
