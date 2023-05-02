package com.edimitre.handyapp.data.view_model


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.edimitre.handyapp.data.model.Cigar
import com.edimitre.handyapp.data.model.Note
import com.edimitre.handyapp.data.service.CigaretteService
import com.edimitre.handyapp.data.service.NoteService

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CigaretteViewModel @Inject constructor(private val cigaretteService: CigaretteService) : ViewModel() {

    var allCigars = cigaretteService.allCigars

    private val mutableTimeSelected: MutableLiveData<Long> = MutableLiveData<Long>(0L)
    val timeSelected: LiveData<Long> get() = mutableTimeSelected

    fun saveCigarette(cigar: Cigar): Job = viewModelScope.launch {


        cigaretteService.saveCigar(cigar)

    }

    fun deleteAllCigars(): Job = viewModelScope.launch {


        cigaretteService.deleteAllCigarettes()

    }

    fun distributeCigars(minutes:Long): Job = viewModelScope.launch {


        cigaretteService.distributeCigars(minutes)

    }

    fun getFirstCigarByAlarmTimeInMills(): Cigar?{

        var cigar:Cigar? = null

        viewModelScope.launch { cigar = cigaretteService.getFirstCigarByAlarmTimeInMills() }

        return cigar

    }


    fun setSelectedTime(minutes:Long){

        mutableTimeSelected.value = minutes
    }

}