package com.edimitre.handyapp.data.service

import com.edimitre.handyapp.data.dao.ReminderNotesDao
import com.edimitre.handyapp.data.model.Reminder

import javax.inject.Inject


class ReminderService @Inject constructor(private val reminderDao: ReminderNotesDao) {

    var allReminders = reminderDao.getAllRemindersLiveData()

//    var firstReminder = reminderDao.getFirstReminderLiveData()


    suspend fun save(reminder: Reminder) {

        reminderDao.saveReminder(reminder)

    }

    suspend fun delete(reminder: Reminder) {

        reminderDao.deleteReminder(reminder)

    }

//    suspend fun getFirstReminderOnCoroutine(): Reminder? {
//        return reminderDao.getFirstReminderOnCoroutine()
//    }

}