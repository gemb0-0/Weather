package com.example.weather.view.map

import android.content.ContentValues.TAG
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weather.BuildConfig
import com.example.weather.R
import com.google.android.gms.common.api.Status

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener

class MapsFragment : Fragment() {

    private val callback = OnMapReadyCallback { googleMap ->

        val sydney = LatLng(30.033333, 31.233334)
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        //googleMap.animateCamera(CameraUpdateFactory.zoomTo(18f), 2000, null)
       // googleMap.addMarker(MarkerOptions().position(sydney).draggable(true))

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        //add an alert
        googleMap.setOnMapClickListener {
            googleMap.clear()
           // googleMap.addMarker(MarkerOptions().position(it).draggable(true))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        //
////      //  search
        Places.initialize( requireContext() , BuildConfig.GOOGLE_MAPS_API_KEY)

        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i("ggggggggggggggggggggggggggg", "Place: ${place.name}, ${place.id}")
            }

            override fun onError(status: Status) {

                Log.i(TAG, "An error occurred: $status")
            }
        })

    }




}