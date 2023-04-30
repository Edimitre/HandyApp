package com.edimitre.handyapp.data.service


import androidx.paging.PagingSource
import com.edimitre.handyapp.data.dao.WorkDayDao
import com.edimitre.handyapp.data.model.WorkDay
import javax.inject.Inject

class WorkDayService @Inject constructor(private val workDayDao: WorkDayDao) {


    suspend fun saveWorkDay(workDay: WorkDay) {
        workDayDao.save(workDay)

    }

    suspend fun deleteWorkDay(workDay: WorkDay) {

        workDayDao.delete(workDay)
    }


    fun getAllWorkDaysPagedByYear(year: Int): PagingSource<Int, WorkDay>? {

        return workDayDao.getAllWorkDaysPagedByYear(year)
    }

    fun getAllWorkDaysPagedByYearAndMonth(year: Int, month: Int): PagingSource<Int, WorkDay>? {

        return workDayDao.getAllWorkDaysPagedByYearAndMonth(year, month)
    }

    suspend fun getAllWorkDaysByYearAndMonth(year: Int, month: Int): List<WorkDay>? {

        return workDayDao.getAllWorkDaysByYearMonth(year, month)

    }

//    suspend fun getWorkDayByYearMonthDay(year: Int, month: Int, day: Int): WorkDay? {
//
//        return workDayDao.getWorkDayByYearMonthDay(year, month, day)
//
//    }
}