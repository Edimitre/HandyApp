package com.edimitre.handyapp.data.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.data.model.firebase.AuthModel
import com.edimitre.handyapp.data.model.firebase.BackUpDto
import com.edimitre.handyapp.data.room_database.HandyDb
import com.edimitre.handyapp.data.util.SystemService
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

class FirebaseDBWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    private var backUpDb = Firebase.firestore

    private var ctx = context

    private var authModel: AuthModel? = null

    private var backUpDto:BackUpDto? = null


    override fun doWork(): Result {

        loadAuth()
        prepareBackup()

        val systemService = SystemService(ctx)


                val sBackup = getMapFromDto(backUpDto!!)
                backUpDb.collection("handy_app_backup").document(authModel!!.uid)
                    .set(sBackup, SetOptions.merge())
                    .addOnSuccessListener {


                        systemService.notify(HandyAppEnvironment.TITLE, "back up success")

                    }
                    .addOnFailureListener { error ->
                        systemService.notify(HandyAppEnvironment.TITLE, "back up worker failure : error -> ${error.localizedMessage}")

                    }



        return Result.success()
    }

    private fun loadAuth(){

        val thread = Thread{
            authModel = HandyDb.getInstance(ctx).getAuthDao().getMainUserForBackup()
        }
        thread.start()
        thread.join()

    }


    private fun prepareBackup() {
        backUpDto = BackUpDto()
        val thread = Thread {


            val shopList = HandyDb.getInstance(ctx).getShopExpenseDao().getAllShopsForBackUp()

            val expenseList = HandyDb.getInstance(ctx).getShopExpenseDao().getAllExpensesForBackup()

            val reminderList = HandyDb.getInstance(ctx).getReminderNotesDao().getAllRemindersForBackUp()

            val notesList = HandyDb.getInstance(ctx).getReminderNotesDao().getAllNotesForBackUp()


            when {
                authModel != null -> {
                    // set dto uid
                    backUpDto!!.uid = authModel!!.uid

                    // set dto shop list
                    backUpDto!!.shopList = shopList!!

                    // set dto expense list
                    backUpDto!!.expenseList = expenseList


                    // set dto reminder list
                    backUpDto!!.reminderList = reminderList!!

                    backUpDto!!.notesList = notesList!!

                }
            }

        }
        thread.start()
        thread.join()
    }


    private fun getMapFromDto(backUpDto: BackUpDto): Map<String, Any> {

        return mapOf(
            "shopList" to Gson().toJson(backUpDto.shopList),
            "expenseList" to Gson().toJson(backUpDto.expenseList),
            "reminderList" to Gson().toJson(backUpDto.reminderList),
            "notesList" to Gson().toJson(backUpDto.notesList),
        )


    }


}