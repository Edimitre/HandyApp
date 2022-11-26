package com.edimitre.handyapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "settings_table")
data class Settings(

    @PrimaryKey(autoGenerate = true)
    val settings_id: Long,

    var isBackupEnabled: Boolean,

    var isDarkThemeEnabled: Boolean
): Serializable {
    constructor():this(
        0,
        false,
        false
    )
}

