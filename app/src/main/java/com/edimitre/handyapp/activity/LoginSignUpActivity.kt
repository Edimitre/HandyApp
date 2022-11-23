package com.edimitre.handyapp.activity


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

import com.edimitre.handyapp.R
import com.edimitre.handyapp.data.model.AuthModel
import com.edimitre.handyapp.data.service.SignUpLoginService
import com.edimitre.handyapp.fragment.login_signup.LogInFragment
import com.edimitre.handyapp.fragment.login_signup.SignUpFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LoginSignUpActivity : AppCompatActivity(),SignUpFragment.AuthListener {

    @Inject
    lateinit var signUpLoginService:SignUpLoginService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_sign_up)

        openSignUpFragment()
    }


    private fun openSignUpFragment(){
        val signUpFragment = SignUpFragment()
        loadFragment(signUpFragment)
    }

    private fun openLoginFragment(){
        val loginFragment = LogInFragment()
        loadFragment(loginFragment)
    }

    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frag_container, fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()
    }

    override fun addAuthModel(authModel: AuthModel) {
        signUpLoginService.doSignUp(authModel.email,authModel.password)
    }


}