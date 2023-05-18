package com.edimitre.handyapp.data.view_model


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edimitre.handyapp.HandyAppEnvironment.TAG
import com.edimitre.handyapp.data.model.Cigar
import com.edimitre.handyapp.data.service.CigaretteService
import com.edimitre.handyapp.data.util.SystemService
import com.edimitre.handyapp.data.util.TimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CigaretteViewModel @Inject constructor(private val cigaretteService: CigaretteService, private val systemService: SystemService) : ViewModel() {

    var allCigars = cigaretteService.allCigars

    var gameTable = cigaretteService.gameTable

    private val mutableTimeSelected: MutableLiveData<Long> = MutableLiveData<Long>(0L)
    val timeSelected: LiveData<Long> get() = mutableTimeSelected

    private val mutableNrOfCigarsSelected: MutableLiveData<Int> = MutableLiveData<Int>(0)
    val nrOfCigars: LiveData<Int> get() = mutableNrOfCigarsSelected

    fun saveCigarette(cigar: Cigar): Job = viewModelScope.launch {


        cigaretteService.saveCigar(cigar)

    }

    fun deleteAllCigars(): Job = viewModelScope.launch {


        cigaretteService.deleteAllCigarettes()

    }

    fun distributeCigars(minutes:Long,nrOfCigars:Int): Job = viewModelScope.launch {


        cigaretteService.distributeCigars(minutes,nrOfCigars)

        activateCigarsAlarm()

    }

    private fun activateCigarsAlarm(): Job = viewModelScope.launch {

        val firstCigar = cigaretteService.getFirstCigarByAlarmTimeInMills()
        Log.e(TAG, "set cigar alarm: ${TimeUtils().getHourStringFromDateInMillis(firstCigar?.alarmInMillis!!)}", )
        systemService.setCigarAlarm(firstCigar.alarmInMillis)

    }

    fun setCigarIsWin(isWin:Boolean,cigarId:String): Job = viewModelScope.launch {


        cigaretteService.setCigarWin(isWin,cigarId)

    }

    fun clearGamePoints():Job = viewModelScope.launch{

        cigaretteService.clearGamePoints()
    }

    fun getFirstCigarByAlarmTimeInMills(): Cigar?{

        var cigar:Cigar? = null

        viewModelScope.launch { cigar = cigaretteService.getFirstCigarByAlarmTimeInMills() }

        return cigar

    }


    fun setSelectedTime(minutes:Long){

        mutableTimeSelected.value = minutes
    }

    fun setNrOfCigars(nr:Int){

        mutableNrOfCigarsSelected.value = nr

    }
}