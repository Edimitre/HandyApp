package com.edimitre.handyapp.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.edimitre.handyapp.R
import com.edimitre.handyapp.data.model.WorkDay
import com.edimitre.handyapp.data.util.CommonUtil
import com.edimitre.handyapp.data.util.SystemService
import com.edimitre.handyapp.data.util.TimeUtils
import com.edimitre.handyapp.data.view_model.FilesViewModel
import com.edimitre.handyapp.data.view_model.WorkDayViewModel
import com.edimitre.handyapp.databinding.ActivityWorkBinding
import com.edimitre.handyapp.fragment.work_related.AddWorkDayForm
import com.edimitre.handyapp.fragment.work_related.FilesFragment
import com.edimitre.handyapp.fragment.work_related.WorkDaysFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class WorkActivity : AppCompatActivity(), AddWorkDayForm.AddWorkDayListener {

    lateinit var binding: ActivityWorkBinding

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


        displayFragment(WorkDaysFragment())

        setActiveButton("WORKDAYS")

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

    private fun setListeners() {

        binding.btnAdd.setOnClickListener {

            val reminderForm = AddWorkDayForm()
            reminderForm.show(supportFragmentManager, "add reminder")
        }

        binding.btnWorkdaysFragment.setOnClickListener {
            displayFragment(WorkDaysFragment())
            setActiveButton("WORKDAYS")

        }

        binding.btnFilesFragment.setOnClickListener {
            displayFragment(FilesFragment())
            setActiveButton("FILES")
        }

    }

    @SuppressLint("ResourceType")
    private fun setActiveButton(value: String) {

        when (value) {

            "WORKDAYS" -> {

                binding.btnWorkdaysFragment.cameraDistance = 10F

                binding.btnFilesFragment.cameraDistance = 0F
            }

            "FILES" -> {
                binding.btnWorkdaysFragment.cameraDistance = 0F
                binding.btnFilesFragment.cameraDistance = 10F

            }
        }
    }

    private fun displayFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frag_container, fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()
    }

    override fun addWorkDay(workDay: WorkDay) {


        _workDayViewModel.saveWorkDay(workDay)
    }


    private fun setLoading(value: Boolean) {

        if (value) {
            binding.progressLayout.visibility = View.VISIBLE
            binding.fragContainer.visibility = View.INVISIBLE
            binding.coorLayout.visibility = View.INVISIBLE

        } else {

            binding.progressLayout.visibility = View.INVISIBLE
            binding.fragContainer.visibility = View.VISIBLE
            binding.coorLayout.visibility = View.VISIBLE


        }

    }


}