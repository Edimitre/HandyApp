package com.edimitre.handyapp.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.edimitre.handyapp.data.model.Reminder
import com.edimitre.handyapp.data.model.firebase.BackUpDto
import com.edimitre.handyapp.data.room_database.HandyDb
import com.edimitre.handyapp.data.util.SystemService
import com.edimitre.handyapp.data.util.TimeUtils
import com.google.gson.Gson
import kotlinx.coroutines.delay


class ImportDBWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val ctx = context

    var systemService = SystemService(ctx)


    override suspend fun doWork(): Result {

        setProgress(workDataOf("isRunning" to true))
        delay(2000)

        val backUpData = inputData.getString("backup_data")
        val backUpDto = Gson().fromJson(backUpData, BackUpDto::class.java)

        systemService.setNotification("BACKUP DATA FOUND", 10, true)


        delay(2000)
        importDto(backUpDto)

        systemService.setNotification("IMPORT SUCCESS", 100, false)

        setProgress(workDataOf("isRunning" to false))

        return Result.success()
    }

    private suspend fun importDto(backUpDto: BackUpDto) {


        val shopDao = HandyDb.getInstance(ctx).getShopExpenseDao()
        val noteDao = HandyDb.getInstance(ctx).getReminderNotesDao()
        val newsDao = HandyDb.getInstance(ctx).getNewsDao()
        val workDayDao = HandyDb.getInstance(ctx).getWorkDayDao()

        systemService.setNotification("STARTING IMPORT", 20, true)


        if (backUpDto.shopList.isNotEmpty()) {

            backUpDto.shopList.forEach { shop ->
                shopDao.saveOrUpdateShop(shop)

            }


            systemService.setNotification("IMPORTED SHOPS", 30, true)
            delay(1000)

        }
        if (backUpDto.expenseList.isNotEmpty()) {
            backUpDto.expenseList.forEach { expense ->
                shopDao.saveOrUpdateExpense(expense)
            }


            systemService.setNotification("IMPORTED EXPENSES", 40, true)

            delay(1000)
        }

        if (backUpDto.notesList.isNotEmpty()) {
            backUpDto.notesList.forEach { note ->
                noteDao.save(note)
            }

            systemService.setNotification("IMPORTED NOTES", 50, true)

            delay(1000)

        }

        if (backUpDto.reminderList.isNotEmpty()) {
            backUpDto.reminderList.forEach { reminder ->
                noteDao.saveReminder(reminder)
                activateReminder(reminder)
            }


            systemService.setNotification("IMPORTED REMINDERS", 60, true)

            delay(1000)

        }


        if (backUpDto.likedNewsList.isNotEmpty()) {

            backUpDto.likedNewsList.forEach { news ->
                newsDao.insert(news)

            }

            systemService.setNotification("IMPORTED NEWS", 70, true)
            delay(1000)
        }

        if (backUpDto.workDaysList.isNotEmpty()) {

            backUpDto.workDaysList.forEach { workday ->
                workDayDao.save(workday)

            }

            systemService.setNotification("IMPORTED WORKDAYS", 90, true)

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

}