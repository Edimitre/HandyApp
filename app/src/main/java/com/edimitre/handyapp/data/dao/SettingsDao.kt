package com.edimitre.handyapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.edimitre.handyapp.data.model.Settings


@Dao
interface SettingsDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveSettingsOnThread(settings: Settings)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: Settings)

    @Query("SELECT * FROM settings_table LIMIT 1")
    fun getSettingOnThread(): Settings?

    @Query("SELECT * FROM settings_table LIMIT 1")
    fun getSettingsLiveData(): LiveData<Settings?>

    @Delete
    suspend fun deleteSettings(settings: Settings)


}