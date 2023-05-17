package com.edimitre.handyapp.data.util

import android.content.Context
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.data.model.FileAsByte
import com.edimitre.handyapp.data.model.MemeTemplate
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

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

    fun setMemeTemplateList(memeTemplateList:List<MemeTemplate>){

        val sharedPreference = context.getSharedPreferences(HandyAppEnvironment.TITLE,Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString("meme_templates", Gson().toJson(memeTemplateList))
        editor.apply()
    }

    fun getMemeTemplateList(): List<MemeTemplate>? {

        val sharedPreference =
            context.getSharedPreferences(HandyAppEnvironment.TITLE, Context.MODE_PRIVATE)

        val string = sharedPreference.getString("meme_templates", "")

        val gson = Gson()
        val itemType = object : TypeToken<List<MemeTemplate>>() {}.type

        return gson.fromJson(string, itemType)

    }

    fun setWorkFilesList(filesAsByteList:List<FileAsByte>){

        val sharedPreference = context.getSharedPreferences(HandyAppEnvironment.TITLE,Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString("work_files", Gson().toJson(filesAsByteList))
        editor.apply()
    }

    fun getWorkFileList(): List<FileAsByte>? {

        val sharedPreference =
            context.getSharedPreferences(HandyAppEnvironment.TITLE, Context.MODE_PRIVATE)

        val string = sharedPreference.getString("work_files", "")

        val gson = Gson()
        val itemType = object : TypeToken<List<FileAsByte>>() {}.type

        return gson.fromJson(string, itemType)

    }


}