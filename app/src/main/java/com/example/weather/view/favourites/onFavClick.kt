package com.example.weather.view.favourites

import com.google.android.gms.maps.model.LatLng

interface onFavClick {
    fun onFavClick(city: LatLng)
    fun inFavDelete(city: LatLng)
}