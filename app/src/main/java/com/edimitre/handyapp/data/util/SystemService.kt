package com.edimitre.handyapp.data.util


import android.annotation.SuppressLint
import android.app.*
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.R
import com.edimitre.handyapp.activity.AlarmActivity
import com.edimitre.handyapp.activity.CigaretteReminderActivity
import com.edimitre.handyapp.activity.MainActivity
import com.edimitre.handyapp.data.model.firebase.BackUpDto
import com.edimitre.handyapp.data.scraper.BotaAlScrapper
import com.edimitre.handyapp.data.scraper.JoqScrapper
import com.edimitre.handyapp.data.scraper.LapsiScrapper
import com.edimitre.handyapp.data.scraper.SyriScrapper
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


    fun setCigarAlarm(alarmTime: Long) {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, CigarAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, FLAG_IMMUTABLE)
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
            10,
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

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val backUpWork = OneTimeWorkRequest.Builder(BackUpDBWorker::class.java)
            .addTag("backup_worker")
            .setConstraints(constraints)
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

        var backUpDto = Gson().fromJson(importData, BackUpDto::class.java)

        // because data cant be larger than 10240 bytes...so the files needs separate work
        backUpDto =  clearDtoFromFiles(backUpDto)


        val data = Data.Builder()
        data.putString("backup_data", Gson().toJson(backUpDto))
        val importWork = OneTimeWorkRequest.Builder(ImportDBWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data.build())
            .addTag("import_work")
            .build()
        val workManager = WorkManager.getInstance(context)

        workManager.enqueue(importWork)

        return importWork.id
    }

    private fun clearDtoFromFiles(backUpDto: BackUpDto):BackUpDto{

        val sharedPrefUtil = SharedPrefUtil(context)

        sharedPrefUtil.setWorkFilesList(backUpDto.filesAsBytesList)
        startWorkFileWorker()
        backUpDto.filesAsBytesList = emptyList()


        sharedPrefUtil.setMemeTemplateList(backUpDto.memeTemplatesList)
        startMemeTemplateWorker()
        backUpDto.memeTemplatesList = emptyList()

        return backUpDto
    }

    private fun startMemeTemplateWorker(): UUID {

        val importTemplatesWork = OneTimeWorkRequest.Builder(MemeTemplateWorker::class.java)
            .addTag("import_meme_templates_work")
            .build()
        val workManager = WorkManager.getInstance(context)

        workManager.enqueue(importTemplatesWork)

        return importTemplatesWork.id

    }

    private fun startWorkFileWorker(): UUID {

        val workFilesWork = OneTimeWorkRequest.Builder(WorkFileWorker::class.java)
            .addTag("work_files_work")
            .build()
        val workManager = WorkManager.getInstance(context)

        workManager.enqueue(workFilesWork)

        return workFilesWork.id

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

    fun getCigarAlarmNotification(title: String?, message: String?,id:Int): Notification {




        val fullScreenIntent = Intent(context, AlarmActivity::class.java)
        val fullScreen = PendingIntent.getActivity(context, HandyAppEnvironment.NOTIFICATION_NUMBER_ID, fullScreenIntent, FLAG_IMMUTABLE)


        val activityIntent = Intent(context, CigaretteReminderActivity::class.java)
        val openActivity = PendingIntent.getActivity(context, HandyAppEnvironment.NOTIFICATION_NUMBER_ID, activityIntent, FLAG_IMMUTABLE)


        val bundleTrue = Bundle()
        bundleTrue.putInt("CIGAR_ID", id)
        bundleTrue.putBoolean("IS_WIN", true)

        val isWinIntent = Intent(context, NotificationActionReceiver::class.java)
        isWinIntent.putExtra("bundle", bundleTrue)
        val isWinClick = PendingIntent.getBroadcast(context, 2, isWinIntent, FLAG_IMMUTABLE)


        val bundleFalse = Bundle()
        bundleFalse.putInt("CIGAR_ID", id)
        bundleFalse.putBoolean("IS_WIN", false)

        val isNotWinIntent = Intent(context, NotificationActionReceiver::class.java)
        isWinIntent.putExtra("bundle", bundleFalse)
        val isNotWin = PendingIntent.getBroadcast(context, 3, isNotWinIntent, FLAG_IMMUTABLE)




        mBuilder = NotificationCompat.Builder(context, HandyAppEnvironment.NOTIFICATION_CHANNEL_ID)
        mBuilder.setSmallIcon(R.drawable.ic_reminder)
        mBuilder.setContentIntent(openActivity)
        mBuilder.setFullScreenIntent(fullScreen, true)
        mBuilder.setContentTitle(title)
        mBuilder.setContentText(message)
        mBuilder.priority = NotificationCompat.PRIORITY_MAX
        mBuilder.setAutoCancel(false)
//        mBuilder.setOnlyAlertOnce(true)
        mBuilder.addAction(R.drawable.ic_check, "ON TIME", isWinClick)
        mBuilder.addAction(R.drawable.ic_close,"NOT ON TIME", isNotWin)



//        val activityIntent = Intent(context, CigaretteReminderActivity::class.java)
//        val openActivity = PendingIntent.getActivity(context, HandyAppEnvironment.NOTIFICATION_NUMBER_ID, activityIntent, FLAG_IMMUTABLE)
//
//
//        val fullScreenIntent = Intent(context, AlarmActivity::class.java)
//        val fullScreenPendingIntent = PendingIntent.getActivity(
//            context, 0,
//            fullScreenIntent, FLAG_IMMUTABLE
//        )
//
//        val notBuilder: NotificationCompat.Builder =
//            NotificationCompat.Builder(context, HandyAppEnvironment.NOTIFICATION_CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_reminder)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setContentIntent(openActivity)
//                .setPriority(NotificationCompat.PRIORITY_MAX)
//                .setCategory(NotificationCompat.CATEGORY_ALARM)
//                .setFullScreenIntent(fullScreenPendingIntent, true)
//                .setAutoCancel(true)

//
//        with(NotificationManagerCompat.from(context)) {
//            // notificationId is a unique int for each notification that you must define
//            notify(HandyAppEnvironment.NOTIFICATION_NUMBER_ID, notBuilder.build())
//        }

        return mBuilder.build()
    }

//    fun aquireWake() {
//        val mPowerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
//        val mWakeLock: PowerManager.WakeLock = mPowerManager.newWakeLock(
//            PowerManager.SCREEN_DIM_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
//            "YourApp:Whatever"
//        )
//        mWakeLock.acquire()
//    }

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