package com.edimitre.handyapp.data.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.edimitre.handyapp.data.model.News


@Dao
interface NewsDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(news: News?)

    @Delete
    suspend fun delete(news: News?)

    @Query("DELETE FROM news_table where source = :source and liked = '0'")
    suspend fun deleteAllNewsBySource(source: String)

    @Query("SELECT * FROM news_table where source = :source LIMIT 1")
    suspend fun getOneBySource(source: String): News?

    @Query("SELECT * FROM news_table where liked = '1'")
    fun getAllLikedNewsPaged(): PagingSource<Int, News>?

    @Query("SELECT * FROM news_table where liked = '1'")
    suspend fun getAllLikedNewsForBackUp(): List<News>?

    @Query("SELECT * FROM news_table WHERE source = :source and liked = '0'")
    fun getNewsBySourcePaged(source: String?): PagingSource<Int, News>?

}