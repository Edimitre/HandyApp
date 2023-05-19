package com.edimitre.handyapp.data.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.edimitre.handyapp.data.dao.CigarDao
import com.edimitre.handyapp.data.dao.CigarGameTableDao
import com.edimitre.handyapp.data.dao.ReminderNotesDao
import com.edimitre.handyapp.data.model.CigarGameTable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@AndroidEntryPoint
class ReminderActivityActionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var reminderNotesDao: ReminderNotesDao


    @Inject
    lateinit var systemService: SystemService

    override fun onReceive(context: Context, intent: Intent) {

        context.stopService(Intent(context, ShowReminderAlarmService::class.java))

        systemService.stopVibrator()
        systemService.stopRingtone()

        runBlocking {

            val reminder = reminderNotesDao.getFirstReminderOnCoroutine()
            reminder?.isActive = false

            reminderNotesDao.saveReminder(reminder)

        }
    }

}