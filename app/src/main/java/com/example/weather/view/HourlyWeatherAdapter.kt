package com.example.weather.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.R

class HourlyWeatherAdapter(var weatherData : List<Triple<String,String,String>>) : RecyclerView.Adapter<HourlyWeatherAdapter.HourlyWeatherViewHolder>() {


    class HourlyWeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val time: TextView = itemView.findViewById(R.id.time_TV)
        val temp: TextView = itemView.findViewById(R.id.temp_TV)
        val icon: ImageView = itemView.findViewById(R.id.weather_IV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyWeatherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_hours, parent, false)
        return HourlyWeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourlyWeatherViewHolder, position: Int) {
    holder.time.text= weatherData[position].first
        holder.temp.text= weatherData[position].second
        Glide.with(holder.itemView.context).load("https://openweathermap.org/img/wn/${weatherData[position].third}.png").into(holder.icon)
    }

    override fun getItemCount(): Int {
        return 24
    }


}