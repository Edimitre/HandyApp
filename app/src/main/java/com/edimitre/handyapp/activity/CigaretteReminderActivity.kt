package com.edimitre.handyapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.edimitre.handyapp.HandyAppEnvironment.TAG
import com.edimitre.handyapp.R
import com.edimitre.handyapp.adapters.tabs_adapter.RemindersAndNotesPagerAdapter
import com.edimitre.handyapp.data.model.Shop
import com.edimitre.handyapp.data.util.TimeUtils
import com.edimitre.handyapp.databinding.ActivityCigaretteReminderBinding
import com.edimitre.handyapp.databinding.ActivityReminderNotesBinding
import com.edimitre.handyapp.fragment.reminder_and_notes.NotesFragment
import com.edimitre.handyapp.fragment.reminder_and_notes.RemindersFragment
import com.edimitre.handyapp.fragment.smoking_fragment.CigarsFragment
import com.edimitre.handyapp.fragment.smoking_fragment.GameTableFragment
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import java.sql.Time

@AndroidEntryPoint
class CigaretteReminderActivity : AppCompatActivity(),CigarsFragment.TimeDistanceSetListener {

    private lateinit var tabs: TabLayout

    private lateinit var pagerAdapter: RemindersAndNotesPagerAdapter

    private lateinit var viewPager: ViewPager2

    private lateinit var binding: ActivityCigaretteReminderBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCigaretteReminderBinding.inflate(layoutInflater)

        setContentView(binding.root)

        loadPageNavigation()

        setListeners()
    }

    private fun loadPageNavigation() {
        // get adapter
        pagerAdapter = RemindersAndNotesPagerAdapter(getSmokingFragments(), this)
        // get tabs
        tabs = binding.remindersAndNotesTabs

        // get viewPager
        viewPager = binding.reminderAndNoteViewPager

        // remove slide functionality
        viewPager.isUserInputEnabled = false
        // set view pager adapter
        viewPager.adapter = pagerAdapter


        //
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

    }

    private fun getSmokingFragments(): ArrayList<Fragment> {
        return arrayListOf(
            CigarsFragment(),
            GameTableFragment()
        )
    }

    private fun setListeners() {

        binding.btnAdd.setOnClickListener {
            when (viewPager.currentItem) {
                0 -> {

                }
                1 -> {

                }
            }
        }
    }

    override fun onTimeDistanceSet(timeInMillis:Int) {

        distributeCigars(timeInMillis)

    }

    private fun distributeCigars(minutes: Int) {

        // todo get time now
        var timeNow = TimeUtils().getTimeInMilliSeconds(TimeUtils().getCurrentYear(), TimeUtils().getCurrentMonth(),TimeUtils().getCurrentDate())
        val timeToAdd = timeNow + 60 * 1000 * minutes

        for (i in 0..20){


            timeNow += timeToAdd

            val date = TimeUtils().getDateStringFromMilliSeconds(timeNow)
            Log.e(TAG, "distributeCigars: $date", )
        }
        // function that ad that time
    }

}