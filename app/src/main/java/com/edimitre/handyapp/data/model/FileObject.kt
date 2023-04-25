package com.edimitre.handyapp.data.model

import java.io.File
import java.io.Serializable

data class FileObject(

    val name: String?,
    val actualFile: File?


) : Serializable {

    constructor() : this(
        null,
        null
    )

}