package com.edimitre.handyapp.data.view_model


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.edimitre.handyapp.data.model.Expense
import com.edimitre.handyapp.data.service.ExpenseService

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ExpenseViewModel @Inject constructor(private val expenseService: ExpenseService) :
    ViewModel() {


    fun saveExpense(expense: Expense): Job = viewModelScope.launch {

        expenseService.saveExpense(expense)

    }


    fun deleteExpense(expense: Expense): Job = viewModelScope.launch {

        expenseService.deleteExpense(expense)

    }


    fun getValueOfExpensesByYearMonthDate(year: Int, month: Int, date: Int): LiveData<Int>? {

        return expenseService.getValueOfExpenseByYearMonthDate(year, month, date)
    }

    fun getValueOfExpensesbyYearMonth(year: Int, month: Int): LiveData<Int>? {

        return expenseService.getValueOfExpenseByYearMonth(year, month)
    }

    fun getValueOfExpensesbyYear(year: Int): LiveData<Int>? {

        return expenseService.getValueOfExpensesByYear(year)
    }

    fun getValueOfExpensesByName(name: String): LiveData<Int>? {

        return expenseService.getValueOfExpensesByName(name)
    }


    // BY YEAR
    fun getAllExpensesByYearPaginated(year: Int): Flow<PagingData<Expense>> {


        return Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            pagingSourceFactory = { expenseService.getAllExpensesByYear(year)!! })
            .flow
            .cachedIn(viewModelScope)
    }

    // BY MONTH
    fun getAllExpensesByYearAndMonth(year: Int, month: Int): Flow<PagingData<Expense>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            pagingSourceFactory = { expenseService.getAllExpensesByYearAndMonth(year, month)!! })
            .flow
            .cachedIn(viewModelScope)
    }

    // BY DATE
    fun getAllExpensesBYYearMonthDate(year: Int, month: Int, date: Int): Flow<PagingData<Expense>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            pagingSourceFactory = {
                expenseService.getAllExpensesByYearMonthDate(
                    year,
                    month,
                    date
                )!!
            })
            .flow
            .cachedIn(viewModelScope)
    }


    fun getAllExpensesByDescription(desc: String): Flow<PagingData<Expense>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            pagingSourceFactory = { expenseService.getAllExpensesByDescription(desc)!! })
            .flow
            .cachedIn(viewModelScope)
    }

}