package com.edimitre.handyapp.data.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
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

    fun getCurrentTimeInMilliSeconds(): Long {

        val cal = Calendar.getInstance()

        cal.set(Calendar.YEAR, getCurrentYear())
        cal.set(Calendar.MONTH, getCurrentMonth())
        cal.set(Calendar.DAY_OF_MONTH, getCurrentDate())

        return cal.timeInMillis
    }

    fun addMinutesToCurrentDate(currentDateInMillis:Long, minutes:Long): Long {

        return currentDateInMillis + TimeUnit.MINUTES.toMillis(minutes)
    }

    fun getMillisFromMinutes(minutes:Long): Long {


        return TimeUnit.MINUTES.toMillis(minutes)
    }

    fun getMinutesFromMillis(millis:Long): Long {


        return TimeUnit.MILLISECONDS.toMinutes(millis)
    }

//    private fun getNrOfDaysOfActualMonth(): Int {
//        val date = Date()
//        val cal = Calendar.getInstance()
//        cal.time = date
//        return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
//    }

//    fun getNrOfRemainingDaysOfActualMonth(): Int {
//
//        val todayDate = getCurrentDate()
//        val nrOfDays = getNrOfDaysOfActualMonth()
//
//        return nrOfDays - todayDate
//    }
//
//    fun convertMillisToMinutes(millis: Long): Long {
//
//        return TimeUnit.MILLISECONDS.toMinutes(millis)
//    }

    @SuppressLint("SimpleDateFormat")
    fun getDateStringFromMilliSeconds(dateTimeInMillis: Long): String {
//
//        val cal = Calendar.getInstance()
//        cal.timeInMillis = dateTimeInMillis

        val formatter = SimpleDateFormat("dd/MM/yyyy")

        return formatter.format(getDateFromMilliseconds(dateTimeInMillis))

    }

    fun getHourStringFromDateInMillis(millis:Long):String{

        val cal = Calendar.getInstance()
        cal.timeInMillis = millis


        return "hour ${cal[Calendar.HOUR_OF_DAY]}/${cal[Calendar.MINUTE]}"
    }

    private fun getDateFromMilliseconds(millis: Long): Date {

        val cal = Calendar.getInstance()
        cal.timeInMillis = millis

        return cal.time
    }

    fun isFriday(): Boolean {

        val cal = Calendar.getInstance()

        return cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY
    }

    fun isSaturdayOrSunday(): Boolean {

        var isNotWorkDay = false
        val cal = Calendar.getInstance()

        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            isNotWorkDay = true
        } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            isNotWorkDay = true
        }

        return isNotWorkDay
    }


    fun getDate(year:Int, month:Int,date:Int,hour:Int, minute:Int): Date? {

        val calendar = Calendar.getInstance()
        calendar[Calendar.YEAR] = year
        calendar[Calendar.MONTH] = month
        calendar[Calendar.DATE] = date
        calendar[Calendar.HOUR_OF_DAY] = hour
        calendar[Calendar.MINUTE] = minute

        return calendar.time


    }

    fun addMinutesToDate(date: Date,minutesToAdd:Long): Date {

        val localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
        val requiredDateTime = localDateTime.plusMinutes(minutesToAdd)

        val zoneDateTime = requiredDateTime.atZone(ZoneId.systemDefault());

        return Date.from(zoneDateTime.toInstant())
    }

    fun getMillisFromDate(date:Date): Long {

        val cal = Calendar.getInstance()
        cal.time = date

        return cal.timeInMillis
    }

    fun getDateFromMillis(timeInMillis:Long): Date? {

        val cal = Calendar.getInstance()
        cal.timeInMillis= timeInMillis

        return cal.time
    }
}