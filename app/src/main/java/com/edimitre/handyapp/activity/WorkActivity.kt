package com.edimitre.handyapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.edimitre.handyapp.R
import com.edimitre.handyapp.fragment.work_related.WorkDaysFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work)

        displayFragment(WorkDaysFragment())
    }


    private fun displayFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frag_container, fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()
    }
}