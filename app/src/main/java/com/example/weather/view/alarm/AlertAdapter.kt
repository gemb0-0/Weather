package com.example.weather.view.alarm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R

class AlertAdapter: RecyclerView.Adapter<AlertAdapter.AlertViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_favourites, parent, false)
        return AlertViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return 5 ////////////////////////
    }

    inner class AlertViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind() {
            itemView.setOnClickListener {
                Toast.makeText(itemView.context, "Item clicked", Toast.LENGTH_SHORT).show()
            }
        }
    }
}