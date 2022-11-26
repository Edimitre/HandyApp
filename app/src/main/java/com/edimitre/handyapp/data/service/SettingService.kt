package com.edimitre.handyapp.data.service


import com.edimitre.handyapp.data.dao.SettingsDao
import com.edimitre.handyapp.data.model.Settings
import javax.inject.Inject

class SettingService @Inject constructor(private val settingsDao: SettingsDao) {


    var userSettings = settingsDao.getSettingsLiveData()

    suspend fun saveSettings(settings: Settings) {
        settingsDao.saveSettings(settings)

    }

    suspend fun deleteSettings(settings: Settings) {

        settingsDao.deleteSettings(settings)
    }


}