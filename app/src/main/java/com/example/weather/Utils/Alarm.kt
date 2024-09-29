package com.example.weather.Utils


import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.weather.R
import com.example.weather.databinding.AlramwindowBinding

class AlarmWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val alarmTime = inputData.getLong("ALARM_TIME", 0L)

        val description = "Weather Alert!"
        val temp = "25°C"

        showOverlay(description, temp)

        return Result.success()
    }

    private fun showOverlay(description: String, temp: String) {
        val overlayManager = OverlayManager(applicationContext)
        overlayManager.showOverlay(description, temp)
    }
}




class OverlayManager(private val context: Context) {

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private lateinit var binding: AlramwindowBinding

    init {
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    fun showOverlay(description: String, temp: String) {
        if (overlayView != null) return // Prevent multiple overlays

        val inflater = LayoutInflater.from(context)
        overlayView = inflater.inflate(R.layout.alarm, null)
        binding = AlramwindowBinding.bind(overlayView!!)


        // Set the weather description
        binding.title.text = description
        binding.message.text = temp

        // Set up the layout parameters for the overlay
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }
        windowManager?.addView(overlayView, params)

        binding.dismissButton.setOnClickListener {
            dismissOverlay()
        }
    }

    fun dismissOverlay() {
        overlayView?.let {
            windowManager?.removeView(it)
            overlayView = null
        }
    }

}
