package com.edimitre.handyapp.data.worker

import android.app.Notification
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.*
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


    private lateinit var notifBuilder: NotificationCompat.Builder


    override suspend fun doWork(): Result {

        delay(2000)

        val backUpData = inputData.getString("backup_data")
        val backUpDto = Gson().fromJson(backUpData, BackUpDto::class.java)

        setForeground(ForegroundInfo(HandyAppEnvironment.NOTIFICATION_NUMBER_ID, getNotification("BACKUP DATA FOUND", 10, true)))
        setProgress(workDataOf("isRunning" to true))

        delay(2000)
        importDto(backUpDto)

        setForeground(ForegroundInfo(HandyAppEnvironment.NOTIFICATION_NUMBER_ID, getNotification("IMPORT FINISHED", 100, false)))
        setProgress(workDataOf("isRunning" to false))

        return Result.success()
    }

    private suspend fun importDto(backUpDto: BackUpDto) {


        val shopDao = HandyDb.getInstance(ctx).getShopExpenseDao()
        val noteDao = HandyDb.getInstance(ctx).getReminderNotesDao()
        val newsDao = HandyDb.getInstance(ctx).getNewsDao()
        val workDayDao = HandyDb.getInstance(ctx).getWorkDayDao()

        setForeground(ForegroundInfo(HandyAppEnvironment.NOTIFICATION_NUMBER_ID, getNotification("STARTING IMPORT", 20, true)))

        if (backUpDto.shopList.isNotEmpty()) {

            backUpDto.shopList.forEach { shop ->
                shopDao.saveOrUpdateShop(shop)

            }


            setForeground(ForegroundInfo(HandyAppEnvironment.NOTIFICATION_NUMBER_ID, getNotification("IMPORTED SHOPS", 30, true)))

            delay(1000)

        }
        if (backUpDto.expenseList.isNotEmpty()) {
            backUpDto.expenseList.forEach { expense ->
                shopDao.saveOrUpdateExpense(expense)
            }


            setForeground(ForegroundInfo(HandyAppEnvironment.NOTIFICATION_NUMBER_ID, getNotification("IMPORTED EXPENSES", 40, true)))

            delay(1000)
        }

        if (backUpDto.notesList.isNotEmpty()) {
            backUpDto.notesList.forEach { note ->
                noteDao.save(note)
            }

            setForeground(ForegroundInfo(HandyAppEnvironment.NOTIFICATION_NUMBER_ID, getNotification("IMPORTED NOTES", 50, true)))

            delay(1000)

        }

        if (backUpDto.reminderList.isNotEmpty()) {
            backUpDto.reminderList.forEach { reminder ->
                noteDao.saveReminder(reminder)
                activateReminder(reminder)
            }


            setForeground(ForegroundInfo(HandyAppEnvironment.NOTIFICATION_NUMBER_ID, getNotification("IMPORTED REMINDERS", 60, true)))

            delay(1000)

        }


        if (backUpDto.likedNewsList.isNotEmpty()) {

            backUpDto.likedNewsList.forEach { news ->
                newsDao.insert(news)

            }

            setForeground(ForegroundInfo(HandyAppEnvironment.NOTIFICATION_NUMBER_ID, getNotification("IMPORTED NEWS", 70, true)))

            delay(1000)
        }

        if (backUpDto.workDaysList.isNotEmpty()) {

            backUpDto.workDaysList.forEach { workday ->
                workDayDao.save(workday)

            }

            setForeground(ForegroundInfo(HandyAppEnvironment.NOTIFICATION_NUMBER_ID, getNotification("IMPORTED WORKDAYS", 90, true)))

            delay(1000)
        }


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

    private fun getNotification(text:String, progress:Int, onGoing:Boolean): Notification {

        val maxProgress = 100

        notifBuilder =
            NotificationCompat.Builder(ctx, HandyAppEnvironment.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_reminder)
                .setContentTitle(HandyAppEnvironment.TITLE)
                .setContentText(text)
                .setOngoing(onGoing)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setProgress(maxProgress, progress, false)
                .setOnlyAlertOnce(true)

        return notifBuilder.build()
    }
}