package com.edimitre.handyapp.data.util


import android.annotation.SuppressLint
import android.app.*
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.R
import com.edimitre.handyapp.activity.CigaretteReminderActivity
import com.edimitre.handyapp.activity.MainActivity
import com.edimitre.handyapp.data.model.firebase.BackUpDto
import com.edimitre.handyapp.data.scraper.BotaAlScrapper
import com.edimitre.handyapp.data.scraper.JoqScrapper
import com.edimitre.handyapp.data.scraper.LapsiScrapper
import com.edimitre.handyapp.data.scraper.SyriScrapper
import com.edimitre.handyapp.data.service.FileService
import com.edimitre.handyapp.data.worker.*
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class SystemService(private val context: Context) {


    @Inject
    lateinit var auth: FirebaseAuth

    lateinit var mBuilder: NotificationCompat.Builder

    fun createNotificationChannel() {
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

    @SuppressLint("UnspecifiedImmutableFlag")
    fun setAlarm(alarmTime: Long) {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, FLAG_IMMUTABLE)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC,
            alarmTime,
            pendingIntent
        )

    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun setCigarAlarm(alarmTime: Long) {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, CigarAlarmReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, FLAG_IMMUTABLE)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC,
            alarmTime,
            pendingIntent
        )

    }

    fun cancelAllAlarms() {

        val i = Intent(context, ReminderReceiver::class.java)

        @SuppressLint("UnspecifiedImmutableFlag")
        val pi = PendingIntent.getBroadcast(context, 0, i, FLAG_IMMUTABLE)
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.cancel(pi)
    }

    fun cancelAllCigarAlarms() {

        val i = Intent(context, CigarAlarmReceiver::class.java)

        @SuppressLint("UnspecifiedImmutableFlag")
        val pi = PendingIntent.getBroadcast(context, 0, i, FLAG_IMMUTABLE)
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.cancel(pi)
    }

    fun notify(title: String?, message: String?) {

        val mainActivity = Intent(context, MainActivity::class.java)

        @SuppressLint("UnspecifiedImmutableFlag")
        val pi = PendingIntent.getActivity(
            context,
            HandyAppEnvironment.NOTIFICATION_NUMBER_ID,
            mainActivity,
            FLAG_IMMUTABLE
        )


        mBuilder = NotificationCompat.Builder(context, HandyAppEnvironment.NOTIFICATION_CHANNEL_ID)
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

    fun startOneTimeBackupWork(): UUID {

        val backUpWork = OneTimeWorkRequest.Builder(BackUpDBWorker::class.java)
            .addTag("backup_worker")
            .build()


        val workManager = WorkManager.getInstance(context)
        workManager.enqueue(backUpWork)

        return backUpWork.id
    }

    fun startCreateFileWorker(): UUID {

        val createFileWork = OneTimeWorkRequest.Builder(FileWorker::class.java)
            .addTag("file_worker")

            .build()

        val workManager = WorkManager.getInstance(context)
        workManager.enqueue(createFileWork)

        return createFileWork.id
    }

    fun startImportWorker(importData: String): UUID {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val backUpDto = Gson().fromJson(importData, BackUpDto::class.java)


        // because cant serialize files because they are more than 10240 bytes so we save them first and then remove from backup dto
        val fileService = FileService()
        fileService.createLocalFiles(backUpDto.filesAsBytesList)
        backUpDto.filesAsBytesList = emptyList()

        val data: Data = Data.Builder().putString("backup_data", Gson().toJson(backUpDto)).build()

        val importWork = OneTimeWorkRequest.Builder(ImportDBWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data)
            .addTag("import_work")
            .build()
        val workManager = WorkManager.getInstance(context)

        workManager.enqueue(importWork)

        return importWork.id
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

    fun stopNotificationWorker() {

        WorkManager.getInstance(context).cancelAllWorkByTag("notification_worker")

    }

    fun startReminderWorker() {

        val reminderWork = OneTimeWorkRequest.Builder(ReminderWorker::class.java)
            .addTag("reminder_work")
            .build()


        val workManager = WorkManager.getInstance(context)
        workManager.enqueue(reminderWork)

    }

    fun startCigarAlarmWorker() {

        val cigarAlarmWork = OneTimeWorkRequest.Builder(CigarAlarmWorker::class.java)
            .addTag("cigar_alarm_work")
            .build()


        val workManager = WorkManager.getInstance(context)
        workManager.enqueue(cigarAlarmWork)

    }

    fun setNotification(text:String, progress:Int, onGoing:Boolean): Notification {

        val maxProgress = 100

        mBuilder =
            NotificationCompat.Builder(context, HandyAppEnvironment.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_reminder)
                .setContentTitle(HandyAppEnvironment.TITLE)
                .setContentText(text)
                .setOngoing(onGoing)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setProgress(maxProgress, progress, false)
                .setOnlyAlertOnce(true)

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(HandyAppEnvironment.NOTIFICATION_NUMBER_ID, mBuilder.build())
        }
        return mBuilder.build()
    }

    fun notifyCigarAlarm(title: String?, message: String?,id:Int) {

        val activityIntent = Intent(context, CigaretteReminderActivity::class.java)


        @SuppressLint("UnspecifiedImmutableFlag")
        val openActivity = PendingIntent.getActivity(context, HandyAppEnvironment.NOTIFICATION_NUMBER_ID, activityIntent, FLAG_IMMUTABLE)


        val isWinIntent = Intent(context, NotificationActionReceiver::class.java)
        isWinIntent.putExtra("IS_WIN",true)
        isWinIntent.putExtra("CIGAR_ID", id.toString())
        val isWinClick = PendingIntent.getBroadcast(context, 2, isWinIntent, FLAG_IMMUTABLE)


        val isNotWinIntent = Intent(context, NotificationActionReceiver::class.java)
        isNotWinIntent.putExtra("IS_WIN",false)
        isNotWinIntent.putExtra("CIGAR_ID", id.toString())
        val isNotWin = PendingIntent.getBroadcast(context, 3, isNotWinIntent, FLAG_IMMUTABLE)




        mBuilder = NotificationCompat.Builder(context, HandyAppEnvironment.NOTIFICATION_CHANNEL_ID)
        mBuilder.setSmallIcon(R.drawable.ic_reminder)
        mBuilder.setContentIntent(openActivity)
        mBuilder.setContentTitle(title)
        mBuilder.setContentText(message)
        mBuilder.priority = NotificationCompat.PRIORITY_DEFAULT
        mBuilder.setAutoCancel(false)
        mBuilder.setOnlyAlertOnce(true)
        mBuilder.addAction(R.drawable.ic_check, "ON TIME", isWinClick)
        mBuilder.addAction(R.drawable.ic_close,"NOT ON TIME", isNotWin)



        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(HandyAppEnvironment.NOTIFICATION_NUMBER_ID, mBuilder.build())
        }
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