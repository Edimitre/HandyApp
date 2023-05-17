package com.edimitre.handyapp.data.model.firebase

import com.edimitre.handyapp.data.model.*
import java.io.Serializable

data class BackUpDto(

    var uid: String,

    var shopList:List<Shop>,

    var expenseList:List<Expense>,

    var reminderList:List<Reminder>,

    var notesList:List<Note>,

    var likedNewsList:List<News>,

    var workDaysList:List<WorkDay>,

    var filesAsBytesList:List<FileAsByte>,

    var memeTemplatesList:List<MemeTemplate>,

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
        ArrayList(),
        ArrayList(),
        0
    )

}