package com.edimitre.handyapp.data.util


import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.view.ContentInfoCompat.Flags
import androidx.work.*
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.HandyAppEnvironment.TAG
import com.edimitre.handyapp.R
import com.edimitre.handyapp.activity.MainActivity
import com.edimitre.handyapp.data.scraper.BotaAlScrapper
import com.edimitre.handyapp.data.scraper.JoqScrapper
import com.edimitre.handyapp.data.scraper.LapsiScrapper
import com.edimitre.handyapp.data.scraper.SyriScrapper
import com.edimitre.handyapp.data.worker.BackUpDBWorker
import com.edimitre.handyapp.data.worker.ImportDBWorker
import com.edimitre.handyapp.data.worker.NotificationWorker
import com.edimitre.handyapp.data.worker.ReminderWorker
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
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getBroadcast(context, 0, intent, FLAG_IMMUTABLE)
        } else {
            PendingIntent.getBroadcast(context, 0, intent, FLAG_IMMUTABLE)

        }
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

    @RequiresApi(Build.VERSION_CODES.M)
    fun cancelAllAlarms() {

        val i = Intent(context, ReminderReceiver::class.java)

        @SuppressLint("UnspecifiedImmutableFlag")
        val pi = PendingIntent.getBroadcast(context, 0, i, FLAG_IMMUTABLE)
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
                FLAG_IMMUTABLE
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

        val backupWorker = PeriodicWorkRequest.Builder(
            BackUpDBWorker::class.java,
            6,
            TimeUnit.HOURS,
        )
//            .setInitialDelay(60, TimeUnit.MINUTES)
            .addTag("backup_worker")
            .setConstraints(constraints)
            .build()


        val workManager = WorkManager.getInstance(context)
        workManager.enqueueUniquePeriodicWork(
            "backup_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            backupWorker
        )


    }

    fun stopBackupWorker() {

        WorkManager.getInstance(context).cancelAllWorkByTag("backup_worker")

    }

    fun startImportWorker(importData: String) {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val data: Data = Data.Builder().putString("backup_data", importData).build()

        val importWork = OneTimeWorkRequest.Builder(ImportDBWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data)
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

    }

    fun startReminderWorker() {

        val reminderWork = OneTimeWorkRequest.Builder(ReminderWorker::class.java)
            .addTag("reminder_work")
            .build()


        val workManager = WorkManager.getInstance(context)
        workManager.enqueue(reminderWork)

    }

    private fun addOneDayToTimeInMillis(millis: Long): Long {

        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        cal.add(Calendar.DAY_OF_YEAR, 1)

        return cal.timeInMillis
    }




    fun startScrapBotaAl() {

        val scrapWork = OneTimeWorkRequest.Builder(BotaAlScrapper::class.java)
            .addTag("scrap_botaal_work")
            .build()


        val workManager = WorkManager.getInstance(context)
        workManager.enqueue(scrapWork)

    }

    fun startScrapJoqAl() {

        val scrapWork = OneTimeWorkRequest.Builder(JoqScrapper::class.java)
            .addTag("scrap_joq_work")
            .build()


        val workManager = WorkManager.getInstance(context)
        workManager.enqueue(scrapWork)

    }

    fun startScrapLapsiAl() {

        val scrapWork = OneTimeWorkRequest.Builder(LapsiScrapper::class.java)
            .addTag("scrap_lapsi_work")
            .build()


        val workManager = WorkManager.getInstance(context)
        workManager.enqueue(scrapWork)

    }

    fun startScrapSyriNet() {

        val scrapWork = OneTimeWorkRequest.Builder(SyriScrapper::class.java)
            .addTag("scrap_syri_work")
            .build()


        val workManager = WorkManager.getInstance(context)
        workManager.enqueue(scrapWork)

    }

}