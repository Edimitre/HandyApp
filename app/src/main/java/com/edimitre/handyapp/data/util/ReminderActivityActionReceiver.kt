package com.edimitre.handyapp.data.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.edimitre.handyapp.data.dao.ReminderNotesDao
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

        runBlocking {

            val reminder = reminderNotesDao.getFirstReminderOnCoroutine()
            reminder?.isActive = false

            reminderNotesDao.saveReminder(reminder)

        }
    }

}