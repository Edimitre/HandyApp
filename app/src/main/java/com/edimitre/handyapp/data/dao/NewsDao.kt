package com.edimitre.handyapp.data.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.edimitre.handyapp.data.model.News


@Dao
interface NewsDao {


    @Insert
    suspend fun insert(news: News?)

    @Delete
    suspend fun delete(news: News?)

    @Query("DELETE FROM news_table")
    suspend fun deleteAllNews()

    @Query("SELECT * FROM news_table")
    suspend fun getAllNewsList(): List<News?>?

    @Query("SELECT * FROM news_table where id = '1' LIMIT 1")
    suspend fun getOne(): News?

    @Query("SELECT * FROM news_table")
    fun getAllNewsPaged(): PagingSource<Int, News>?

    @Query("SELECT * FROM news_table WHERE source LIKE :source")
    fun getNewsBySourcePaged(source: String?): PagingSource<Int, News>?

}