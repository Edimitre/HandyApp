package com.edimitre.handyapp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.edimitre.handyapp.HandyAppEnvironment.TAG
import com.edimitre.handyapp.adapters.tabs_adapter.RemindersAndNotesPagerAdapter
import com.edimitre.handyapp.data.model.Cigar
import com.edimitre.handyapp.data.view_model.CigaretteViewModel
import com.edimitre.handyapp.databinding.ActivityCigaretteReminderBinding
import com.edimitre.handyapp.fragment.smoking_fragment.CigarsFragment
import com.edimitre.handyapp.fragment.smoking_fragment.GameTableFragment
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CigaretteReminderActivity : AppCompatActivity(){

    private lateinit var tabs: TabLayout

    private lateinit var pagerAdapter: RemindersAndNotesPagerAdapter

    private lateinit var viewPager: ViewPager2

    private lateinit var binding: ActivityCigaretteReminderBinding

    private val _cigarViewModel: CigaretteViewModel by viewModels()

    var minutes: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCigaretteReminderBinding.inflate(layoutInflater)

        setContentView(binding.root)

        loadPageNavigation()

        setListeners()

        observeSelectedMinutes()

        observeCigars()

//        checkIfNotificationAction()
    }

//    // if application is starting and receives notification intent
//    private fun checkIfNotificationAction(){
//
//        val bundle:Bundle? = intent?.extras
//
//        if(bundle != null){
//
//            val isWin = bundle.getBoolean("IS_WIN")
//            val cigarId = bundle.getString("CIGAR_ID")
//
//            Log.e(TAG, "onCreate intent is win: $isWin", )
//            Log.e(TAG, "on create intent cigarId: $cigarId", )
//
////            _cigarViewModel.setCigarIsWin(isWin,cigarId!!)
//
////            Toast.makeText(this, "is win $isWin", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    // if application is open and receives notification intent
//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//
//        val bundle:Bundle? = intent?.extras
//
//        if(bundle != null){
//
//            val isWin = bundle.getBoolean("IS_WIN")
//            val cigarId = bundle.getString("CIGAR_ID")
//            _cigarViewModel.setCigarIsWin(isWin,cigarId!!)
//
////            Toast.makeText(this, "is win $isWin", Toast.LENGTH_SHORT).show()
//        }
//    }

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

    private fun observeSelectedMinutes(){
        _cigarViewModel.timeSelected.observe(this){
            if(it != 0L){
                this.minutes = it
            }
        }
    }

    private fun setListeners() {

        binding.btnAdd.setOnClickListener {
            when (viewPager.currentItem) {
                0 -> {

                    if(this.minutes != null){

                        _cigarViewModel.distributeCigars(this.minutes!!)
                    }else{
                        Toast.makeText(this, "distance time cant be null", Toast.LENGTH_SHORT).show()
                    }

                }
                1 -> {

                }
            }
        }
    }

    private fun observeCigars(){

        _cigarViewModel.allCigars?.observe(this){

            activateAddButton(it)
        }

    }

    private fun activateAddButton(listCigars:List<Cigar>){

        binding.btnAdd.isEnabled = listCigars.isEmpty()
    }

}