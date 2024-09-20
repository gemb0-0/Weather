package com.example.weather.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentManager: FragmentManager,lifecycle: Lifecycle,arrayList: ArrayList<Fragment>): FragmentStateAdapter(fragmentManager,lifecycle) {
  val FragmentList = arrayList

    override fun getItemCount(): Int {
        return FragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return FragmentList[position]
    }

}