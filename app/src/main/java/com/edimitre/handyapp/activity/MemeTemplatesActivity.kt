package com.edimitre.handyapp.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.edimitre.handyapp.adapters.tabs_adapter.MemeTemplatesAdapter
import com.edimitre.handyapp.data.view_model.MemeTemplateViewModel
import com.edimitre.handyapp.databinding.ActivityMemeTemplatesBinding
import com.edimitre.handyapp.fragment.meme_templates.AllTemplatesFragment
import com.edimitre.handyapp.fragment.meme_templates.SelectTemplateFragment
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MemeTemplatesActivity : AppCompatActivity() {


    private lateinit var pagerAdapter: MemeTemplatesAdapter

    private lateinit var tabs: TabLayout

    private lateinit var viewPager: ViewPager2

    lateinit var binding: ActivityMemeTemplatesBinding

    private val _memeTemplatesViewModel: MemeTemplateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMemeTemplatesBinding.inflate(layoutInflater)

        setContentView(binding.root)

        loadPageNavigation()

    }

    private fun loadPageNavigation() {
        // get adapter
        pagerAdapter = MemeTemplatesAdapter(getMemeFragments(), this)
        // get tabs
        tabs = binding.memeTemplateTabs

        // get viewPager
        viewPager = binding.memeTemplatesViewPager

        // remove slide functionality
        viewPager.isUserInputEnabled = false
        // set view pager adapter
        viewPager.adapter = pagerAdapter


        //
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

    }

    private fun getMemeFragments(): ArrayList<Fragment> {
        return arrayListOf(
            SelectTemplateFragment(),
            AllTemplatesFragment()
        )
    }



}