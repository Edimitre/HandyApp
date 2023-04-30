package com.edimitre.handyapp.data.service

import android.os.Environment
import android.util.Log
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.data.model.FileAsByte
import com.edimitre.handyapp.data.model.FileObject
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths


class FileService {


    private val storageDirectory =
        File("${Environment.getExternalStorageDirectory()}/${HandyAppEnvironment.FILES_STORAGE_DIRECTORY}")


    fun createStorageDirectory() {

        val folderName = HandyAppEnvironment.FILES_STORAGE_DIRECTORY

        val file = File("${Environment.getExternalStorageDirectory()}/$folderName")

        var folderCreated = false

        if (!file.exists()) {
            folderCreated = file.mkdir()
        }
        if (folderCreated) {
            Log.e(HandyAppEnvironment.TAG, "folder created")
        } else {

            Log.e(HandyAppEnvironment.TAG, "folder not created because it exists")
        }
    }

    fun getAllFiles(): ArrayList<FileObject> {

        val folderName = HandyAppEnvironment.FILES_STORAGE_DIRECTORY
        val directory = File("${Environment.getExternalStorageDirectory()}/$folderName")

        val files = directory.listFiles()

        val listFile = arrayListOf<FileObject>()


        if (files != null) {
            for (i in files.indices) {

                listFile.add(FileObject(files[i].name, files[i]))

            }
        }

        return listFile
    }


    fun getFilesAsBytesList(): List<FileAsByte> {

        val allFiles = getAllFiles()

        val filesAsBytesList = arrayListOf<FileAsByte>()

        allFiles.forEach { file ->
            run {


                val fileBytes = Files.readAllBytes(Paths.get(file.actualFile!!.path))

                val fileAsByte = FileAsByte(file.name, fileBytes)

                filesAsBytesList.add(fileAsByte)
            }
        }

        return filesAsBytesList
    }


    fun createLocalFiles(fileAsByteList: List<FileAsByte>) {

        fileAsByteList.forEach { file ->

            val localFile = File("${storageDirectory}/${file.name}")

            if (!localFile.exists()) {
                FileOutputStream("${storageDirectory}/${file.name}").use { fos -> fos.flush() }
            }

        }

    }

}

