package com.example.weather.Utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.view.View
import androidx.core.content.ContextCompat
import com.example.weather.R
import com.google.android.material.snackbar.Snackbar

class NetworkChangeReceiver(private val view:View) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        var flag :Boolean = false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

        if (isConnected&&flag) {
            val bar = Snackbar.make(view, R.string.connection, Snackbar.LENGTH_LONG)
            bar.setTextColor(ContextCompat.getColor(context, R.color.green))
            bar.show()

        } else {
            val bar = Snackbar.make(view, R.string.no_connection, Snackbar.LENGTH_LONG)
          //  bar.setBackgroundTint(ContextCompat.getColor(context, R.color.white))
            bar.setTextColor(ContextCompat.getColor(context, R.color.redd))
            bar.show()
            flag = true
        }
    }
}