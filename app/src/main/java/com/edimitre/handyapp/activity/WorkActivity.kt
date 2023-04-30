package com.edimitre.handyapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.edimitre.handyapp.R
import com.edimitre.handyapp.data.model.WorkDay
import com.edimitre.handyapp.data.util.PermissionUtil
import com.edimitre.handyapp.data.util.SystemService
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

    private lateinit var _workDayViewModel: WorkDayViewModel


    @Inject
    lateinit var systemService: SystemService

    companion object {

        var PERMISSION_REQUEST_CODE: Int = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWorkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()

        displayFragment(WorkDaysFragment())

        setActiveButton("WORKDAYS")

        setListeners()

        val permissionUtil = PermissionUtil(this@WorkActivity)

        if (!permissionUtil.hasPermission()) {

            permissionUtil.requestStoragePermission()

        }
    }

    private fun initViewModel() {

        _workDayViewModel = ViewModelProvider(this)[WorkDayViewModel::class.java]


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

    private fun setActiveButton(value: String) {

        when (value) {

            "WORKDAYS" -> {

                binding.btnWorkdaysFragment.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.purple_200
                    )
                )
                binding.btnFilesFragment.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.open_purple
                    )
                )
            }

            "FILES" -> {
                binding.btnWorkdaysFragment.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.open_purple
                    )
                )
                binding.btnFilesFragment.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.purple_200
                    )
                )

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

//    private fun createFolder() {
//
//        val folderName = HandyAppEnvironment.FILES_STORAGE_DIRECTORY
//
//        val file = File("${Environment.getExternalStorageDirectory()}/$folderName")
//
//        var folderCreated = false
//
//        if (!file.exists()) {
//            folderCreated = file.mkdir()
//        }
//        if (folderCreated) {
//            Log.e(TAG, "folder created")
//        } else {
//
//            Log.e(TAG, "folder not created because it exists")
//        }
//    }


}