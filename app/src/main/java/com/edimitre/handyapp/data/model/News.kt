package com.edimitre.handyapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "news_table")
data class News(

    @PrimaryKey(autoGenerate = true)
    val id: Int,

    val source:String,

    val link: String,

    val title:String,

    val paragraph:String

): Serializable {
    constructor():this(
        0,
        "",
        "",
        "",
        ""
    )
}
