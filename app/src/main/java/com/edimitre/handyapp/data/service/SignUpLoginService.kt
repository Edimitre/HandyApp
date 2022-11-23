package com.edimitre.handyapp.data.service

import android.util.Log
import com.edimitre.handyapp.HandyAppEnvironment
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject


class SignUpLoginService @Inject constructor(private val auth: FirebaseAuth) {


    fun doSignUp(email: String, password: String) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    Log.e(HandyAppEnvironment.TAG, "sign up success for user with email : ${auth.currentUser!!.email}" )

                }else{


                    Log.e(HandyAppEnvironment.TAG, "signup error reason : ${task.exception} " )
                }
            }
    }

}







