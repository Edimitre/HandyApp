package com.edimitre.handyapp.data.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class TimeUtils {

    fun getCurrentYear(): Int {

        val date = Date()

        val cal = Calendar.getInstance()

        cal.time = date



        return cal.get(Calendar.YEAR)
    }

    fun getCurrentMonth(): Int {

        val date = Date()

        val cal = Calendar.getInstance()

        cal.time = date



        return cal.get(Calendar.MONTH)
    }

    fun getCurrentDate(): Int {

        val date = Date()

        val cal = Calendar.getInstance()

        cal.time = date



        return cal.get(Calendar.DAY_OF_MONTH)
    }

    fun getCurrentHour(): Int {

        val date = Date()

        val cal = Calendar.getInstance()

        cal.time = date



        return cal.get(Calendar.HOUR_OF_DAY)
    }

    fun getCurrentMinute(): Int {

        val date = Date()

        val cal = Calendar.getInstance()

        cal.time = date



        return cal.get(Calendar.MINUTE)
    }

    fun getTimeInMilliSeconds(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long {

        val cal = Calendar.getInstance()

        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, day)
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        return cal.timeInMillis
    }

    fun getTimeInMilliSeconds(year: Int, month: Int, day: Int): Long {

        val cal = Calendar.getInstance()

        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, day)

        return cal.timeInMillis
    }

    private fun getNrOfDaysOfActualMonth(): Int {
        val date = Date()
        val cal = Calendar.getInstance()
        cal.time = date
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    fun getNrOfRemainingDaysOfActualMonth(): Int {

        val todayDate = getCurrentDate()
        val nrOfDays = getNrOfDaysOfActualMonth()

        return nrOfDays - todayDate
    }

    fun convertMillisToMinutes(millis: Long): Long {

        return TimeUnit.MILLISECONDS.toMinutes(millis)
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateStringFromMilliSeconds(dateTimeInMillis: Long): String {
//
//        val cal = Calendar.getInstance()
//        cal.timeInMillis = dateTimeInMillis

        val formatter = SimpleDateFormat("dd/MM/yyyy");



        return formatter.format(Date(dateTimeInMillis))

    }
}