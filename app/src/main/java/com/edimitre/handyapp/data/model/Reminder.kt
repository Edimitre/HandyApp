package com.edimitre.handyapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "reminder_table")
data class Reminder(

    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var alarmTimeInMillis: Long,
    var description: String,
    var isActive: Boolean

):Serializable {
    override fun toString(): String {
        return "Reminder{" +
                "id=" + id +
                ", alarmTimeInMillis=" + alarmTimeInMillis +
                ", description='" + description + '\'' +
                ", isActive=" + isActive +
                '}'
    }
}