package com.edimitre.handyapp.data.worker

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.R
import com.edimitre.handyapp.data.model.firebase.BackUpDto
import com.edimitre.handyapp.data.room_database.HandyDb
import com.edimitre.handyapp.data.util.SystemService
import com.edimitre.handyapp.data.util.TimeUtils
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class BackUpDBWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {


    private val ctx = context

    private var backUpDb = Firebase.firestore

    private var failed = false

    private lateinit var notifBuilder: NotificationCompat.Builder


    override suspend fun doWork(): Result {


        setForeground(ForegroundInfo(HandyAppEnvironment.NOTIFICATION_NUMBER_ID, getNotification("STARTING BACKUP", 0, true)))
        setProgress(workDataOf("isRunning" to true))

        delay(2000)
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

        delay(2000)
        when {
            auth != null -> {

                setForeground(ForegroundInfo(HandyAppEnvironment.NOTIFICATION_NUMBER_ID, getNotification("DATA GATHERED", 30, true)))

                delay(2000)
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

                backUpDto.backUpDate = TimeUtils().getTimeInMilliSeconds(
                    TimeUtils().getCurrentYear(),
                    TimeUtils().getCurrentMonth(),
                    TimeUtils().getCurrentDate(),
                    TimeUtils().getCurrentHour(),
                    TimeUtils().getCurrentMinute()
                )

                setForeground(ForegroundInfo(HandyAppEnvironment.NOTIFICATION_NUMBER_ID, getNotification("DATA GATHERED", 50, true)))

                delay(2000)
                val backup = getMapFromDto(backUpDto)

                backUpDb.collection("handy_app").document(auth.uid)
                    .set(backup, SetOptions.merge())
                    .addOnSuccessListener {


                        failed = false
                    }
                    .addOnFailureListener { error ->

                        failed = true

                    }
            }
        }


        return if(!failed){
            setForeground(ForegroundInfo(HandyAppEnvironment.NOTIFICATION_NUMBER_ID, getNotification("SUCCESS", 100, false)))
            delay(2000)
            setProgress(workDataOf("isRunning" to false))
            Result.success()
        }else{
            setForeground(ForegroundInfo(HandyAppEnvironment.NOTIFICATION_NUMBER_ID, getNotification("FAILED", 100, false)))
            delay(2000)
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
            "backupDate" to backUpDto.backUpDate
        )


    }


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