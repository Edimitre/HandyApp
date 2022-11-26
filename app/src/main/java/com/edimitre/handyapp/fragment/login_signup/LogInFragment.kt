package com.edimitre.handyapp.fragment.login_signup

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.edimitre.handyapp.R
import com.edimitre.handyapp.databinding.FragmentLogInBinding
import com.edimitre.handyapp.databinding.FragmentSignUpBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogInFragment : Fragment() {

    private lateinit var binding: FragmentLogInBinding


    private lateinit var listener: LoginListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLogInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragment()
    }

    private fun initFragment(){


        setListeners()
    }

    private fun setListeners(){
        binding.btnLogin.setOnClickListener {


            when{
                inputsAreOk() -> {
                    val email = binding.inputLoginEmail.text.trim().toString()
                    val password = binding.inputLoginPassword.text.trim().toString()
                    listener.executeLogin(email, password)
                }
            }
        }

        binding.signUpText.setOnClickListener {
            listener.switchSignUpFragment()
        }
    }

    private fun inputsAreOk():Boolean{

        val email = binding.inputLoginEmail.text.trim().toString()
        val password = binding.inputLoginPassword.text.trim().toString()


        when{
            email == "" -> {
                binding.inputLoginEmail.error = "email can't be empty"
                return false
            }
            password == "" -> {
                binding.inputLoginPassword.error = "password can't be empty"
                return false
            }

            else-> {
                return true
            }
        }

    }



    interface LoginListener {
        fun executeLogin(email:String, password:String)
        fun switchSignUpFragment()
    }

    override fun onAttach(context: Context) {
        listener = try {
            context as LoginListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + "add listener")
        }
        super.onAttach(context)
    }
}