package com.edimitre.handyapp.data.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.edimitre.handyapp.data.model.Expense
import com.edimitre.handyapp.data.model.Shop


@Dao
interface ShopExpenseDao {


    // SHOPS
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveOrUpdateShop(shop: Shop)

    @Delete
    suspend fun deleteShop(shop: Shop)

    @Query("DELETE FROM shop_table")
    suspend fun deleteAllShops()

    @Query("SELECT * FROM shop_table LIMIT 1")
    fun getTestShop(): LiveData<Shop>?

    @Query("SELECT * FROM shop_table")
    fun getAllShops(): PagingSource<Int, Shop>?

    @Query("SELECT * FROM shop_table WHERE shop_name LIKE '%' || :name || '%'")
    fun getByNamePaged(name: String): PagingSource<Int, Shop>?

    @Query("SELECT * FROM shop_table WHERE shop_id = :id")
    fun getShopById(id: Long): LiveData<Shop>?


    // EXPENSES
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveOrUpdateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM expense_table WHERE shop_id = :id")
    fun getExpenseByShopId(id: Long): LiveData<List<Expense>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveExpenseOnThread(expense: Expense)


    // LIVEDATA

    // YEAR
    @Query("SELECT * FROM expense_table WHERE year = :year")
    fun getAllExpensesByYearLiveData(year: Int): LiveData<List<Expense>>?

    @Query("SELECT SUM(spentValue) FROM expense_table WHERE year = :year")
    fun getValueOfExpensesByYear(year: Int): LiveData<Int>?

    @Query("SELECT COUNT(*) FROM expense_table where year = :year")
    fun getNrOfExpensesByYear(year: Int): LiveData<Int>?


    // MONTH
    @Query("SELECT * FROM expense_table WHERE year = :year and month = :month")
    fun getAllExpensesByYearAndMonthLiveData(year: Int, month: Int): LiveData<List<Expense>>?

    @Query("SELECT SUM(spentValue) FROM expense_table WHERE year = :year and month = :month")
    fun getValueOfExpensesByYearAndMonth(year: Int, month: Int): LiveData<Int>?

    @Query("SELECT COUNT(*) FROM expense_table where year = :year and month = :month")
    fun getNrOfExpensesByYearMonth(year: Int, month: Int): LiveData<Int>?


    // DATE
    @Query("SELECT * FROM expense_table WHERE year = :year and month = :month and date = :date")
    fun getAllExpensesByYearMonthAndDateLiveData(
        year: Int,
        month: Int,
        date: Int
    ): LiveData<List<Expense>>?

    @Query("SELECT SUM(spentValue) FROM expense_table WHERE year = :year and month = :month and date = :date")
    fun getValueOfExpensesByYearMonthDate(year: Int, month: Int, date: Int): LiveData<Int>?

    @Query("SELECT COUNT(*) FROM expense_table where year = :year and month = :month and date = :date")
    fun getNrOfExpensesByYearMonthDate(year: Int, month: Int, date: Int): LiveData<Int>?

    @Query("SELECT SUM(spentValue) FROM expense_table WHERE year = :year and month = :month and date = :date")
    fun getValueOfExpensesByYearMonthDateOnThread(year: Int, month: Int, date: Int): Int?


    // BIGGEST IN MONTH
    @Query("SELECT * FROM expense_table where year =:year and month =:month ORDER BY spentValue DESC LIMIT 1")
    fun getBiggestExpenseByYearAndMonth(year: Int, month: Int): LiveData<Expense>?

    // BY NAME
    @Query("select * from expense_table where description LIKE '%' || :desc || '%'")
    fun getAllExpensesByDescriptionLiveData(desc: String): PagingSource<Int, Expense>?

    @Query("SELECT SUM(spentValue) FROM expense_table WHERE description LIKE '%' || :name || '%'")
    fun getValueOfExpensesByDescription(name: String?): LiveData<Int>?

    @Query("SELECT SUM(spentValue) FROM expense_table WHERE shop_name LIKE '%' || :name || '%'")
    fun getValueOfExpensesByShopName(name: String?): LiveData<Int>?

    @Query("SELECT COUNT(*) FROM expense_table WHERE description LIKE '%' || :description || '%'")
    fun getNrOfExpensesByDescription(description:String): LiveData<Int>?

    @Query("SELECT * FROM expense_table")
    fun getAllExpensesForBackup(): List<Expense>


    // PAGED
    // YEAR
    @Query("SELECT * FROM expense_table WHERE year = :year")
    fun getAllExpensesByYearPaged(year: Int): PagingSource<Int, Expense>?

    // MONTH
    @Query("SELECT * FROM expense_table WHERE year = :year and month = :month")
    fun getAllExpensesByYearAndMonthPaged(year: Int, month: Int): PagingSource<Int, Expense>?

    // DATE
    @Query("SELECT * FROM expense_table WHERE year = :year and month = :month and date = :date")
    fun getAllExpensesByYearMonthAndDatePaged(
        year: Int,
        month: Int,
        date: Int
    ): PagingSource<Int, Expense>?

    @Query("select * from expense_table where shop_name LIKE '%' || :name || '%'")
    fun getAllExpensesByName(name: String): PagingSource<Int, Expense>?

    @Query("select * from expense_table WHERE year = :year and month = :month and date = :date and shop_name LIKE '%' || :name || '%'")
    fun getAllExpensesByNameToday(
        year: Int,
        month: Int,
        date: Int,
        name: String
    ): PagingSource<Int, Expense>?

    @Query("select * from expense_table WHERE year = :year and month = :month and shop_name LIKE '%' || :name || '%'")
    fun getAllExpensesByNameThisMonth(
        year: Int,
        month: Int,
        name: String
    ): PagingSource<Int, Expense>?

    @Query("select * from expense_table WHERE year = :year and shop_name LIKE '%' || :name || '%'")
    fun getAllExpensesByNameThisYear(year: Int, name: String): PagingSource<Int, Expense>?


}