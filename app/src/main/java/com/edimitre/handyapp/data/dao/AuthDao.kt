package com.edimitre.handyapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.edimitre.handyapp.data.model.firebase.AuthModel



@Dao
interface AuthDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(authModel: AuthModel)

    @Delete
    suspend fun delete(authModel: AuthModel)

    @Delete
    fun deleteOnThread(authModel: AuthModel)

    @Query("SELECT * FROM auth_table LIMIT 1")
    fun getMainUserLiveData(): LiveData<AuthModel>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveMainUserOnThread(authModel: AuthModel)

    @Query("SELECT * FROM auth_table LIMIT 1")
    fun getMainUserForBackup(): AuthModel?

    @Query("SELECT * FROM auth_table LIMIT 1")
    suspend fun getMainUserOnCoroutine(): AuthModel?
}