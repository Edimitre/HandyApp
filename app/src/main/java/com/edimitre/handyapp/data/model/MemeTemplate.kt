package com.edimitre.handyapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meme_template_table")
data class MemeTemplate (

    @PrimaryKey(autoGenerate = true)
    val id: Int,

    val name:String,

    val imgBase64:String
)