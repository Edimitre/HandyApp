package com.edimitre.handyapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "note_table")
data class Note(

    @PrimaryKey(autoGenerate = true)
    val id: Int,

    val content: String

): Serializable {
    constructor():this(
        0,
        ""
    )
}
