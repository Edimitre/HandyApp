package com.edimitre.handyapp.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.edimitre.handyapp.R
import com.edimitre.handyapp.adapters.tabs_adapter.RemindersAndNotesPagerAdapter
import com.edimitre.handyapp.adapters.tabs_adapter.WorkFragmentsAdapter
import com.edimitre.handyapp.data.model.WorkDay
import com.edimitre.handyapp.data.util.CommonUtil
import com.edimitre.handyapp.data.util.SystemService
import com.edimitre.handyapp.data.util.TimeUtils
import com.edimitre.handyapp.data.view_model.FilesViewModel
import com.edimitre.handyapp.data.view_model.WorkDayViewModel
import com.edimitre.handyapp.databinding.ActivityWorkBinding
import com.edimitre.handyapp.fragment.reminder_and_notes.NotesFragment
import com.edimitre.handyapp.fragment.reminder_and_notes.RemindersFragment
import com.edimitre.handyapp.fragment.work_related.AddWorkDayForm
import com.edimitre.handyapp.fragment.work_related.FilesFragment
import com.edimitre.handyapp.fragment.work_related.WorkDaysFragment
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class WorkActivity : AppCompatActivity(), AddWorkDayForm.AddWorkDayListener {

    lateinit var binding: ActivityWorkBinding

    private lateinit var tabs: TabLayout

    private lateinit var pagerAdapter: WorkFragmentsAdapter

    private lateinit var viewPager: ViewPager2

    private val _workDayViewModel: WorkDayViewModel by viewModels()

    private val _filesViewModel: FilesViewModel by viewModels()

    private lateinit var commonUtil: CommonUtil

    @Inject
    lateinit var systemService: SystemService

    companion object {

        var PERMISSION_REQUEST_CODE: Int = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWorkBinding.inflate(layoutInflater)
        setContentView(binding.root)


        loadPageNavigation()

        setListeners()

        observeForLoading()

        commonUtil = CommonUtil(this@WorkActivity)

        if (!commonUtil.hasPermission()) {

            commonUtil.requestStoragePermission()

        }


    }



    private fun observeForLoading(){

        _filesViewModel.isFilesFragmentRefreshing.observe(this){

            setLoading(it)
        }
    }

    private fun loadPageNavigation() {
        // get adapter
        pagerAdapter = WorkFragmentsAdapter(getWorkFragments(), this)
        // get tabs
        tabs = binding.workFragmentsTabs

        // get viewPager
        viewPager = binding.workFragmentsViewPager

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

    private fun getWorkFragments(): ArrayList<Fragment> {
        return arrayListOf(
            WorkDaysFragment(),
            FilesFragment()
        )
    }

    private fun setListeners() {

        binding.btnAdd.setOnClickListener {

            val reminderForm = AddWorkDayForm()
            reminderForm.show(supportFragmentManager, "add reminder")
        }


    }


    override fun addWorkDay(workDay: WorkDay) {


        _workDayViewModel.saveWorkDay(workDay)
    }


    private fun setLoading(value: Boolean) {

//        if (value) {
//            binding.progressLayout.visibility = View.VISIBLE
//            binding.fragContainer.visibility = View.INVISIBLE
//            binding.coorLayout.visibility = View.INVISIBLE
//
//        } else {
//
//            binding.progressLayout.visibility = View.INVISIBLE
//            binding.coorLayout.visibility = View.VISIBLE
//
//        }

    }


}