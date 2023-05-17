package com.edimitre.handyapp.data.model

import java.io.Serializable

data class FileAsByte(

    val name: String?,
    val fileStringBase64: String?


) : Serializable {

    constructor():this(
        "",
        "",

    )


}