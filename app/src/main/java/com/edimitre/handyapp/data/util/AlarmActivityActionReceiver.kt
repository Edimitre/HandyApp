package com.edimitre.handyapp.data.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.edimitre.handyapp.data.dao.CigarDao
import com.edimitre.handyapp.data.dao.CigarGameTableDao
import com.edimitre.handyapp.data.model.CigarGameTable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@AndroidEntryPoint
class AlarmActivityActionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var cigarDao: CigarDao

    @Inject
    lateinit var gameTableDao: CigarGameTableDao

    @Inject
    lateinit var systemService: SystemService

    override fun onReceive(context: Context, intent: Intent) {

        val isWin = intent.extras?.getBoolean("IS_WIN")
        val cigarId = intent.extras?.getInt("CIGAR_ID")

        context.stopService(Intent(context, ShowCigarAlarmService::class.java))

        systemService.stopVibrator()
        systemService.stopRingtone()

        runBlocking {

            val cigar = cigarDao.getCigarById(cigarId!!)
            cigar?.isWin = isWin
            cigar?.isActive = false
            cigarDao.saveCigar(cigar!!)



            var gameTable = gameTableDao.getCigarGameTableByYearAndMonthOnCoroutine(
                TimeUtils().getCurrentYear(),
                TimeUtils().getCurrentMonth()
            )
            if (gameTable == null) {

                gameTable = CigarGameTable(
                    1,
                    TimeUtils().getCurrentYear(),
                    TimeUtils().getCurrentMonth(),
                    0,
                    0,
                    false
                )
                gameTableDao.saveCigarGameTable(gameTable)
            }


            if (cigar.isWin == true) {
                gameTable.pointsWon = gameTable.pointsWon + 1
            }

            if (cigar.isWin == false) {
                gameTable.pointsLose = gameTable.pointsLose + 1
            }

            if (gameTable.pointsWon >= gameTable.pointsLose) {
                gameTable.isWinning = true
            }
            gameTableDao.saveCigarGameTable(gameTable)


            val nextCigar = cigarDao.getFirstCigarOnCoroutine()
            systemService.setCigarAlarm(nextCigar!!.alarmInMillis)
        }



//        systemService.notify("Handy app", "result saved")


    }

}