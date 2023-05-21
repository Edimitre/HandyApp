package com.edimitre.handyapp.activity

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.edimitre.handyapp.data.dao.CigarDao
import com.edimitre.handyapp.data.model.Cigar
import com.edimitre.handyapp.data.util.AlarmActivityActionReceiver
import com.edimitre.handyapp.data.util.SystemService
import com.edimitre.handyapp.databinding.ActivityAlarmBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@AndroidEntryPoint
class AlarmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlarmBinding

    @Inject
    lateinit var cigarDao: CigarDao

    @Inject
    lateinit var systemService: SystemService


    var cigar: Cigar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dismissKeyguard()

        runBlocking {

            cigar = cigarDao.getFirstCigarOnCoroutine()

        }


        showCigar()
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

    @SuppressLint("SetTextI18n")
    private fun showCigar() {
        binding.cigarNrText.text = "cigar nr : ${cigar?.id}"
    }

    private fun setListeners() {

        binding.btnWin.setOnClickListener {

            val intent = Intent(this, AlarmActivityActionReceiver::class.java)
            intent.putExtra("CIGAR_ID", this.cigar?.id)
            intent.putExtra("IS_WIN", true)
            sendBroadcast(intent)

            finish()

        }

        binding.btnLose.setOnClickListener {

            val intent = Intent(this, AlarmActivityActionReceiver::class.java)
            intent.putExtra("CIGAR_ID", this.cigar?.id)
            intent.putExtra("IS_WIN", false)
            sendBroadcast(intent)

            finish()

        }
    }


}