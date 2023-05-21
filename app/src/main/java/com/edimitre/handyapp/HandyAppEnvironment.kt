package com.edimitre.handyapp

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object HandyAppEnvironment {


    val TAG: String = "HandyApp"
    val TITLE = "HandyApp"
    val DATABASE_NAME = "HandyDB"
    val NOTIFICATION_CHANNEL_ID = "HandyAppNotificationChannel"
    val NOTIFICATION_NUMBER_ID = 111
    val NOTIFICATION_ALARM_CHANNEL_ID = "HandyAppNotificationAlarmChannel"
    val NOTIFICATION_ALARM_NUMBER_ID = 222
    val FILES_STORAGE_DIRECTORY = "HandyAppWorkStorage"
    val MEME_STORAGE_DIRECTORY = "MemeTemplates"
    val TEMP_STORAGE_DIRECTORY = "TempDirectory"

}