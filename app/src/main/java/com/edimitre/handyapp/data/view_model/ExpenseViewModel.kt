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


    // VALUE BY YEAR MONTH DATE
    fun getValueOfExpensesByYearMonthDate(year: Int, month: Int, date: Int): LiveData<Int>? {

        return expenseService.getValueOfExpenseByYearMonthDate(year, month, date)
    }

    // VALUE BY YEAR MONTH
    fun getValueOfExpensesbyYearMonth(year: Int, month: Int): LiveData<Int>? {

        return expenseService.getValueOfExpenseByYearMonth(year, month)
    }

    // VALUE BY YEAR
    fun getValueOfExpensesbyYear(year: Int): LiveData<Int>? {

        return expenseService.getValueOfExpensesByYear(year)
    }

    // VALUE BY DESCRIPTION
    fun getValueOfExpensesByDescription(name: String): LiveData<Int>? {

        return expenseService.getValueOfExpensesByDescription(name)
    }

    // VALUE BY SHOP NAME
    fun getValueOfExpensesByShopName(shopName: String): LiveData<Int>? {

        return expenseService.getValueOfExpensesByShopName(shopName)
    }


    fun getNrOfExpensesByYearMonthDate(year: Int, month: Int, date: Int): LiveData<Int>? {

        return expenseService.getNrOfExpensesByYearMonthDate(year, month, date)
    }

    fun getNrOfExpensesByYearMonth(year: Int, month: Int): LiveData<Int>? {

        return expenseService.getNrOfExpensesByYearMonth(year, month)
    }

    fun getNrOfExpensesByYear(year: Int): LiveData<Int>? {

        return expenseService.getNrOfExpensesByYear(year)
    }

    fun getNrOfExpensesByDescription(description: String): LiveData<Int>? {

        return expenseService.getNrOfExpensesByDescription(description)
    }

    fun getNrOfExpensesByShopName(shopName: String): LiveData<Int>? {

        return expenseService.getNrOfExpensesByShopName(shopName)
    }


    //  BY YEAR
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

    // ALL BY MONTH
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

    // ALL BY DATE
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

    // ALL BY DESCRIPTION
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

    //  BY SHOPNAME
    fun getAllExpensesByShopName(shopName: String): Flow<PagingData<Expense>> {


        return Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            pagingSourceFactory = { expenseService.getAllExpensesByShopName(shopName)!! })
            .flow
            .cachedIn(viewModelScope)
    }
}