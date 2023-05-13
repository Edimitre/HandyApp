package com.edimitre.handyapp.data.view_model


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.edimitre.handyapp.data.model.WorkDay
import com.edimitre.handyapp.data.service.WorkDayService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class WorkDayViewModel @Inject constructor(private val workDayService: WorkDayService) :
    ViewModel() {


    fun saveWorkDay(workDay: WorkDay): Job = viewModelScope.launch {

        workDayService.saveWorkDay(workDay)

    }

    fun deleteWorkDay(workDay: WorkDay): Job = viewModelScope.launch {

        workDayService.deleteWorkDay(workDay)

    }

//
//    fun getAllWorkDaysPagedByYear(year: Int): Flow<PagingData<WorkDay>> {
//        return Pager(
//            config = PagingConfig(
//                pageSize = 20,
//                maxSize = 100,
//                enablePlaceholders = false,
//                initialLoadSize = 20
//            ),
//            pagingSourceFactory = { workDayService.getAllWorkDaysPagedByYear(year)!! })
//            .flow
//            .cachedIn(viewModelScope)
//    }

    fun getAllWorkDaysPagedByYearAndMonth(year: Int, month: Int): Flow<PagingData<WorkDay>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            pagingSourceFactory = {
                workDayService.getAllWorkDaysPagedByYearAndMonth(
                    year,
                    month
                )!!
            })
            .flow
            .cachedIn(viewModelScope)
    }



}