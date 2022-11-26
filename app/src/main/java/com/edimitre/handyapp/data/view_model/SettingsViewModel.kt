package com.edimitre.handyapp.data.view_model


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.edimitre.handyapp.data.model.Note
import com.edimitre.handyapp.data.model.Settings
import com.edimitre.handyapp.data.service.NoteService
import com.edimitre.handyapp.data.service.SettingService

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(private val settingService: SettingService) : ViewModel() {

    var userSettings = settingService.userSettings

    fun saveSettings(settings: Settings): Job = viewModelScope.launch {


        settingService.saveSettings(settings)

    }

    fun deleteSettings(settings: Settings): Job = viewModelScope.launch {


        settingService.deleteSettings(settings)

    }



}