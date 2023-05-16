package com.edimitre.handyapp.data.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.edimitre.handyapp.data.model.MemeTemplate


@Dao
interface MemeTemplateDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(memeTemplate: MemeTemplate)

    @Delete
    suspend fun delete(memeTemplate: MemeTemplate)

    @Query("SELECT * FROM meme_template_table")
    fun getAllPaged(): PagingSource<Int, MemeTemplate>?


    @Query("SELECT * FROM meme_template_table WHERE name LIKE '%' || :name || '%'")
    fun getByNamePaged(name: String): PagingSource<Int, MemeTemplate>?


    //    @Query("SELECT * FROM image WHERE name IN (:arg0)")
//    fun findByNameContaining(imageTestIds: List<Int>): LiveData<List<MemeTemplate>>


}