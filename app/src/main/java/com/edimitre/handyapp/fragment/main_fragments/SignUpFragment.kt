package com.edimitre.handyapp.fragment.main_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.edimitre.handyapp.data.view_model.MainViewModel
import com.edimitre.handyapp.databinding.FragmentSignUpBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SignUpFragment : Fragment() {


    private lateinit var binding: FragmentSignUpBinding




    private val mainViewModel: MainViewModel by activityViewModels()

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

        observeData()
    }

    private fun setListeners(){
        binding.btnSignUp.setOnClickListener {
            when{
                inputsAreOk() -> {
                    val email = binding.inputEmail.text.trim().toString()
                    val password = binding.inputPassword.text.trim().toString()
                    mainViewModel.signUpUser(email, password)
                }
            }
        }

        binding.alreadyUser.setOnClickListener {
            mainViewModel.selectFragment(LogInFragment())
        }
    }

    private fun observeData(){


        activity?.let {
            mainViewModel.isDoingWork.observe(it, Observer { doingWork ->
                when{
                    doingWork -> {

                    }
                }
            })
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





}