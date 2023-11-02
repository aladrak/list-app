package com.example.list.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.list.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class TabFragment(): Fragment() {
    private lateinit var collectionAdapter: CollectionAdapter
    private lateinit var viewPager: ViewPager2

//    public fun setPage(page: Int) {
//        viewPager.setCurrentItem(page, true)
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tab_fragment, container, false)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        collectionAdapter = CollectionAdapter(this)
        viewPager = view.findViewById(R.id.pager)
        viewPager.adapter = collectionAdapter

        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = ""
                    tab.icon = resources.getDrawable(
                        R.drawable.baseline_play_arrow_24,
                        requireContext().theme
                    )
                }

                1 -> {
                    tab.text = ""
                    tab.icon = resources.getDrawable(
                        R.drawable.baseline_pause_24,
                        requireContext().theme
                    )
                }
            }
        }.attach()
    }
}