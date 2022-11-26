package com.edimitre.handyapp.data.model.firebase

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "auth_table")
data class AuthModel (

    @PrimaryKey(autoGenerate = false)
    var uid: String,

    var email:String,

    var password:String,

    var isSignedIn:Boolean

    ): Serializable {

    constructor(): this(
        "",
        "",
        "",
        true)


}