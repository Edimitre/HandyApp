package com.edimitre.handyapp.data.worker

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.HandyAppEnvironment.TAG
import com.edimitre.handyapp.R
import com.edimitre.handyapp.data.model.Reminder
import com.edimitre.handyapp.data.model.firebase.BackUpDto
import com.edimitre.handyapp.data.room_database.HandyDb
import com.edimitre.handyapp.data.util.SystemService
import com.edimitre.handyapp.data.util.TimeUtils
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ImportDBWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val ctx = context

    var systemService = SystemService(ctx)

    private var progress = "";


    override suspend fun doWork(): Result {


        val backUpData = inputData.getString("backup_data")
        val backUpDto = Gson().fromJson(backUpData, BackUpDto::class.java)
        importDto(backUpDto)


        setForegroundAsync(createForegroundInfo(progress))


        delay(10000)
        return Result.success()
    }

    private suspend fun importDto(backUpDto: BackUpDto) {


        val shopDao = HandyDb.getInstance(ctx).getShopExpenseDao()
        val noteDao = HandyDb.getInstance(ctx).getReminderNotesDao()
        val newsDao = HandyDb.getInstance(ctx).getNewsDao()
        val workDayDao = HandyDb.getInstance(ctx).getWorkDayDao()

        progress = "starting restore";

        Log.e(TAG, "starting restore")

        if (backUpDto.shopList.isNotEmpty()) {

            progress = "shops found ...restoring"
            backUpDto.shopList.forEach { shop ->
                shopDao.saveOrUpdateShop(shop)
                delay(1000)
            }



        }
        if (backUpDto.expenseList.isNotEmpty()) {
            progress = "expenses found ...restoring"
            backUpDto.expenseList.forEach { expense ->
                shopDao.saveOrUpdateExpense(expense)
                delay(1000)
            }


        }

        if (backUpDto.notesList.isNotEmpty()) {
            progress = "notes found ...restoring"
            backUpDto.notesList.forEach { note ->
                noteDao.save(note)
                delay(1000)
            }


        }

        if (backUpDto.reminderList.isNotEmpty()) {
            progress = "reminders found ...restoring"
            backUpDto.reminderList.forEach { reminder ->
                noteDao.saveReminder(reminder)
                delay(1000)
                activateReminder(reminder)
            }


        }


        if (backUpDto.likedNewsList.isNotEmpty()) {

            progress = "liked news found ...restoring"
            backUpDto.likedNewsList.forEach { news ->
                newsDao.insert(news)

                delay(1000)
            }

        }

        if (backUpDto.workDaysList.isNotEmpty()) {

            progress = "workdays found ...restoring"
            backUpDto.workDaysList.forEach { workday ->
                workDayDao.save(workday)

                delay(1000)
            }

        }

        progress = "finished ...restoring"

    }

    private fun activateReminder(reminder: Reminder) {

        val actualTime = TimeUtils().getTimeInMilliSeconds(
            TimeUtils().getCurrentYear(),
            TimeUtils().getCurrentMonth(),
            TimeUtils().getCurrentDate(),
            TimeUtils().getCurrentHour(),
            TimeUtils().getCurrentMinute()
        )

        val reminderTime = reminder.alarmTimeInMillis

        when {
            reminderTime > actualTime -> {
                systemService.setAlarm(reminder.alarmTimeInMillis)
            }
        }

    }

    // Creates an instance of ForegroundInfo which can be used to update the
    // ongoing notification.
    private fun createForegroundInfo(progress: String): ForegroundInfo {
        val id = HandyAppEnvironment.NOTIFICATION_CHANNEL_ID
        val title = HandyAppEnvironment.TITLE
        val cancel = "CANCEL"
        // This PendingIntent can be used to cancel the worker
        val intent = WorkManager.getInstance(ctx)
            .createCancelPendingIntent(getId())


        val notification = NotificationCompat.Builder(ctx, id)
            .setContentTitle(title)
            .setTicker(title)
            .setContentText(progress)
            .setSmallIcon(R.drawable.ic_settings)
            .setOngoing(true)
            // Add the cancel action to the notification which can
            // be used to cancel the worker
            .addAction(android.R.drawable.ic_delete, cancel, intent)
            .build()

        return ForegroundInfo(HandyAppEnvironment.NOTIFICATION_NUMBER_ID, notification)
    }

}