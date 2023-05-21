package com.edimitre.handyapp.activity

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.edimitre.handyapp.data.dao.ReminderNotesDao
import com.edimitre.handyapp.data.model.Reminder
import com.edimitre.handyapp.data.util.ReminderActivityActionReceiver
import com.edimitre.handyapp.databinding.ActivityReminderAlarmBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@AndroidEntryPoint
class ReminderAlarmActivity : AppCompatActivity() {

    @Inject
    lateinit var reminderNotesDao: ReminderNotesDao


    lateinit var binding :ActivityReminderAlarmBinding

    var reminder:Reminder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReminderAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dismissKeyguard()
        showReminder()
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

    private fun setListeners(){
        binding.btnOk.setOnClickListener {

            val intent = Intent(this, ReminderActivityActionReceiver::class.java)
            sendBroadcast(intent)

            finish()
        }


    }

    @SuppressLint("SetTextI18n")
    private fun showReminder(){

        runBlocking {

            reminder = reminderNotesDao.getFirstReminderOnCoroutine()

        }


        binding.reminderDescriptionText.text = "reminding you \n${reminder?.description}"
    }
}