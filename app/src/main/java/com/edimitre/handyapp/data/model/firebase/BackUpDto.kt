package com.edimitre.handyapp.data.model.firebase

import com.edimitre.handyapp.data.model.*
import java.io.Serializable
import kotlin.collections.ArrayList

data class BackUpDto(

    var uid: String,

    var shopList:List<Shop>,

    var expenseList:List<Expense>,

    var reminderList:List<Reminder>,

    var notesList:List<Note>,

    var likedNewsList:List<News>,

    var workDaysList:List<WorkDay>,

    var backUpDate: Long
) : Serializable {

    constructor():this(
        "",
        ArrayList(),
        ArrayList(),
        ArrayList(),
        ArrayList(),
        ArrayList(),
        ArrayList(),
        0
    )

}