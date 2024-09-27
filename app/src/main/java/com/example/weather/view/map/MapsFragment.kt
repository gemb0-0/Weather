package com.example.weather.view.map

import android.location.Address
import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.Nullable
import com.example.weather.R
import com.example.weather.databinding.FragmentMapsBinding

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {
    lateinit var binding: FragmentMapsBinding
    private var googleMap: GoogleMap? = null
     var mylatLng: LatLng? = null
    private val callback = OnMapReadyCallback { map ->
        googleMap = map
       val location = LatLng(30.033333, 31.233334)
       // googleMap?.uiSettings?.isZoomControlsEnabled = false //might cause a problem
        map.uiSettings.isZoomGesturesEnabled = true

        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(location))
        googleMap?.animateCamera(CameraUpdateFactory.zoomTo(8f), 2000, null)
        googleMap?.setOnMapClickListener { latLng ->
            googleMap?.clear()
            googleMap?.addMarker(MarkerOptions().position(latLng).draggable(true))
            mylatLng = latLng
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        val searchForPlace = binding.searchView
        searchByName(searchForPlace)
        binding.floatingActionButton.setOnClickListener {
           if (mylatLng == null) {
             Toast.makeText(requireContext(),
                 getString(R.string.please_select_a_location), Toast.LENGTH_SHORT).show()
           }
            else {


               val bundle = Bundle()
               parentFragmentManager.setFragmentResult("requestKey", bundle)
               parentFragmentManager.popBackStack()
           }
        }
    }

    private fun searchByName(searchForPlace: SearchView) {
        searchForPlace.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null && googleMap != null) {
                    val geocoder = Geocoder(requireContext())
                    val addressList: List<Address>? = geocoder.getFromLocationName(query, 11)
                    if (!addressList.isNullOrEmpty()) {
                        val address = addressList[0]
                        val latLng = LatLng(address.latitude, address.longitude)
                        mylatLng = latLng
                        val markerOptions =
                            MarkerOptions().position(latLng).title(address.getAddressLine(0))
                        googleMap?.addMarker(markerOptions)
                        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                        googleMap?.animateCamera(CameraUpdateFactory.zoomTo(10f), 2000, null)
                    }
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {

    //                if (query != null && googleMap != null) {
    //                    val geocoder = Geocoder(requireContext())
    //                    val addressList: MutableList<Address>? = geocoder.getFromLocationName(query, 15)
    //                    Log.i(TAG, "onQueryTextChange: $addressList")
    //                    if (!addressList.isNullOrEmpty()) {
    //                        binding.recyler.visibility = View.VISIBLE
    //                        val myLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    //                        binding.recyler.layoutManager = myLayoutManager
    //                        binding.recyler.adapter = mapsAdapter(addressList)
    //                    }
    //                }

                return true
            }
        }

        )
    }
}


/*     //  search
// Places.initialize( requireContext() , BuildConfig.GOOGLE_MAPS_API_KEY)
//        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
//        // Specify the types of place data to return.
//        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))
//        // Set up a PlaceSelectionListener to handle the response.
//        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
//            override fun onPlaceSelected(place: Place) {
//                Log.i("ggggggggggggggggggggggggggg", "Place: ${place.name}, ${place.id}")
//            }
//            override fun onError(status: Status) {
//                Log.i(TAG, "An error occurred: $status")
//            }
//        })

 */














