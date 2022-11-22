package com.edimitre.handyapp.data.service


import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.edimitre.handyapp.data.dao.ShopExpenseDao
import com.edimitre.handyapp.data.model.Expense
import com.edimitre.handyapp.data.util.TimeUtils

import javax.inject.Inject

class ExpenseService @Inject constructor(private val expenseDao: ShopExpenseDao) {




    suspend fun saveExpense(expense: Expense) {
        expenseDao.saveOrUpdateExpense(expense)

    }

    suspend fun deleteExpense(expense: Expense) {

        return expenseDao.deleteExpense(expense)
    }


    // VALUE
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

    fun getValueOfExpensesByDescription(name: String): LiveData<Int>? {

        return expenseDao.getValueOfExpensesByDescription(name)
    }

    fun getValueOfExpensesByShopName(name: String): LiveData<Int>? {

        return expenseDao.getValueOfExpensesByShopName(name)
    }


    // NUMBER
    fun getNrOfExpensesByYearMonthDate(year: Int, month: Int, date: Int):LiveData<Int>?{

        return expenseDao.getNrOfExpensesByYearMonthDate(year, month, date)
    }

    fun getNrOfExpensesByYearMonth(year: Int, month: Int):LiveData<Int>?{

        return expenseDao.getNrOfExpensesByYearMonth(year, month)
    }

    fun getNrOfExpensesByYear(year: Int):LiveData<Int>?{

        return expenseDao.getNrOfExpensesByYear(year)
    }

    fun getNrOfExpensesByDescription(description:String):LiveData<Int>?{

        return expenseDao.getNrOfExpensesByDescription(description)
    }

    fun getNrOfExpensesByShopName(shopName:String):LiveData<Int>?{

        return expenseDao.getNrOfExpensesByShopName(shopName)
    }

    // ALL
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

    fun getAllExpensesByShopName(shopName: String): PagingSource<Int, Expense>? {

        return expenseDao.getAllExpensesByShopName(shopName)
    }



}