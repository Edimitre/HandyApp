package com.edimitre.handyapp.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.HandyAppEnvironment.TAG
import com.edimitre.handyapp.R
import com.edimitre.handyapp.data.model.WorkDay
import com.edimitre.handyapp.data.util.SystemService
import com.edimitre.handyapp.data.view_model.WorkDayViewModel
import com.edimitre.handyapp.databinding.ActivityWorkBinding
import com.edimitre.handyapp.fragment.work_related.AddWorkDayForm
import com.edimitre.handyapp.fragment.work_related.FilesFragment
import com.edimitre.handyapp.fragment.work_related.WorkDaysFragment
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class WorkActivity : AppCompatActivity(), AddWorkDayForm.AddWorkDayListener {

    lateinit var binding: ActivityWorkBinding

    private lateinit var _workDayViewModel: WorkDayViewModel


    @Inject
    lateinit var systemService :SystemService

    companion object{

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

        if (hasPermission()) {

            createFolder()


        } else {

            Log.e(TAG, "onCreate: permission not granted requesting")
           requestPermission()
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

    private fun setActiveButton(value:String){

        when(value){

            "WORKDAYS"-> {

                binding.btnWorkdaysFragment.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_200))
                binding.btnFilesFragment.setBackgroundColor(ContextCompat.getColor(this, R.color.open_purple))
            }

            "FILES"-> {
                binding.btnWorkdaysFragment.setBackgroundColor(ContextCompat.getColor(this, R.color.open_purple))
                binding.btnFilesFragment.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_200))

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

    private fun createFolder() {

        val folderName = HandyAppEnvironment.FILES_STORAGE_DIRECTORY

        val file = File("${Environment.getExternalStorageDirectory()}/$folderName")

        var folderCreated = false

        if(!file.exists()){
            folderCreated = file.mkdir()
        }
        if (folderCreated) {
            Log.e(TAG, "folder created")
        } else {

            Log.e(TAG, "folder not created because it exists")
        }
    }

    private fun hasPermission(): Boolean {


        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // android is 11(R) or above

            Environment.isExternalStorageManager()

        } else {


            val write =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val read =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

            write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED
        }

    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            AlertDialog.Builder(this@WorkActivity)
                .setTitle("PERMISSION REQUEST")
                .setMessage("Please allow the Storage permission..\nit is required for creating excel files !")
                .setPositiveButton("Accept"
                ) { _, _ ->
                    try {
                        val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                        intent.addCategory("android.intent.category.DEFAULT")
                        intent.data = Uri.parse(
                            String.format(
                                "package:%s", applicationContext.packageName
                            )
                        )
                        storageActivityResultLauncher.launch(intent)
                    } catch (e: Exception) {
                        val intent = Intent()
                        intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                        storageActivityResultLauncher.launch(intent)
                    }
                }
                .setNegativeButton("Decline") { dialog, _ ->
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                .setCancelable(false)
                .show()
        } else {
            ActivityCompat.requestPermissions(this@WorkActivity, arrayOf(
                 Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), PERMISSION_REQUEST_CODE)
        }
    }

    private val storageActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {

                    createFolder()
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                } else {

                    // menage external storage permission denied

//                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()

                }
            } else {

                //android is below 11(R)
            }

        }


}