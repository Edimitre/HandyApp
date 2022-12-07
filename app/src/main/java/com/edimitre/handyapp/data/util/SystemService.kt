package com.edimitre.handyapp.data.util


import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.HandyAppEnvironment.TAG
import com.edimitre.handyapp.R
import com.edimitre.handyapp.activity.MainActivity
import com.edimitre.handyapp.data.worker.BackUpDBWorker
import com.edimitre.handyapp.data.worker.ImportDBWorker
import com.edimitre.handyapp.data.worker.NotificationWorker
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class SystemService(private val context: Context) {


    @Inject
    lateinit var auth: FirebaseAuth


    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel

            val mChannel = NotificationChannel(
                HandyAppEnvironment.NOTIFICATION_CHANNEL_ID,
                HandyAppEnvironment.NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH
            )

            mChannel.description = HandyAppEnvironment.NOTIFICATION_CHANNEL_ID

            mChannel.enableLights(true)
            mChannel.lightColor = Color.RED
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            // register in system
            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(mChannel)
        }

    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun setAlarm(alarmTime: Long) {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC,
                alarmTime,
                pendingIntent
            )

        } else {
            alarmManager.setExact(
                AlarmManager.RTC,
                alarmTime,
                pendingIntent
            )

        }
    }

    fun cancelAllAlarms() {

        val i = Intent(context, ReminderReceiver::class.java)

        @SuppressLint("UnspecifiedImmutableFlag")
        val pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.cancel(pi)
    }

    fun notify(title: String?, message: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val mainActivity = Intent(context, MainActivity::class.java)

            @SuppressLint("UnspecifiedImmutableFlag")
            val pi = PendingIntent.getActivity(
                context,
                HandyAppEnvironment.NOTIFICATION_NUMBER_ID,
                mainActivity,
                PendingIntent.FLAG_IMMUTABLE
            )


            val mBuilder: NotificationCompat.Builder =
                NotificationCompat.Builder(context, HandyAppEnvironment.NOTIFICATION_CHANNEL_ID)

            mBuilder.setSmallIcon(R.drawable.ic_reminder)
            mBuilder.setContentIntent(pi)
            mBuilder.setContentTitle(title)
            mBuilder.setContentText(message)
            mBuilder.priority = NotificationCompat.PRIORITY_HIGH
            mBuilder.setAutoCancel(true)

            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
            notificationManager!!.notify(
                HandyAppEnvironment.NOTIFICATION_NUMBER_ID,
                mBuilder.build()
            )

        }

    }

    fun startBackupWorker() {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()


        val backUpwork = OneTimeWorkRequest.Builder(BackUpDBWorker::class.java)
            .setConstraints(constraints)
            .addTag("back_up_work")
            .build()


        val workManager = WorkManager.getInstance(context)
        workManager.enqueue(backUpwork)

    }

    fun removeBackupWorker() {

        WorkManager.getInstance(context).cancelAllWorkByTag("back_up_work")

    }

    fun startImportWorker() {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()


        val importWork = OneTimeWorkRequest.Builder(ImportDBWorker::class.java)
            .setConstraints(constraints)
            .addTag("import_work")
            .build()
        val workManager = WorkManager.getInstance(context)

        workManager.enqueue(importWork)

    }

    fun startNotificationWorker() {

        val workManager = WorkManager.getInstance(context)

        val notificationWorker = PeriodicWorkRequest.Builder(
            NotificationWorker::class.java,
            4,
            TimeUnit.HOURS,
        )
            .setInitialDelay(1, TimeUnit.MINUTES)
            .addTag("notification_worker")
            .build()
        workManager.enqueueUniquePeriodicWork(
            "notification_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            notificationWorker
        )

        Log.e(TAG, "notification work scheduled")
    }

    private fun addOneDayToTimeInMillis(millis: Long): Long {

        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        cal.add(Calendar.DAY_OF_YEAR, 1)

        return cal.timeInMillis
    }


}