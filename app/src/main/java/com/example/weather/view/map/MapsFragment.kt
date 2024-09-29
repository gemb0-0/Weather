package com.example.weather.view.map

import android.content.Context.MODE_PRIVATE
import android.location.Address
import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import com.example.weather.R
import com.example.weather.databinding.FragmentMapsBinding
import com.example.weather.model.Repository
import com.example.weather.model.localDataSource.Sharedpref
import com.example.weather.model.remoteDataSource.RemoteDataSource

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {
    lateinit var binding: FragmentMapsBinding
    lateinit var viewModel: MapsViewModel
    private var googleMap: GoogleMap? = null
    var favLocation: Pair<String, LatLng>? = null

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

        (activity as AppCompatActivity).supportActionBar?.hide()

        var sharedpref = requireActivity().getSharedPreferences("favourites", MODE_PRIVATE)

        viewModel = ViewModelProvider(this, MapsViewModel.MapsViewModelFactory(Repository(
            RemoteDataSource.getInstance(),
            Sharedpref()
        ), sharedpref)).get(MapsViewModel::class.java)
        searchByName(searchForPlace)


        arguments?.let { bundle ->
            val sender = bundle.getString("location")
            if(sender == "gps"){
                binding.floatingActionButton.setOnClickListener {
                    if (favLocation == null) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.please_select_a_location), Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val mainLocation = requireActivity().getSharedPreferences("settings", MODE_PRIVATE)
                        viewModel.saveMainLocation(favLocation!!, mainLocation)
                        parentFragmentManager.popBackStack()

                    }
                }
            }
        }?: run {
            binding.floatingActionButton.setOnClickListener {
                if (favLocation == null) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.please_select_a_location), Toast.LENGTH_SHORT
                    ).show()
                } else {
                    viewModel.saveLocation(favLocation!!)
                    parentFragmentManager.popBackStack()

                }
            }
        }

    }

    val callback = OnMapReadyCallback { map ->
        googleMap = map
        initalizeMap(map)

        googleMap?.setOnMapClickListener { latLng ->
            handleMap(latLng)

        }
    }

    private fun initalizeMap(map: GoogleMap) {
        val location = LatLng(30.06263, 31.24967)
        // googleMap?.uiSettings?.isZoomControlsEnabled = false //might cause a problem
        map.uiSettings.isZoomGesturesEnabled = true
        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(location))
        googleMap?.animateCamera(CameraUpdateFactory.zoomTo(8f), 2000, null)
    }

    private fun handleMap(latLng: LatLng) {
        googleMap?.clear()
        var address: MutableList<Address>? =
            Geocoder(requireContext()).getFromLocation(latLng.latitude, latLng.longitude, 1)
        googleMap?.addMarker(
            MarkerOptions().position(latLng).draggable(true).title(
                address?.get(0)?.getAddressLine(0)

                    ?: getString(R.string.unknown_location)
            )
        )?.showInfoWindow()
        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap?.animateCamera(CameraUpdateFactory.zoomTo(10f), 2000, null)
        favLocation = Pair(
            address?.get(0)?.countryName + "," + (address?.get(0)?.subAdminArea
                ?: "Unknown location"), latLng
        )
    }

    private fun searchByName(searchForPlace: SearchView) {
        searchForPlace.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null && googleMap != null) {
                    googleMap?.clear()
                    val geocoder = Geocoder(requireContext())
                    val addressList: List<Address>? = geocoder.getFromLocationName(query, 11)
                    if (!addressList.isNullOrEmpty()) {
                        val address = addressList[0]
                        Log.i("address", "onQueryTextSubmit: $address")
                        val latLng = LatLng(address.latitude, address.longitude)
                        val markerOptions =
                            MarkerOptions().position(latLng).title(address.getAddressLine(0))
                        googleMap?.addMarker(markerOptions)?.showInfoWindow()
                        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                        googleMap?.animateCamera(CameraUpdateFactory.zoomTo(10f), 2000, null)
                        favLocation = Pair(address.getAddressLine(0), latLng)
                    }
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {

                //                if (query != null && googleMap != null) {
//                        val geocoder = Geocoder(requireContext())
//                        val addressList: MutableList<Address>? =
//                            query?.let { geocoder.getFromLocationName(it, 15) }
//                        Log.i("query", "onQueryTextChange: $addressList")
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


    override fun onDestroyView() {
        super.onDestroyView()
        val result = Bundle().apply {
            putString("bundleKey", "someValue")
        }
        setFragmentResult("requestKey", result)
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

