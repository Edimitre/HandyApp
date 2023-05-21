package com.edimitre.handyapp.data.util

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.edimitre.handyapp.data.dao.CigarDao
import com.edimitre.handyapp.data.dao.ReminderNotesDao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var systemService: SystemService


    @Inject
    lateinit var reminderDao: ReminderNotesDao

    @Inject
    lateinit var cigarDao: CigarDao

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {

        systemService.cancelAllAlarms()

        activateAlarmsIfAny()
    }

    private fun activateAlarmsIfAny() {

        runBlocking {
            val reminder = reminderDao.getFirstReminderOnCoroutine()

            if (reminder != null) {
                systemService.setAlarm(reminder.alarmTimeInMillis)
            }


            val cigar = cigarDao.getFirstCigarOnCoroutine()
            if(cigar != null){
                systemService.setCigarAlarm(cigar.alarmInMillis)
            }

        }

    }

}