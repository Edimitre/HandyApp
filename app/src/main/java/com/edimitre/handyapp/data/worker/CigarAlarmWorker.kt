package com.edimitre.handyapp.data.worker

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.activity.AlarmActivity
import com.edimitre.handyapp.activity.CigaretteReminderActivity
import com.edimitre.handyapp.data.util.SharedPrefUtil
import com.edimitre.handyapp.data.util.SystemService


class CigarAlarmWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val ctx = context

    var systemService = SystemService(ctx)

    var sharedPref = SharedPrefUtil(ctx)
    override suspend fun doWork(): Result {



//        setForeground(systemService.notifyCigarAlarm(HandyAppEnvironment.TITLE, "on cigar alarm", 1))
//        val cigarDao = HandyDb.getInstance(ctx).getCigarDao()
//
//        val cigar: Cigar? = cigarDao.getFirstCigarOnCoroutine()
//
//        if (cigar != null) {
//
//            cigar.isActive = false
//            cigarDao.saveCigar(cigar)
//
//            Log.e(TAG, "on cigar worker id cigar ${cigar.id}", )
//
//            systemService.notifyCigarAlarm("cigar alarm : ", TimeUtils().getHourStringFromDateInMillis(cigar.alarmInMillis).replace("/", ":"), cigar.id)
//
//            sharedPref.setCigarId(cigar.id)
//
//            val nextCigar: Cigar? = cigarDao.getFirstCigarOnCoroutine()
//
//            if (nextCigar != null) {
//                systemService.cancelAllCigarAlarms()
//
//                systemService.setCigarAlarm(nextCigar.alarmInMillis)
//            }
//        }

        return Result.success()
    }



    fun test(){


        try {

            val activityIntent = Intent(applicationContext, CigaretteReminderActivity::class.java)
            val openActivity = PendingIntent.getActivity(applicationContext, HandyAppEnvironment.NOTIFICATION_NUMBER_ID, activityIntent,
                PendingIntent.FLAG_IMMUTABLE
            )


            val fullScreenIntent = Intent(applicationContext, AlarmActivity::class.java)
            val fullScreenPendingIntent = PendingIntent.getActivity(
                applicationContext, 0,
                fullScreenIntent, PendingIntent.FLAG_IMMUTABLE
            )

            val mBuilder: NotificationCompat.Builder =
                NotificationCompat.Builder(applicationContext, HandyAppEnvironment.NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(com.edimitre.handyapp.R.drawable.ic_reminder)
                    .setContentTitle("title")
                    .setContentText("message")
                    .setContentIntent(openActivity)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setFullScreenIntent(fullScreenPendingIntent, true)
                    .setAutoCancel(true)
                    .setOngoing(false)






//            startForeground(HandyAppEnvironment.NOTIFICATION_NUMBER_ID, mBuilder.build())



        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

}