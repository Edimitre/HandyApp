package com.edimitre.handyapp.data.util

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.edimitre.handyapp.activity.MainActivity
import com.edimitre.handyapp.activity.WorkActivity
import com.edimitre.handyapp.data.service.FileService
import java.io.File

class CommonUtil(private val activity: AppCompatActivity) {

    fun hasPermission(): Boolean {


        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // android is 11(R) or above

            Environment.isExternalStorageManager()

        } else {

            // android is below 11(R)

            val write =
                ContextCompat.checkSelfPermission(
                    activity.applicationContext,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            val read =
                ContextCompat.checkSelfPermission(
                    activity.applicationContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )

            write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED
        }

    }

    fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            AlertDialog.Builder(activity)
                .setTitle("PERMISSION REQUEST")
                .setMessage("Please allow the Storage permission..\nit is required for creating excel files !")
                .setPositiveButton(
                    "Accept"
                ) { _, _ ->
                    try {
                        val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                        intent.addCategory("android.intent.category.DEFAULT")
                        intent.data = Uri.parse(
                            String.format(
                                "package:%s", activity.applicationContext.packageName
                            )
                        )
                        this.storageActivityResultLauncher.launch(intent)
                    } catch (e: Exception) {
                        val intent = Intent()
                        intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                        this.storageActivityResultLauncher.launch(intent)
                    }
                }
                .setNegativeButton("Decline") { dialog, _ ->
                    Toast.makeText(
                        activity.applicationContext,
                        "Permission denied",
                        Toast.LENGTH_SHORT
                    ).show()
                    dialog.dismiss()
                    activity.let {
                        it.startActivity(Intent(it, MainActivity::class.java))
                    }
                }
                .setCancelable(false)
                .show()
        } else {
            ActivityCompat.requestPermissions(
                activity, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), WorkActivity.PERMISSION_REQUEST_CODE
            )
        }
    }

    private val storageActivityResultLauncher =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {

                    FileService().createStorageDirectory()

                    Toast.makeText(activity, "Permission granted", Toast.LENGTH_SHORT).show()
                } else {

                    // menage external storage permission denied
                    Toast.makeText(activity, "Permission denied", Toast.LENGTH_SHORT).show()

                }
            } else {

                //android is below 11(R)
            }

        }


    fun shareOnOtherApp(file: File) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND

            val uri = FileProvider.getUriForFile(activity, activity.packageName + ".provider", file)
            putExtra(Intent.EXTRA_STREAM, uri)
            type = activity.contentResolver.getType(uri)
        }

        activity.startActivity(sendIntent)

    }


    fun openOnOtherApp(file: File) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_VIEW

            val myMime: MimeTypeMap = MimeTypeMap.getSingleton()
            val fileMime = myMime.getMimeTypeFromExtension(file.extension).toString()
            val uri = FileProvider.getUriForFile(activity, activity.packageName + ".provider", file)
            setDataAndType(uri, fileMime)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        activity.startActivity(sendIntent)

    }


}