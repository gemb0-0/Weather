package com.example.weather.Utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weather.R
import com.example.weather.model.IRepository
import com.example.weather.model.Repository

class AlertWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    val context: Context = appContext
    lateinit var message : String
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var repository: IRepository
    override suspend fun doWork(): Result {
        return try {
            sharedPreferences = context.getSharedPreferences("notifications", MODE_PRIVATE)


            val id = inputData.getString("id")
            Log.i("deleteAfterWork", "doWork: id $id")
            message = inputData.getString("message").toString()
            val type = inputData.getString("type").toString()
            sendNotification(message,type)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun sendNotification(message : String,type : String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "55"

        val channel =
            NotificationChannel(channelId, "6", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)


            if(type == "notification") {
                val notification = NotificationCompat.Builder(context, channelId)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText("weather alert")
                    .setSmallIcon(R.drawable.ic_01d)
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_MAX)
                    .build()
                notificationManager.notify(1, notification)
            } else {
                val intent = Intent(applicationContext, AlarmService::class.java)
                intent.putExtra("mmmmm",message)
                context.startService(intent)
            }

    }
}