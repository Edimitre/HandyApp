package com.edimitre.handyapp.data.model.firebase

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "auth_table")
data class AuthModel(

    @PrimaryKey(autoGenerate = true)
    var id:Int,

    var uid: String,

    var email: String,

    var password: String,

    var isSignedIn: Boolean,

    var isBackupEnabled: Boolean,

    var isDarkThemeEnabled: Boolean,

    var isNotificationEnabled :Boolean

) : Serializable {

    constructor() : this(
        0,
        "",
        "",
        "",
        true,
        false,
        false,
        false
    )


}