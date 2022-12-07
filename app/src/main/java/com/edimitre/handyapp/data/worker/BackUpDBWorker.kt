package com.edimitre.handyapp.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.data.model.firebase.BackUpDto
import com.edimitre.handyapp.data.room_database.HandyDb
import com.edimitre.handyapp.data.util.SystemService
import com.edimitre.handyapp.data.util.TimeUtils
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class BackUpDBWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {


    private val ctx = context

    private var backUpDb = Firebase.firestore

    override suspend fun doWork(): Result {

        val backUpDto = BackUpDto()

        val systemService = SystemService(ctx)

        withContext(Dispatchers.Default) {

            launch {
                val auth = HandyDb.getInstance(ctx).getAuthDao().getAuthModelOnCoroutine()


                val shopList = HandyDb.getInstance(ctx).getShopExpenseDao().getAllShopsForBackUp()

                val expenseList =
                    HandyDb.getInstance(ctx).getShopExpenseDao().getAllExpensesForBackup()

                val reminderList =
                    HandyDb.getInstance(ctx).getReminderNotesDao().getAllRemindersForBackUp()

                val notesList =
                    HandyDb.getInstance(ctx).getReminderNotesDao().getAllNotesForBackUp()


                when {
                    auth != null -> {
                        // set dto uid
                        backUpDto.uid = auth.uid

                        // set dto shop list
                        backUpDto.shopList = shopList!!

                        // set dto expense list
                        backUpDto.expenseList = expenseList


                        // set dto reminder list
                        backUpDto.reminderList = reminderList!!

                        backUpDto.notesList = notesList!!

                        backUpDto.backUpDate = TimeUtils().getTimeInMilliSeconds(
                            TimeUtils().getCurrentYear(),
                            TimeUtils().getCurrentMonth(),
                            TimeUtils().getCurrentDate(),
                            TimeUtils().getCurrentHour(),
                            TimeUtils().getCurrentMinute()
                        )

                        val backup = getMapFromDto(backUpDto)

                        backUpDb.collection("handy_app").document(auth.uid)
                            .set(backup, SetOptions.merge())
                            .addOnSuccessListener {
                                systemService.notify(HandyAppEnvironment.TITLE, "back up success")
                            }
                            .addOnFailureListener { error ->
                                systemService.notify(
                                    HandyAppEnvironment.TITLE,
                                    "back up worker failure : error -> ${error.localizedMessage}"
                                )

                            }
                    }
                }

            }
        }





        return Result.success()
    }


    private fun getMapFromDto(backUpDto: BackUpDto): Map<String, Any> {

        return mapOf(
            "shopList" to Gson().toJson(backUpDto.shopList),
            "expenseList" to Gson().toJson(backUpDto.expenseList),
            "reminderList" to Gson().toJson(backUpDto.reminderList),
            "notesList" to Gson().toJson(backUpDto.notesList),
            "backupDate" to backUpDto.backUpDate
        )


    }
}