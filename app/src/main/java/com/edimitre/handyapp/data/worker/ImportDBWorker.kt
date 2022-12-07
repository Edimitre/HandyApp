package com.edimitre.handyapp.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.edimitre.handyapp.HandyAppEnvironment.TAG
import com.edimitre.handyapp.data.model.Reminder
import com.edimitre.handyapp.data.model.firebase.BackUpDto
import com.edimitre.handyapp.data.room_database.HandyDb
import com.edimitre.handyapp.data.util.SystemService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ImportDBWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val ctx = context

    var systemService = SystemService(ctx)

    override suspend fun doWork(): Result {

        withContext(Dispatchers.Default) {
            launch {
                val backUpData = inputData.getString("backup_data")
                val backUpDto = Gson().fromJson(backUpData, BackUpDto::class.java)
                importDto(backUpDto)
            }

        }

        return Result.success()
    }

    private suspend fun importDto(backUpDto: BackUpDto) {


        val shopDao = HandyDb.getInstance(ctx).getShopExpenseDao()
        val noteDao = HandyDb.getInstance(ctx).getReminderNotesDao()

        Log.e(TAG, "starting")

        if (backUpDto.shopList.isNotEmpty()) {
            backUpDto.shopList.forEach { shop ->
                shopDao.saveOrUpdateShop(shop)
            }
        }
        if (backUpDto.expenseList.isNotEmpty()) {
            backUpDto.expenseList.forEach { expense ->
                shopDao.saveOrUpdateExpense(expense)
            }
        }

        if (backUpDto.notesList.isNotEmpty()) {
            backUpDto.notesList.forEach { note ->
                noteDao.save(note)
            }

        }

        if (backUpDto.reminderList.isNotEmpty()) {
            backUpDto.reminderList.forEach { reminder ->
                noteDao.saveReminder(reminder)
            }

            val reminder = noteDao.getFirstReminderOnCoroutine()
            activateReminder(reminder!!)
        }

    }

    private fun activateReminder(reminder: Reminder) {

        systemService.cancelAllAlarms()

        systemService.setAlarm(reminder.alarmTimeInMillis)
    }
}