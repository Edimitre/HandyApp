package com.edimitre.handyapp.activity


import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.edimitre.handyapp.adapters.tabs_adapter.NewsTabAdapter
import com.edimitre.handyapp.data.util.SystemService
import com.edimitre.handyapp.data.view_model.NewsViewModel
import com.edimitre.handyapp.databinding.ActivityNewsBinding
import com.edimitre.handyapp.fragment.news.BotaAlFragment
import com.edimitre.handyapp.fragment.news.JoqFragment
import com.edimitre.handyapp.fragment.news.LapsiFragment
import com.edimitre.handyapp.fragment.news.SyriFragment
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class NewsActivity : AppCompatActivity() {


    @Inject
    lateinit var systemService: SystemService

    lateinit var binding: ActivityNewsBinding

    private lateinit var tabs: TabLayout

    private lateinit var pagerAdapter: NewsTabAdapter

    private lateinit var viewPager: ViewPager2


    private val newsViewModel: NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewsBinding.inflate(layoutInflater)

        setContentView(binding.root)





        initFragmentTabs()


        startScrapers()


    }


    private fun startScrapers() {
        lifecycleScope.launch {


            val botaAlNews = newsViewModel.getOneBySource("bota.al")

            when (botaAlNews) {
                null -> {
                    systemService.startScrapBotaAl()
                }
                else -> {
                    newsViewModel.deleteAllBySource("bota.al")
                        .also { systemService.startScrapBotaAl() }

                }
            }

            val joqAlNews = newsViewModel.getOneBySource("joq.al")

            when (joqAlNews) {
                null -> {
                    systemService.startScrapJoqAl()
                }
                else -> {
                    newsViewModel.deleteAllBySource("joq.al")
                        .also { systemService.startScrapJoqAl() }

                }
            }

            val lapsiNews = newsViewModel.getOneBySource("lapsi.al")

            when (lapsiNews) {
                null -> {
                    systemService.startScrapLapsiAl()
                }
                else -> {
                    newsViewModel.deleteAllBySource("lapsi.al")
                        .also { systemService.startScrapLapsiAl() }

                }
            }

            val syriNews = newsViewModel.getOneBySource("syri.net")

            when (syriNews) {
                null -> {
                    systemService.startScrapSyriNet()
                }
                else -> {
                    newsViewModel.deleteAllBySource("syri.net")
                        .also { systemService.startScrapSyriNet() }

                }
            }
        }
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
            SyriFragment()
        )
    }


}