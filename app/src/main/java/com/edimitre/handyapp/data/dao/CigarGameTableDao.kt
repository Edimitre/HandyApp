package com.edimitre.handyapp.data.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.edimitre.handyapp.data.model.*


@Dao
interface CigarGameTableDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCigarGameTable(cigarGameTable:CigarGameTable)

    @Query("SELECT * FROM cigar_game_table")
    fun getAllCigarGameTablesLiveData(): LiveData<List<CigarGameTable>>?

    @Query("SELECT * FROM cigar_game_table WHERE year = :year and month = :month")
    suspend fun getCigarGameTableByYearAndMonthOnCoroutine(year: Int, month: Int): CigarGameTable?

    @Query("SELECT * FROM cigar_game_table WHERE year = :year and month = :month")
    fun getCigarGameTableByYearAndMonthLiveData(year: Int, month: Int): LiveData<CigarGameTable?>

    @Query("DELETE FROM cigar_game_table")
    suspend fun deleteAllCigarGameTables()

}