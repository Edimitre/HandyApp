package com.edimitre.handyapp.data.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.data.model.Reminder
import com.edimitre.handyapp.data.service.ReminderService
import com.edimitre.handyapp.data.util.SystemService

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(private val reminderService: ReminderService) :
    ViewModel() {

    @Inject
    lateinit var systemService: SystemService

    var reminderList = reminderService.allReminders


    fun saveReminder(reminder: Reminder): Job = viewModelScope.launch {
        reminderService.save(reminder)
    }

    fun deleteReminder(reminder: Reminder): Job = viewModelScope.launch {

        reminderService.delete(reminder)
        activateNextReminder()
    }

    private fun activateNextReminder() {
        systemService.cancelAllAlarms()

        viewModelScope.launch {


            val firstReminder = reminderService.getFirstReminderOnCoroutine()

            when {
                firstReminder != null -> {
                    systemService.setAlarm(firstReminder.alarmTimeInMillis)

                }
                else -> {

                    Log.e(HandyAppEnvironment.TAG, "empty reminders")
                }
            }
        }

    }

}