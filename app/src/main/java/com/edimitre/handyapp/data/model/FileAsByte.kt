package com.edimitre.handyapp.data.model

import java.io.Serializable

data class FileAsByte(

    val name: String?,
    val fileBytes: ByteArray?


) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileAsByte

        if (name != other.name) return false
        if (fileBytes != null) {
            if (other.fileBytes == null) return false
            if (!fileBytes.contentEquals(other.fileBytes)) return false
        } else if (other.fileBytes != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (fileBytes?.contentHashCode() ?: 0)
        return result
    }

}