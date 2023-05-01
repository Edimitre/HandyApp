package com.edimitre.handyapp.data.view_model


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.edimitre.handyapp.data.model.FileObject
import com.edimitre.handyapp.data.service.FileService


class FilesViewModel : ViewModel() {

    private val mutableIsFilesFragmentRefreshing: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val isFilesFragmentRefreshing: LiveData<Boolean> get() = mutableIsFilesFragmentRefreshing


    fun getAllFiles(): ArrayList<FileObject> {

        val fileService = FileService()



        return fileService.getAllFiles()
    }


    fun setIsFilesFragmentRefreshing(isRefreshing:Boolean){

        mutableIsFilesFragmentRefreshing.value = isRefreshing
    }

}