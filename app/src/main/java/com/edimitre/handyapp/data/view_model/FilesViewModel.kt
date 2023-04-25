package com.edimitre.handyapp.data.view_model


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.edimitre.handyapp.data.model.FileObject
import com.edimitre.handyapp.data.model.WorkDay
import com.edimitre.handyapp.data.service.FileService
import com.edimitre.handyapp.data.service.WorkDayService
import com.edimitre.handyapp.data.util.TimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject



class FilesViewModel : ViewModel() {



    fun getAllFiles(): ArrayList<FileObject> {

        val fileService = FileService()



        return fileService.getAllFiles()
    }


}