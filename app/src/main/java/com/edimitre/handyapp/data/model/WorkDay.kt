package com.edimitre.handyapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.Date

@Entity(tableName = "workday_table")
data class WorkDay(

    @PrimaryKey(autoGenerate = true)
    val id: Int,

    val year:Int,
    val month:Int,
    val day:Int,
    val workHours:Int,
    val activity :String,



): Serializable {
    constructor():this(
        0,
        0,
        0,
        0,
        0,
        ""
    )
}
