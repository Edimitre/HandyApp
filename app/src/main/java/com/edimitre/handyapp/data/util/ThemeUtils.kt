package com.edimitre.handyapp.data.util

import androidx.appcompat.app.AppCompatActivity
import com.edimitre.handyapp.R

class ThemeUtils {


    companion object MyValues {
        const val light: Int = 0
        const val dark: Int = 1
        var theme: Int = 0
    }


//    fun changeToTheme(activity: AppCompatActivity, myTheme: Int) {
//        theme = myTheme
//
//        activity.recreate()
//    }

//    fun setThemeOnCreate(activity: AppCompatActivity) {
//
//        when (theme) {
//            light -> {
//                activity.setTheme(R.style.Theme_HandyApp_Day)
//            }
//            dark -> {
//                activity.setTheme(R.style.Theme_HandyApp_Night)
//            }
//        }
//
//
//    }
}
