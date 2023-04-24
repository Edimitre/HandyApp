package com.edimitre.handyapp.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.edimitre.handyapp.HandyAppEnvironment.TAG
import com.edimitre.handyapp.R
import com.edimitre.handyapp.data.model.WorkDay
import com.edimitre.handyapp.data.service.FileService
import com.edimitre.handyapp.data.view_model.ReminderViewModel
import com.edimitre.handyapp.data.view_model.WorkDayViewModel
import com.edimitre.handyapp.databinding.ActivityNewsBinding
import com.edimitre.handyapp.databinding.ActivityWorkBinding
import com.edimitre.handyapp.fragment.reminder_and_notes.AddReminderForm
import com.edimitre.handyapp.fragment.work_related.AddWorkDayForm
import com.edimitre.handyapp.fragment.work_related.WorkDaysFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WorkActivity : AppCompatActivity() ,AddWorkDayForm.AddWorkDayListener{

    lateinit var binding: ActivityWorkBinding

    private lateinit var _workDayViewModel:WorkDayViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWorkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()

        checkPermissions()
        displayFragment(WorkDaysFragment())

        setListeners()
    }

    private fun initViewModel(){

        _workDayViewModel = ViewModelProvider(this)[WorkDayViewModel::class.java]


    }

    private fun setListeners(){

        binding.btnAdd.setOnClickListener {

            val reminderForm = AddWorkDayForm()
            reminderForm.show(supportFragmentManager, "add reminder")
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


    fun checkPermissions(){

        // sdk between 23 and 29
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 100)

            }

        }

        // sdk 30 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            if(Environment.isExternalStorageManager()){

                try {
                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    intent.addCategory("android.intent.category.DEFAULT")
                    intent.data = Uri.parse(String.format("package:%s", applicationContext.packageName))
                    startActivityIfNeeded(intent,101)
                }catch (e:java.lang.Exception){


                    val intent = Intent()
                    intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                    startActivityIfNeeded(intent,101)
                }
            }

        }
    }

}