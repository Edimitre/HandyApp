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
import com.edimitre.handyapp.activity.ReminderAlarmActivity
import com.edimitre.handyapp.activity.ReminderNotesActivity
import com.edimitre.handyapp.data.dao.CigarDao
import com.edimitre.handyapp.data.dao.ReminderNotesDao
import com.edimitre.handyapp.data.model.Cigar
import com.edimitre.handyapp.data.model.Reminder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@AndroidEntryPoint
class ShowReminderAlarmService : Service() {


    @Inject
    lateinit var reminderNotesDao: ReminderNotesDao

    var reminder: Reminder? = null


    @Inject
    lateinit var systemService: SystemService

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        showReminderAlarmNotification()

        systemService.startVibrator()
        systemService.startRingtone()

        runBlocking {
            reminder = reminderNotesDao.getFirstReminderOnCoroutine()
        }

        return START_STICKY
    }


    private fun showReminderAlarmNotification() {

        try {

            val activityIntent = Intent(applicationContext, ReminderNotesActivity::class.java)
            val openActivity = PendingIntent.getActivity(
                applicationContext,
                HandyAppEnvironment.NOTIFICATION_NUMBER_ID,
                activityIntent,
                PendingIntent.FLAG_IMMUTABLE
            )


            val fullScreenIntent = Intent(applicationContext, ReminderAlarmActivity::class.java)
            val fullScreenPendingIntent = PendingIntent.getActivity(
                applicationContext,
                112,
                fullScreenIntent,
                PendingIntent.FLAG_IMMUTABLE
            )


            val okIntent = Intent(applicationContext, ReminderNotificationActionReceiver::class.java)
            val isWinClick = PendingIntent.getBroadcast(
                applicationContext, 110, okIntent,
                PendingIntent.FLAG_IMMUTABLE
            )


            val mBuilder: NotificationCompat.Builder =
                NotificationCompat.Builder(
                    applicationContext,
                    HandyAppEnvironment.NOTIFICATION_CHANNEL_ID
                )
                    .setSmallIcon(R.drawable.ic_reminder)
                    .setContentTitle(HandyAppEnvironment.TITLE)
                    .setContentText("reminding you \n${reminder?.description}")
                    .setContentIntent(openActivity)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setFullScreenIntent(fullScreenPendingIntent, true)
                    .setAutoCancel(true)
                    .setOngoing(false)
                    .addAction(R.drawable.ic_check, "Win", isWinClick)







            startForeground(111, mBuilder.build())


        } catch (e: Exception) {
            e.printStackTrace()
        }


    }


}