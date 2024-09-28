package com.example.weather.view.favourites

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.databinding.RowFavouritesBinding
import com.google.android.gms.maps.model.LatLng



class FavDiffUtil : DiffUtil.ItemCallback<Pair<String, LatLng>>() {
    override fun areItemsTheSame(oldItem: Pair<String, LatLng>, newItem: Pair<String, LatLng>): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Pair<String, LatLng>, newItem: Pair<String, LatLng>): Boolean {
        return oldItem == newItem
    }
}




class FavouriteAdapter(
    val onFavClick: onFavClick
): ListAdapter <Pair<String, LatLng>, FavouriteAdapter.FavouriteViewHolder>(FavDiffUtil()) {
    lateinit var binding: RowFavouritesBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_favourites,parent,false)
        binding = RowFavouritesBinding.bind(view)
        return FavouriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val item = getItem(position) ?: return
        holder.binding.textView.text = item.first
        holder.binding.root.setOnClickListener {
            onFavClick.onFavClick(item.second)
        }
        holder.binding.deleteFav.setOnClickListener {
            onFavClick.inFavDelete(item.second)
            Log.i("FavouriteAdapter", "onBindViewHolder: ${item.second}")
        }
    }
    class FavouriteViewHolder(var binding: RowFavouritesBinding) : RecyclerView.ViewHolder(binding.root)
}