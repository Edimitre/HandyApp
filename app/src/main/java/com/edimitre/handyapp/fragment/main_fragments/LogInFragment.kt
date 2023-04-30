package com.edimitre.handyapp.fragment.main_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.edimitre.handyapp.data.view_model.MainViewModel
import com.edimitre.handyapp.databinding.FragmentLogInBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogInFragment : Fragment() {

    private lateinit var binding: FragmentLogInBinding

    private val mainViewModel: MainViewModel by activityViewModels()

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

    private fun initFragment() {

        setListeners()

        observeData()

        observeLoading()
    }

    private fun setListeners() {
        binding.btnLogin.setOnClickListener {

            when {
                inputsAreOk() -> {
                    val email = binding.inputLoginEmail.text.trim().toString()
                    val password = binding.inputLoginPassword.text.trim().toString()
                    mainViewModel.doLogin(email, password)
                }
            }
        }

        binding.signUpText.setOnClickListener {
            mainViewModel.setActiveFragment(SignUpFragment())
        }
    }

    private fun inputsAreOk(): Boolean {

        val email = binding.inputLoginEmail.text.trim().toString()
        val password = binding.inputLoginPassword.text.trim().toString()


        when {
            email == "" -> {
                binding.inputLoginEmail.error = "email can't be empty"
                return false
            }
            password == "" -> {
                binding.inputLoginPassword.error = "password can't be empty"
                return false
            }

            else -> {
                return true
            }
        }

    }

    private fun observeData() {

        activity?.let {
            mainViewModel.isDoingWork.observe(it) { doingWork ->
                when {
                    doingWork -> {

                    }
                }
            }
        }
    }

    private fun observeLoading() {

        mainViewModel.isDoingWork.observe(viewLifecycleOwner) {

            setLoadingMode(it)
        }
    }

    private fun setLoadingMode(value: Boolean) {
        when {
            value -> {

                binding.loadingSpinner.visibility = View.VISIBLE
                binding.sampleText.visibility = View.INVISIBLE
                binding.inputLoginEmail.visibility = View.INVISIBLE
                binding.inputLoginPassword.visibility = View.INVISIBLE
                binding.btnLogin.visibility = View.INVISIBLE
                binding.signUpText.visibility = View.INVISIBLE
            }
            else -> {

                binding.loadingSpinner.visibility = View.INVISIBLE
                binding.sampleText.visibility = View.VISIBLE
                binding.inputLoginEmail.visibility = View.VISIBLE
                binding.inputLoginPassword.visibility = View.VISIBLE
                binding.btnLogin.visibility = View.VISIBLE
                binding.signUpText.visibility = View.VISIBLE

            }
        }

    }


}