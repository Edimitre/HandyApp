package com.edimitre.handyapp.data.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.edimitre.handyapp.data.dao.ReminderNotesDao
import com.edimitre.handyapp.data.model.Reminder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var reminderDao: ReminderNotesDao

    @Inject
    lateinit var systemService: SystemService

    override fun onReceive(context: Context, intent: Intent) {

        Thread {
            setFirstReminderFalse()
            activateNextReminder()
        }.start()

    }

    private fun setFirstReminderFalse() {

        val reminder: Reminder? = reminderDao.getFirstReminderOnThread()

        if (reminder != null) {

            systemService.notify("Reminding you of : ", reminder.description)

            reminder.isActive = false

            reminderDao.saveReminderOnThread(reminder)
        }

    }

    private fun activateNextReminder() {

        val reminder: Reminder? = reminderDao.getFirstReminderOnThread()

        if (reminder != null) {
            systemService.cancelAllAlarms()

            systemService.setAlarm(reminder.alarmTimeInMillis)
        }

    }
}