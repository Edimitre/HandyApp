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
    fun getAuthModelLiveData(): LiveData<AuthModel>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAuthModelOnThread(authModel: AuthModel)

    @Query("SELECT * FROM auth_table LIMIT 1")
    fun getAuthModelForBackup(): AuthModel?

    @Query("SELECT * FROM auth_table where id = '1' LIMIT 1")
    suspend fun getAuthModelOnCoroutine(): AuthModel?
}