package com.edimitre.handyapp.data.util

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.R
import com.edimitre.handyapp.activity.AlarmActivity
import com.edimitre.handyapp.activity.CigaretteReminderActivity
import com.edimitre.handyapp.data.dao.CigarDao
import com.edimitre.handyapp.data.model.Cigar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ShowCigarAlarmService : Service() {


    @Inject
    lateinit var cigarDao: CigarDao

    var cigar: Cigar? = null

    @Inject
    lateinit var systemService: SystemService

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        showCigarAlarmNotification()

        return START_STICKY
    }


    private fun showCigarAlarmNotification() {

        try {

            val activityIntent = Intent(applicationContext, CigaretteReminderActivity::class.java)
            val openActivity = PendingIntent.getActivity(
                applicationContext,
                HandyAppEnvironment.NOTIFICATION_NUMBER_ID,
                activityIntent,
                PendingIntent.FLAG_IMMUTABLE
            )


            val fullScreenIntent = Intent(applicationContext, AlarmActivity::class.java)
            val fullScreenPendingIntent = PendingIntent.getActivity(
                applicationContext,
                101,
                fullScreenIntent,
                PendingIntent.FLAG_IMMUTABLE
            )


            val isWinIntent = Intent(applicationContext, NotificationActionReceiver::class.java)
            isWinIntent.putExtra("CIGAR_ID", 1)
            isWinIntent.putExtra("IS_WIN", true)
            val isWinClick = PendingIntent.getBroadcast(
                applicationContext, 102, isWinIntent,
                PendingIntent.FLAG_IMMUTABLE
            )


            val bundleFalse = Bundle()
            bundleFalse.putInt("CIGAR_ID", 1)
            bundleFalse.putBoolean("IS_WIN", false)

            val isNotWinIntent = Intent(applicationContext, NotificationActionReceiver::class.java)
            isWinIntent.putExtra("CIGAR_ID", 1)
            isWinIntent.putExtra("IS_WIN", false)
            val isNotWin = PendingIntent.getBroadcast(
                applicationContext, 103, isNotWinIntent,
                PendingIntent.FLAG_IMMUTABLE
            )


            val mBuilder: NotificationCompat.Builder =
                NotificationCompat.Builder(
                    applicationContext,
                    HandyAppEnvironment.NOTIFICATION_CHANNEL_ID
                )
                    .setSmallIcon(R.drawable.ic_reminder)
                    .setContentTitle("title")
                    .setContentText("message")
                    .setContentIntent(openActivity)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setFullScreenIntent(fullScreenPendingIntent, true)
                    .setAutoCancel(true)
                    .setOngoing(false)
                    .addAction(R.drawable.ic_check, "Win", isWinClick)
                    .addAction(R.drawable.ic_close, "Lose", isNotWin)






            startForeground(123, mBuilder.build())


        } catch (e: Exception) {
            e.printStackTrace()
        }


    }


}