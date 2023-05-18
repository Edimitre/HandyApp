package com.edimitre.handyapp.data.view_model


import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.edimitre.handyapp.data.model.MemeTemplate
import com.edimitre.handyapp.data.service.MemeTemplateService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemeTemplateViewModel @Inject constructor(private val memeTemplateService: MemeTemplateService) :
    ViewModel() {


    private var croppedImageMutable:MutableLiveData<Bitmap>? = MutableLiveData<Bitmap>()
    val croppedImage: LiveData<Bitmap>? get() = croppedImageMutable

    fun saveMemeTemplate(memeTemplate: MemeTemplate): Job = viewModelScope.launch {

        memeTemplateService.saveMemeTemplate(memeTemplate)

    }


    fun deleteMemeTemplate(memeTemplate: MemeTemplate): Job = viewModelScope.launch {

        memeTemplateService.deleteMemeTemplate(memeTemplate)

    }


    fun getAllTemplatesPaged(): Flow<PagingData<MemeTemplate>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            pagingSourceFactory = { memeTemplateService.getAllMemeTemplatesPaged()!! })
            .flow
            .cachedIn(viewModelScope)
    }

    fun getAllTemplatesByName(name: String): Flow<PagingData<MemeTemplate>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            pagingSourceFactory = { memeTemplateService.getMemeTemplatesByName(name)!! })
            .flow
            .cachedIn(viewModelScope)
    }


    fun setCroppedImage(croppedBitmap:Bitmap){
        croppedImageMutable?.value = croppedBitmap
    }



}