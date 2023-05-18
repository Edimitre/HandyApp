package com.edimitre.handyapp.data.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.edimitre.handyapp.data.dao.CigarDao
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CigarAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var cigarDao: CigarDao

    @Inject
    lateinit var systemService: SystemService

    override fun onReceive(context: Context, intent: Intent) {

        context.startForegroundService(Intent(context, ShowCigarAlarmService::class.java))
    }

}