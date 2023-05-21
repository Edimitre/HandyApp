package com.edimitre.handyapp.data.util


import android.annotation.SuppressLint
import android.app.*
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.ContentResolver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.R
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

    private lateinit var mBuilder: NotificationCompat.Builder

    private var vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager: VibratorManager =
            context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator

    } else {
        // backward compatibility for Android API < 31,
        // VibratorManager was only added on API level 31 release.
        // noinspection deprecation
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    private var mediaPlayer: MediaPlayer ? = MediaPlayer()

    fun createNotificationChannel() {
        // Create the NotificationChannel

        val mChannel = NotificationChannel(
            HandyAppEnvironment.NOTIFICATION_CHANNEL_ID,
            HandyAppEnvironment.NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT
        )

        mChannel.description = HandyAppEnvironment.NOTIFICATION_CHANNEL_ID

        mChannel.enableLights(true)
        mChannel.lightColor = Color.RED
        mChannel.enableVibration(true)
        mChannel.vibrationPattern = longArrayOf(1L,2L,3L)
        // register in system
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)

    }

    fun createAlarmNotificationChannel() {
        // Create the NotificationChannel

        val mChannel = NotificationChannel(HandyAppEnvironment.NOTIFICATION_ALARM_CHANNEL_ID, HandyAppEnvironment.NOTIFICATION_ALARM_CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH)

        mChannel.description = HandyAppEnvironment.NOTIFICATION_ALARM_CHANNEL_ID

        mChannel.enableLights(true)
        mChannel.lightColor = Color.RED
        mChannel.enableVibration(true)
        mChannel.vibrationPattern = longArrayOf(1L,2L,3L)
        mChannel.setSound(null, null)
        // register in system
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)

    }

    fun setAlarm(alarmTime: Long) {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, FLAG_IMMUTABLE)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmTime,
            pendingIntent
        )

    }

    fun setCigarAlarm(alarmTime: Long) {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, CigarAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, FLAG_IMMUTABLE)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmTime,
            pendingIntent
        )

    }

    fun cancelAllAlarms() {

        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val cancelReminderIntent = Intent(context, ReminderReceiver::class.java)
        @SuppressLint("UnspecifiedImmutableFlag")
        val cancelReminderPI = PendingIntent.getBroadcast(context, 0, cancelReminderIntent, FLAG_IMMUTABLE)
        alarm.cancel(cancelReminderPI)

        val cancelCigarsAlarmsIntent = Intent(context, CigarAlarmReceiver::class.java)
        @SuppressLint("UnspecifiedImmutableFlag")
        val cancelCigarAlarmsPI = PendingIntent.getBroadcast(context, 0, cancelCigarsAlarmsIntent, FLAG_IMMUTABLE)
        alarm.cancel(cancelCigarAlarmsPI)
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
        mBuilder.priority = NotificationCompat.PRIORITY_DEFAULT
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

    fun startRingtone() {

        val uriSound = Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(context.packageName)
            .appendPath("${R.raw.alarm}")
            .build()

//        mediaPlayer = MediaPlayer()
        mediaPlayer?.apply {
            reset()
            setAudioAttributes( // Here is the important part
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM) // usage - alarm
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            setDataSource(
                context,
                uriSound
            )
            isLooping = true
            prepare()
            start()
        }

    }

    fun stopRingtone() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.reset()
            mediaPlayer?.release()

        } catch (e: Exception) {

            Log.e(HandyAppEnvironment.TAG, "error stopping media player ${e.localizedMessage}")
        }


    }

    @SuppressLint("ServiceCast")
    fun startVibrator() {

        val DELAY = 0
        val VIBRATE = 1000
        val SLEEP = 1000
        val START = 0
        val vibratePattern = longArrayOf(DELAY.toLong(), VIBRATE.toLong(), SLEEP.toLong())

        vibrator.vibrate(VibrationEffect.createWaveform(vibratePattern, START))

    }

    fun stopVibrator() {

        vibrator.cancel()
    }

    // return status of device internet connection
    fun hasConnection(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
//                Log.e(HandyAppEnvironment.TAG, "Has cellular connection")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
//                Log.e(HandyAppEnvironment.TAG, "Has wifi connection")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
//                Log.e(HandyAppEnvironment.TAG, "Has ethernet connection")
                return true
            }
        }

        // todo this value needs to be some type of flow or live data
        return false
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