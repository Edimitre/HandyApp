package com.edimitre.handyapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "workday_table")
data class WorkDay(

    @PrimaryKey(autoGenerate = true)
    val id: Int,

    var year: Int,
    var month: Int,
    var day: Int,
    var workHours: Int,
    var activity: String,


    ) : Serializable {
    constructor() : this(
        0,
        0,
        0,
        0,
        0,
        ""
    )
}
