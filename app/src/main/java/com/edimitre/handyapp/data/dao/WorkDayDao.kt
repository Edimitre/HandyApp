package com.edimitre.handyapp.data.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.edimitre.handyapp.data.model.News
import com.edimitre.handyapp.data.model.Reminder
import com.edimitre.handyapp.data.model.WorkDay


@Dao
interface WorkDayDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(workDay: WorkDay)

    @Delete
    suspend fun delete(workDay: WorkDay)

//    @Query("DELETE FROM workday_table where source = :source and liked = '0'")
//    suspend fun deleteAllNewsBySource(source: String)

//    @Query("SELECT * FROM news_table where source = :source LIMIT 1")
//    suspend fun getOneBySource(source: String): News?

    @Query("SELECT * FROM workday_table where year =:year")
    fun getAllWorkDaysPagedByYear(year:Int): PagingSource<Int, WorkDay>?

    @Query("SELECT * FROM workday_table where year =:year and month = :month")
    fun getAllWorkDaysPagedByYearAndMonth(year:Int, month:Int): PagingSource<Int, WorkDay>?

    @Query("SELECT * FROM workday_table")
    suspend fun getAllWorkDaysForBackUp(): List<WorkDay>?

//    @Query("SELECT * FROM news_table WHERE source = :source and liked = '0'")
//    fun getNewsBySourcePaged(source: String?): PagingSource<Int, News>?


}