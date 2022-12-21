package com.edimitre.handyapp.data.util

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.edimitre.handyapp.data.dao.ReminderNotesDao
import com.edimitre.handyapp.data.model.Reminder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var systemService: SystemService


    @Inject
    lateinit var reminderDao: ReminderNotesDao

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {

        systemService.cancelAllAlarms()

        Thread {
            activateFirstReminder()
        }.start()
    }

    private fun activateFirstReminder() {


        val reminder: Reminder? = reminderDao.getFirstReminderOnThread()


        if (reminder != null) {

            systemService.setAlarm(reminder.alarmTimeInMillis)

        }

    }

}