package com.edimitre.handyapp.activity

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.edimitre.handyapp.HandyAppEnvironment.TAG
import com.edimitre.handyapp.R
import com.edimitre.handyapp.data.dao.CigarDao
import com.edimitre.handyapp.data.model.Cigar
import com.edimitre.handyapp.data.util.AlarmActivityActionReceiver
import com.edimitre.handyapp.databinding.ActivityAlarmBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@AndroidEntryPoint
class AlarmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlarmBinding

    @Inject
    lateinit var cigarDao: CigarDao

    private lateinit var vibrator: Vibrator

    private var mediaPlayer: MediaPlayer = MediaPlayer()

    var cigar: Cigar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dismissKeyguard()

        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        runBlocking {

            cigar = cigarDao.getFirstCigarOnCoroutine()

        }


        startRingtone()

        startVibrator()

        setListeners()

    }

    private fun dismissKeyguard() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setTurnScreenOn(true)
            setShowWhenLocked(true)
            (getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager?)?.requestDismissKeyguard(
                this,
                object : KeyguardManager.KeyguardDismissCallback() {
                    //log if succes or failre
                }
            )
        } else {
            window?.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun setListeners() {

        binding.btnWin.setOnClickListener {

            stopRingtone()
            stopVibrator()

            val intent = Intent(this, AlarmActivityActionReceiver::class.java)
            intent.putExtra("CIGAR_ID", this.cigar?.id)
            intent.putExtra("IS_WIN", true)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            sendBroadcast(intent)
        }

        binding.btnLose.setOnClickListener {

            stopRingtone()
            stopVibrator()

            val intent = Intent(this, AlarmActivityActionReceiver::class.java)
            intent.putExtra("CIGAR_ID", this.cigar?.id)
            intent.putExtra("IS_WIN", false)
            sendBroadcast(intent)
        }
    }


    private fun startRingtone() {

        val uriSound = Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(applicationContext.packageName)
            .appendPath("${R.raw.alarm}")
            .build()

//        mediaPlayer = MediaPlayer()
        mediaPlayer.apply {
            reset()
            setAudioAttributes( // Here is the important part
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM) // usage - alarm
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            setDataSource(
                this@AlarmActivity,
                uriSound
            )
            isLooping = true
            prepare()
            start()
        }

    }

    private fun stopRingtone() {
        try {
            mediaPlayer.stop()
            mediaPlayer.release()
//            mediaPlayer?.apply {
//
//                reset()
//                prepare()
//                stop()
//                release()
//
//            }

        } catch (e: Exception) {

            Log.e(TAG, "error stopping media player ${e.localizedMessage}")
        }


    }

    @SuppressLint("ServiceCast")
    private fun startVibrator() {


        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager: VibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator

        } else {
            // backward compatibility for Android API < 31,
            // VibratorManager was only added on API level 31 release.
            // noinspection deprecation
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        val DELAY = 0
        val VIBRATE = 1000
        val SLEEP = 1000
        val START = 0
        val vibratePattern = longArrayOf(DELAY.toLong(), VIBRATE.toLong(), SLEEP.toLong())

        vibrator.vibrate(VibrationEffect.createWaveform(vibratePattern, START))

    }

    private fun stopVibrator() {

        vibrator.cancel()
    }


}