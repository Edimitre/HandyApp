package com.edimitre.handyapp.data.util

import android.content.Context
import com.edimitre.handyapp.HandyAppEnvironment

class SharedPrefUtil (private val context: Context){



    fun setCigarId(id:Int){

        val sharedPreference = context.getSharedPreferences(HandyAppEnvironment.TITLE,Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putInt("cigar_id",id)
        editor.apply()
    }

    fun getCigarId():Int{

        val sharedPreference = context.getSharedPreferences(HandyAppEnvironment.TITLE,Context.MODE_PRIVATE)

        return sharedPreference.getInt("cigar_id",0)

    }



}