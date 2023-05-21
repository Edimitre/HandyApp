package com.edimitre.handyapp.data.service


import com.edimitre.handyapp.data.dao.CigarDao
import com.edimitre.handyapp.data.dao.CigarGameTableDao
import com.edimitre.handyapp.data.model.Cigar
import com.edimitre.handyapp.data.util.TimeUtils
import java.util.*
import javax.inject.Inject

class CigaretteService @Inject constructor(
    private val cigarDao: CigarDao,
    private val cigarGameTableDao: CigarGameTableDao
) {

    var allCigars = cigarDao.getAllCigarsLiveData()

    var gameTable = cigarGameTableDao.getCigarGameTableByYearAndMonthLiveData(
        TimeUtils().getCurrentYear(),
        TimeUtils().getCurrentMonth()
    )

    suspend fun saveCigar(cigar: Cigar) {
        cigarDao.saveCigar(cigar)

    }

    suspend fun deleteAllCigarettes() {

        cigarDao.deleteAllCigars()
    }

    suspend fun getFirstCigarByAlarmTimeInMills(): Cigar? {
        return cigarDao.getFirstCigarOnCoroutine()
    }


    suspend fun distributeCigars(minutes: Long, nrOfCigars: Int) {

        var currentDate = Date()

        for (i in 1..nrOfCigars) {

            val timeInMillis = TimeUtils().getMillisFromDate(currentDate)

            val cigar = Cigar(i, true,null,timeInMillis)
            saveCigar(cigar)

            val nextDate = TimeUtils().addMinutesToDate(currentDate, minutes)
            currentDate = nextDate

        }


    }


    suspend fun setCigarWin(value: Boolean, cigarId: String) {

        val cigar = cigarDao.getCigarById(cigarId.toInt())
        cigar?.isWin = value

        cigarDao.saveCigar(cigar!!)

    }

    suspend fun clearGamePoints() {

        val gameTable = cigarGameTableDao.getCigarGameTableByYearAndMonthOnCoroutine(
            TimeUtils().getCurrentYear(),
            TimeUtils().getCurrentMonth()
        )
        gameTable?.pointsWon = 0
        gameTable?.pointsLose = 0
        gameTable?.isWinning = null

        cigarGameTableDao.saveCigarGameTable(gameTable!!)
    }


}