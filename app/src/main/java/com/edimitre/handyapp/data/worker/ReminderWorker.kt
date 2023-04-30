package com.edimitre.handyapp.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.edimitre.handyapp.data.model.Reminder
import com.edimitre.handyapp.data.room_database.HandyDb
import com.edimitre.handyapp.data.util.SystemService


class ReminderWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val ctx = context

    var systemService = SystemService(ctx)

    override suspend fun doWork(): Result {

        val reminderDao = HandyDb.getInstance(ctx).getReminderNotesDao()

        val reminder: Reminder? = reminderDao.getFirstReminderOnCoroutine()

        if (reminder != null) {

            systemService.notify("Reminding you of : ", reminder.description)

            reminder.isActive = false

            reminderDao.saveReminder(reminder)


            val nextReminder: Reminder? = reminderDao.getFirstReminderOnThread()

            if (nextReminder != null) {
                systemService.cancelAllAlarms()

                systemService.setAlarm(nextReminder.alarmTimeInMillis)
            }
        }

        return Result.success()
    }


}