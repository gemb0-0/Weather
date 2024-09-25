package com.example.weather.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class TodaysWeatherAdapter(var weatherData : List<Triple<String,String,String>>, val numOfCards:Int) : RecyclerView.Adapter<TodaysWeatherAdapter.HourlyWeatherViewHolder>() {

    class HourlyWeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val time: TextView = itemView.findViewById(R.id.time_TV)
        val temp: TextView = itemView.findViewById(R.id.temp_TV)
        val icon: ImageView = itemView.findViewById(R.id.weather_IV)
        val weekDay: TextView ?= itemView.findViewById(R.id.day_TV)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyWeatherViewHolder {
        if(numOfCards==24){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_hours, parent, false)
            return HourlyWeatherViewHolder(view)
        }
        else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.row_week, parent, false)
            return HourlyWeatherViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: HourlyWeatherViewHolder, position: Int) {
        holder.time.text= weatherData[position].first
        holder.temp.text= weatherData[position].second
        Glide.with(holder.itemView.context).load("https://openweathermap.org/img/wn/${weatherData[position].third}.png").into(holder.icon)

        if (numOfCards==10) {

                if (position == 0) {
                    holder.weekDay!!.text = getString(holder.itemView.context, R.string.tomorrow)
                } else {
                    val currentLocale = Locale.getDefault()
                    val formatter = DateTimeFormatter.ofPattern("EEEE", currentLocale)
                    val today = LocalDate.now().plusDays(position.toLong() + 1).format(formatter)
                    val formattedDay = today.replaceFirstChar { it.uppercase() }

                    holder.weekDay!!.text = formattedDay

                }
        }
    }

    override fun getItemCount(): Int {
        return numOfCards
    }


}