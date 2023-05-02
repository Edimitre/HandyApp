package com.edimitre.handyapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "cigar_table")
data class Cigar(

    @PrimaryKey(autoGenerate = true)
    val id: Int,

    var isActive:Boolean,

    var isWin:Boolean?,

    val alarmInMillis: Long

): Serializable {
    constructor():this(
        0,
        false,
        null,
        0L
    )
}
