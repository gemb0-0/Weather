package com.example.weather.view.favourites

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.R
import com.example.weather.databinding.FragmentFavouritesBinding
import com.example.weather.model.Repository
import com.example.weather.model.localDataSource.Sharedpref
import com.example.weather.model.remoteDataSource.RemoteDataSource
import com.example.weather.view.map.MapsFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch


class FavouritesFragment : Fragment(), onFavClick {
    lateinit var binding: FragmentFavouritesBinding
    lateinit var viewModel: FavouritesViewModel
    lateinit var adapter: FavouriteAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = FavouriteAdapter(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter

        val sharedpref = requireActivity().getSharedPreferences("favourites", MODE_PRIVATE)
        viewModel = FavouritesViewModel.FavouritesViewModelFactory(Repository(
            RemoteDataSource.getInstance(),
            Sharedpref()
        ),sharedpref).create(FavouritesViewModel::class.java)
        viewModel.getFavourites()
        lifecycleScope.launch {
            viewModel.fav.collect {
                Log.i("FavouritesFragment", "onViewCreated: $it")
                if(it.isEmpty()){
                    binding.imageView4.visibility = View.VISIBLE
                    binding.textView6.visibility = View.VISIBLE
                }
                else{
                    binding.imageView4.visibility = View.GONE
                    binding.textView6.visibility = View.GONE
                }
                adapter.submitList(it)

            }
        }

        binding.floatingActionButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, MapsFragment())
                .addToBackStack(null)
                .commit()
        }

        setFragmentResultListener("requestKey") { requestKey, bundle ->
            val result = bundle.getString("bundleKey")
            if (result == "someValue") {
                (activity as AppCompatActivity).supportActionBar?.show()
                viewModel.getFavourites()
                Log.i("FavouritesFragment", "listenerrrr ")
            }
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            Log.i("FavouritesFragment", "onViewCreated: Refreshing")
            viewModel.getFavourites()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }


    override fun onFavClick(city: LatLng) {
        Log.i("FavouriteAdapter no it's frag", "onFavClick: $city")
        val bundle = Bundle()
        bundle.putParcelable("city",city)
        findNavController().navigate(R.id.todaysWeather, bundle)
    }

    override fun inFavDelete(city: LatLng) {
        Log.i("FavouriteAdapter no it's frag", "inFavDelete: $city")
        viewModel.deleteFavourite(city)
    }
}