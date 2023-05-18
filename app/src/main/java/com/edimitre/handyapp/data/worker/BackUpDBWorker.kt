package com.edimitre.handyapp.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.edimitre.handyapp.data.model.firebase.BackUpDto
import com.edimitre.handyapp.data.room_database.HandyDb
import com.edimitre.handyapp.data.service.FileService
import com.edimitre.handyapp.data.util.SystemService
import com.edimitre.handyapp.data.util.TimeUtils
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.delay


class BackUpDBWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {


    private val ctx = context

    private var backUpDb = Firebase.firestore

    private var failed = false

    var systemService = SystemService(ctx)


    override suspend fun doWork(): Result {

        setProgress(workDataOf("isRunning" to true))

        delay(1000)

        systemService.setNotification("STARTING BACKUP", 0, true)


        delay(1000)
        val backUpDto = BackUpDto()


        val auth = HandyDb.getInstance(ctx).getAuthDao().getAuthModelOnCoroutine()


        val shopList = HandyDb.getInstance(ctx).getShopExpenseDao().getAllShopsForBackUp()

        val expenseList =
            HandyDb.getInstance(ctx).getShopExpenseDao().getAllExpensesForBackup()

        val reminderList =
            HandyDb.getInstance(ctx).getReminderNotesDao().getAllRemindersForBackUp()

        val notesList =
            HandyDb.getInstance(ctx).getReminderNotesDao().getAllNotesForBackUp()

        val likedNewsList = HandyDb.getInstance(ctx).getNewsDao().getAllLikedNewsForBackUp()

        val workDaysList = HandyDb.getInstance(ctx).getWorkDayDao().getAllWorkDaysForBackUp()

        val memeTemplates = HandyDb.getInstance(ctx).getMemeTemplateDao().getAllTemplatesForBackUp()

        delay(1000)
        when {
            auth != null -> {

                systemService.setNotification("WORKING...", 30, true)


                delay(1000)
                // set dto uid
                backUpDto.uid = auth.uid

                // set dto shop list
                backUpDto.shopList = shopList!!

                // set dto expense list
                backUpDto.expenseList = expenseList


                // set dto reminder list
                backUpDto.reminderList = reminderList!!

                backUpDto.notesList = notesList!!

                backUpDto.likedNewsList = likedNewsList!!

                backUpDto.workDaysList = workDaysList!!

                backUpDto.memeTemplatesList = memeTemplates!!


                val fileService = FileService()
                val filesAsBytesList = fileService.getFilesAsBytesList()
                backUpDto.filesAsBytesList = filesAsBytesList

                backUpDto.backUpDate = TimeUtils().getTimeInMilliSeconds(
                    TimeUtils().getCurrentYear(),
                    TimeUtils().getCurrentMonth(),
                    TimeUtils().getCurrentDate(),
                    TimeUtils().getCurrentHour(),
                    TimeUtils().getCurrentMinute()
                )

                systemService.setNotification("DATA GATHERED", 50, true)

                delay(2000)
                val backup = getMapFromDto(backUpDto)

                backUpDb.collection("handy_app").document(auth.uid)
                    .set(backup, SetOptions.merge())
                    .addOnSuccessListener {


                        failed = false
                    }
                    .addOnFailureListener {

                        failed = true

                    }
            }
        }


        return if (!failed) {

            systemService.setNotification("BACKUP SUCCESS", 100, false)
            delay(1000)
            setProgress(workDataOf("isRunning" to false))
            Result.success()
        } else {


            systemService.setNotification("BACKUP FAILED", 100, false)

            delay(1000)
            setProgress(workDataOf("isRunning" to false))
            Result.failure()
        }


    }


    private fun getMapFromDto(backUpDto: BackUpDto): Map<String, Any> {

        return mapOf(
            "shopList" to Gson().toJson(backUpDto.shopList),
            "expenseList" to Gson().toJson(backUpDto.expenseList),
            "reminderList" to Gson().toJson(backUpDto.reminderList),
            "notesList" to Gson().toJson(backUpDto.notesList),
            "likedNewsList" to Gson().toJson(backUpDto.likedNewsList),
            "workDaysList" to Gson().toJson(backUpDto.workDaysList),
            "filesAsBytesList" to Gson().toJson(backUpDto.filesAsBytesList),
            "memeTemplatesList" to Gson().toJson(backUpDto.memeTemplatesList),
            "backupDate" to backUpDto.backUpDate
        )


    }


}