package com.edimitre.handyapp.data.service

import android.os.Environment
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.data.model.FileObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import java.io.File


class FileService {



   fun getAllFiles(): ArrayList<FileObject> {

        val folderName = HandyAppEnvironment.FILES_STORAGE_DIRECTORY
        val directory = File("${Environment.getExternalStorageDirectory()}/$folderName")

        val files = directory.listFiles()

        val listFile = arrayListOf<FileObject>()


        if (files != null) {
            for (i in files.indices) {

                listFile.add(FileObject(files[i].name , files[i]))

            }
        }

        return listFile
    }
}