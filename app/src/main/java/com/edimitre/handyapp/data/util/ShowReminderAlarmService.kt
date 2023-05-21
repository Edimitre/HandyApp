package com.edimitre.handyapp.data.util

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.R
import com.edimitre.handyapp.activity.ReminderAlarmActivity
import com.edimitre.handyapp.activity.ReminderNotesActivity
import com.edimitre.handyapp.data.dao.ReminderNotesDao
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

        systemService.startVibrator()
        systemService.startRingtone()

        showReminderAlarmNotification()

        return START_STICKY
    }


    private fun showReminderAlarmNotification() {


        runBlocking {
            reminder = reminderNotesDao.getFirstReminderOnCoroutine()
        }


//        Log.e(TAG, "on reminder alarm service outside run blocking ${reminder?.id}", )

        try {


            val activityIntent = Intent(applicationContext, ReminderNotesActivity::class.java)
            val openActivity = PendingIntent.getActivity(
                applicationContext,
                1,
                activityIntent,
                PendingIntent.FLAG_IMMUTABLE
            )


            val fullScreenIntent = Intent(applicationContext, ReminderAlarmActivity::class.java)
            val fullScreenPendingIntent = PendingIntent.getActivity(
                applicationContext,
                2,
                fullScreenIntent,
                PendingIntent.FLAG_IMMUTABLE
            )


            val okIntent =
                Intent(applicationContext, ReminderNotificationActionReceiver::class.java)
            val isWinClick = PendingIntent.getBroadcast(
                applicationContext, 3, okIntent,
                PendingIntent.FLAG_IMMUTABLE
            )


            val mBuilder: NotificationCompat.Builder =
                NotificationCompat.Builder(
                    applicationContext,
                    HandyAppEnvironment.NOTIFICATION_ALARM_CHANNEL_ID
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
                    .addAction(R.drawable.ic_check, "Ok", isWinClick)

            startForeground(HandyAppEnvironment.NOTIFICATION_ALARM_NUMBER_ID, mBuilder.build())

        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    override fun onDestroy() {
        super.onDestroy()

        systemService.stopVibrator()
        systemService.stopRingtone()

    }

}