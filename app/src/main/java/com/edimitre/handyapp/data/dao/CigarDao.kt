package com.edimitre.handyapp.data.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.edimitre.handyapp.data.model.Cigar
import com.edimitre.handyapp.data.model.Note
import com.edimitre.handyapp.data.model.Reminder


@Dao
interface CigarDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCigar(cigar:Cigar)

    @Query("SELECT * FROM cigar_table where id =:id")
    suspend fun getCigarById(id:Int):Cigar?

    @Query("SELECT * FROM cigar_table where isActive like 1 ORDER BY alarmInMillis ASC LIMIT 1")
    fun getFirstCigarAlarmOnThread(): Cigar?

    @Query("SELECT * FROM cigar_table where isActive like 1 ORDER BY alarmInMillis ASC LIMIT 1")
    suspend fun getFirstCigarOnCoroutine(): Cigar?

    @Query("SELECT * FROM cigar_table")
    fun getAllCigarsLiveData(): LiveData<List<Cigar>>?

    @Query("DELETE FROM cigar_table")
    suspend fun deleteAllCigars()

}