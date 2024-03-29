package com.edimitre.handyapp.data.model.firebase

import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity(tableName = "auth_table")
data class AuthModel(

    @PrimaryKey(autoGenerate = true)
    var id: Int,

    var uid: String,

    var email: String,

    var password: String,

    var isSignedIn: Boolean,

    var isBackupEnabled: Boolean,

    var isDarkThemeEnabled: Boolean,

    var isNotificationEnabled: Boolean,

    var isWorkNotificationEnabled: Boolean

)