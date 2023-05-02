package com.edimitre.handyapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "cigar_game_table")
data class CigarGameTable(

    @PrimaryKey(autoGenerate = true)
    val id: Int,

    val year:Int,

    val month:Int,

    var pointsWon:Int,

    var pointsLose:Int,

    var isWinning: Boolean


): Serializable {
    constructor():this(
        0,
        0,
        0,
        0,
        0,
        false
    )
}
