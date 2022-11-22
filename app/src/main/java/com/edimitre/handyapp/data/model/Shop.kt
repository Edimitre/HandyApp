package com.edimitre.handyapp.data.model


import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(
    tableName = "shop_table", indices = [Index(
        value = ["shop_id", "shop_name"],
        unique = true
    )]
)
data class Shop(


    @PrimaryKey(autoGenerate = true)
    val shop_id: Long,


    val shop_name: String,

    ) : Serializable {

    constructor() : this(
        0,
        ""
    )
}
