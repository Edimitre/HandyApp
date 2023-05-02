package com.edimitre.handyapp.data.service


import com.edimitre.handyapp.data.dao.CigarDao
import com.edimitre.handyapp.data.model.Cigar
import com.edimitre.handyapp.data.util.TimeUtils
import javax.inject.Inject

class CigaretteService @Inject constructor(private val cigarDao: CigarDao) {

    var allCigars = cigarDao.getAllCigarsLiveData()

    suspend fun saveCigar(cigar: Cigar) {
        cigarDao.saveCigar(cigar)

    }

    suspend fun deleteAllCigarettes() {

        cigarDao.deleteAllCigars()
    }


    suspend fun getFirstCigarByAlarmTimeInMills(): Cigar? {
        return cigarDao.getFirstCigarOnCoroutine()
    }


    suspend fun distributeCigars(minutes: Long) {

        var timeNow = TimeUtils().getCurrentTimeInMilliSeconds()

        val timeToAdd = TimeUtils().getMillisFromMinutes(minutes)

        for (i in 1..20){

            val cigar = Cigar(i, isActive = true, isWin = false, alarmInMillis = timeNow)

            saveCigar(cigar)
            timeNow += timeToAdd

//            val alarmTime = "${TimeUtils().getDateStringFromMilliSeconds(timeNow)} :: ${TimeUtils().getHourStringFromDateInMillis(timeNow)}"

        }

    }

}