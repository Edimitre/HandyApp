package com.edimitre.handyapp.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "expense_table")
data class Expense(

    @PrimaryKey(autoGenerate = true)
    val expense_id: Long,

    val description: String,
    val year: Int,
    val month: Int,
    val date: Int,
    val hour: Int,
    val minute: Int,
    val spentValue: Double,
    @Embedded
    var shop: Shop
)
