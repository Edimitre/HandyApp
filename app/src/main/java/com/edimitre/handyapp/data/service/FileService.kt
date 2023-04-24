package com.edimitre.handyapp.data.service

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.internal.ContextUtils.getActivity
import javax.inject.Inject

class FileService @Inject constructor(private val context: Context) {




    @SuppressLint("RestrictedApi")
    fun getActivityByContext(context: Context?): Activity? {
        if (context == null) {
            return null
        } else if (context is ContextWrapper && context is Activity) {
            return context
        } else if (context is ContextWrapper) {
            return getActivity(context.baseContext)
        }
        return null
    }

}