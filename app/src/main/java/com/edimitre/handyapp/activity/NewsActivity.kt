package com.edimitre.handyapp.activity


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.edimitre.handyapp.adapters.tabs_adapter.NewsTabAdapter
import com.edimitre.handyapp.data.util.SystemService
import com.edimitre.handyapp.databinding.ActivityNewsBinding
import com.edimitre.handyapp.fragment.news.*
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class NewsActivity : AppCompatActivity() {


    @Inject
    lateinit var systemService: SystemService

    lateinit var binding: ActivityNewsBinding

    private lateinit var tabs: TabLayout

    private lateinit var pagerAdapter: NewsTabAdapter

    private lateinit var viewPager: ViewPager2


//    private val newsViewModel: NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewsBinding.inflate(layoutInflater)

        setContentView(binding.root)





        initFragmentTabs()


    }


    private fun initFragmentTabs() {
        // get adapter
        pagerAdapter = NewsTabAdapter(getListOfFragments(), this)
        // get tabs
        tabs = binding.newsTabs

        // get viewPager
        viewPager = binding.newsViewPager

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

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

    }

    private fun getListOfFragments(): ArrayList<Fragment> {
        return arrayListOf(
            BotaAlFragment(),
            JoqFragment(),
            LapsiFragment(),
            SyriFragment(),
            LikedNewsFragment()
        )
    }


}