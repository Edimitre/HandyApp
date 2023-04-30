package com.edimitre.handyapp.data.view_model


import androidx.lifecycle.ViewModel
import com.edimitre.handyapp.data.model.FileObject
import com.edimitre.handyapp.data.service.FileService


class FilesViewModel : ViewModel() {


    fun getAllFiles(): ArrayList<FileObject> {

        val fileService = FileService()



        return fileService.getAllFiles()
    }


}