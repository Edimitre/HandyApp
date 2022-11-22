package com.edimitre.handyapp.data.service


import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.edimitre.handyapp.data.dao.ShopExpenseDao
import com.edimitre.handyapp.data.model.Expense
import com.edimitre.handyapp.data.util.TimeUtils

import javax.inject.Inject

class ExpenseService @Inject constructor(private val expenseDao: ShopExpenseDao) {

    //TODAY
    var todayExpenses = expenseDao.getAllExpensesByYearMonthAndDateLiveData(
        TimeUtils().getCurrentYear(), TimeUtils().getCurrentMonth(), TimeUtils().getCurrentDate()
    )

    var valueSpentToday = expenseDao.getValueOfExpensesByYearMonthDate(
        TimeUtils().getCurrentYear(), TimeUtils().getCurrentMonth(), TimeUtils().getCurrentDate()
    )

    var nrOfExpensesToday = expenseDao.getNrOfExpensesByYearMonthDate(
        TimeUtils().getCurrentYear(), TimeUtils().getCurrentMonth(), TimeUtils().getCurrentDate()
    )


    // THIS MONTH
    var thisMonthExpenses = expenseDao.getAllExpensesByYearAndMonthLiveData(
        TimeUtils().getCurrentYear(), TimeUtils().getCurrentMonth()
    )

    var valueSpentThisMonth = expenseDao.getValueOfExpensesByYearAndMonth(
        TimeUtils().getCurrentYear(), TimeUtils().getCurrentMonth()
    )

    var nrOfExpensesThisMonth = expenseDao.getNrOfExpensesByYearMonth(
        TimeUtils().getCurrentYear(), TimeUtils().getCurrentMonth()
    )

    //THIS YEAR
    var thisYearExpenses = expenseDao.getAllExpensesByYearLiveData(TimeUtils().getCurrentYear())


    var nrOfExpensesThisYear = expenseDao.getNrOfExpensesByYear(TimeUtils().getCurrentYear())

    var biggestExpenseThisMonth = expenseDao.getBiggestExpenseByYearAndMonth(
        TimeUtils().getCurrentYear(), TimeUtils().getCurrentMonth()
    )


    suspend fun saveExpense(expense: Expense) {
        expenseDao.saveOrUpdateExpense(expense)

    }

    suspend fun deleteExpense(expense: Expense) {

        return expenseDao.deleteExpense(expense)
    }

    fun getValueOfExpenseByYearMonthDate(year: Int, month: Int, date: Int): LiveData<Int>? {
        return expenseDao.getValueOfExpensesByYearMonthDate(year, month, date)
    }

    fun getValueOfExpenseByYearMonth(year: Int, month: Int): LiveData<Int>? {
        return expenseDao.getValueOfExpensesByYearAndMonth(
            year, month
        )
    }

    fun getValueOfExpensesByYear(year: Int): LiveData<Int>? {
        return expenseDao.getValueOfExpensesByYear(
            year
        )
    }

    fun getAllExpensesByYear(year: Int): PagingSource<Int, Expense>? {
        return expenseDao.getAllExpensesByYearPaged(year)
    }

    fun getAllExpensesByYearAndMonth(year: Int, month: Int): PagingSource<Int, Expense>? {
        return expenseDao.getAllExpensesByYearAndMonthPaged(year, month)
    }

    fun getAllExpensesByYearMonthDate(
        year: Int,
        month: Int,
        date: Int
    ): PagingSource<Int, Expense>? {
        return expenseDao.getAllExpensesByYearMonthAndDatePaged(year, month, date)
    }


    fun getAllExpensesByDescription(desc: String): PagingSource<Int, Expense>? {

        return expenseDao.getAllExpensesByDescriptionLiveData(desc)
    }

    fun getAllExpensesByNameToday(name: String): PagingSource<Int, Expense>? {

        return expenseDao.getAllExpensesByNameToday(
            TimeUtils().getCurrentYear(),
            TimeUtils().getCurrentMonth(), TimeUtils().getCurrentDate(), name
        )
    }

    fun getAllExpensesByNameThisMonth(name: String): PagingSource<Int, Expense>? {

        return expenseDao.getAllExpensesByNameThisMonth(
            TimeUtils().getCurrentYear(),
            TimeUtils().getCurrentMonth(), name
        )
    }

    fun getAllExpensesByNameThisYear(name: String): PagingSource<Int, Expense>? {

        return expenseDao.getAllExpensesByNameThisYear(TimeUtils().getCurrentYear(), name)
    }


    fun getValueOfExpensesByName(name: String): LiveData<Int>? {

        return expenseDao.getValueOfExpensesByDescription(name)
    }

    fun getValueOfExpensesByShopName(name: String): LiveData<Int>? {

        return expenseDao.getValueOfExpensesByShopName(name)
    }
}