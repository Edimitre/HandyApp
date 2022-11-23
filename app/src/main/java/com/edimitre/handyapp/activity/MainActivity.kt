package com.edimitre.handyapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.edimitre.handyapp.R
import com.edimitre.handyapp.data.util.SystemService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }
}