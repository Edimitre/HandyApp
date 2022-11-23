package com.edimitre.handyapp.fragment.login_signup

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.edimitre.handyapp.R
import com.edimitre.handyapp.data.model.AuthModel
import com.edimitre.handyapp.data.model.Shop
import com.edimitre.handyapp.databinding.AddReminderFormBinding
import com.edimitre.handyapp.databinding.FragmentSignUpBinding
import com.edimitre.handyapp.fragment.shops_expenses.SelectShopForm
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SignUpFragment : Fragment() {


    private lateinit var binding: FragmentSignUpBinding


    private lateinit var listener: AuthListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSignUpBinding.inflate(inflater, container, false)

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
        binding.btnSignUp.setOnClickListener {


            when{
                inputsAreOk() -> {
                    val email = binding.inputEmail.text.trim().toString()
                    val password = binding.inputPassword.text.trim().toString()

                    val auth = AuthModel(email,password)
                    listener.addAuthModel(auth)
                }
            }


        }
    }


    private fun inputsAreOk():Boolean{

        val email = binding.inputEmail.text.trim().toString()
        val password = binding.inputPassword.text.trim().toString()
        val confirmPassword = binding.inputConfirmPassword.text.trim().toString()

        when{
            email == "" -> {
                binding.inputEmail.error = "email can't be empty"
                return false
            }
            password == "" -> {
                binding.inputPassword.error = "password can't be empty"
                return false
            }

            confirmPassword == ""->{
                binding.inputConfirmPassword.error ="confirmed password can't be empty"

                return false
            }

            password != confirmPassword -> {
                binding.inputConfirmPassword.error = "password doesn't match"
                return false
            }

            else-> {
                return true
            }
        }

    }


    interface AuthListener {
        fun addAuthModel(authModel: AuthModel)
    }

    override fun onAttach(context: Context) {
        listener = try {
            context as AuthListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + "add listener")
        }
        super.onAttach(context)
    }


}