package com.edimitre.handyapp.data.service


import com.edimitre.handyapp.data.dao.CigarDao
import com.edimitre.handyapp.data.dao.CigarGameTableDao
import com.edimitre.handyapp.data.model.Cigar
import com.edimitre.handyapp.data.util.TimeUtils
import javax.inject.Inject

class CigaretteService @Inject constructor(private val cigarDao: CigarDao,private val cigarGameTableDao: CigarGameTableDao) {

    var allCigars = cigarDao.getAllCigarsLiveData()

    var gameTable = cigarGameTableDao.getCigarGameTableByYearAndMonthLiveData(TimeUtils().getCurrentYear(),TimeUtils().getCurrentMonth())

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

        for (i in 1..20) {

            val cigar = Cigar(i, isActive = true, isWin = null, alarmInMillis = timeNow)

            saveCigar(cigar)
            timeNow += timeToAdd

        }


    }

    suspend fun setCigarWin(value: Boolean, cigarId: String) {

        val cigar = cigarDao.getCigarById(cigarId.toInt())
        cigar?.isWin = value

        cigarDao.saveCigar(cigar!!)

    }



}