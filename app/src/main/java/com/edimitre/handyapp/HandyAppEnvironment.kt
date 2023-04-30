package com.edimitre.handyapp

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object HandyAppEnvironment {


    var TAG: String = "HandyApp"
    var TITLE = "HandyApp"
    var DATABASE_NAME = "HandyDB"
    var NOTIFICATION_CHANNEL_ID = "HandyNotificationChannel"
    var NOTIFICATION_NUMBER_ID = 1
    var FILES_STORAGE_DIRECTORY = "HandyAppWorkStorage"

}