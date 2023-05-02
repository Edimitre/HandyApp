package com.edimitre.handyapp.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.edimitre.handyapp.HandyAppEnvironment.TAG
import com.edimitre.handyapp.data.model.Cigar
import com.edimitre.handyapp.data.model.Reminder
import com.edimitre.handyapp.data.room_database.HandyDb
import com.edimitre.handyapp.data.util.SystemService
import com.edimitre.handyapp.data.util.TimeUtils


class CigarAlarmWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val ctx = context

    var systemService = SystemService(ctx)

    override suspend fun doWork(): Result {

        val cigarDao = HandyDb.getInstance(ctx).getCigarDao()

        val cigar: Cigar? = cigarDao.getFirstCigarOnCoroutine()

        if (cigar != null) {

            // todo show a notification with button action to get what happened with that cigar is a win or a lose

            cigar.isActive = false
            cigarDao.saveCigar(cigar)

            Log.e(TAG, "on cigar worker id cigar ${cigar.id}", )

            systemService.notifyCigarAlarm("cigar alarm : ", TimeUtils().getHourStringFromDateInMillis(cigar.alarmInMillis).replace("/", ":"), cigar.id)


            val nextCigar: Cigar? = cigarDao.getFirstCigarOnCoroutine()

            if (nextCigar != null) {
                systemService.cancelAllCigarAlarms()

                systemService.setCigarAlarm(nextCigar.alarmInMillis)
            }
        }

        return Result.success()
    }


}