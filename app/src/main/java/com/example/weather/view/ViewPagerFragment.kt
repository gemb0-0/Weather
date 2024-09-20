package com.example.weather.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.weather.R

class ViewPagerFragment : Fragment() {
        lateinit var viewpager: ViewPager2
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_pager, container, false)
        val fragmentList = arrayListOf<Fragment>(
            MapsFragment(),
            TodaysWeather(),
            FavouritesFragment()

        )
        val adapter = ViewPagerAdapter(requireActivity().supportFragmentManager, lifecycle, fragmentList)
        viewpager = view.findViewById(R.id.viewpager)

        viewpager.adapter = adapter
        return view
    }


}