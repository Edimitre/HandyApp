package com.edimitre.handyapp.data.model.firebase

import com.edimitre.handyapp.data.model.Expense
import com.edimitre.handyapp.data.model.Note
import com.edimitre.handyapp.data.model.Reminder
import com.edimitre.handyapp.data.model.Shop
import java.io.Serializable
import kotlin.collections.ArrayList

data class BackUpDto(

    var uid: String,

    var shopList:List<Shop>,

    var expenseList:List<Expense>,

    var reminderList:List<Reminder>,

    var notesList:List<Note>,

    var backUpDate: Long
) : Serializable {

    constructor():this(
        "",
        ArrayList(),
        ArrayList(),
        ArrayList(),
        ArrayList(),
        0
    )

}