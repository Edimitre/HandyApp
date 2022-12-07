package com.edimitre.handyapp.fragment.main_fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.edimitre.handyapp.HandyAppEnvironment.TAG
import com.edimitre.handyapp.data.util.SystemService
import com.edimitre.handyapp.data.view_model.MainViewModel
import com.edimitre.handyapp.databinding.FragmentSettingsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : BottomSheetDialogFragment() {

    private lateinit var listener: ImportDbListener

    private val mainViewModel: MainViewModel by activityViewModels()

    @Inject
    lateinit var systemService: SystemService

    lateinit var binding: FragmentSettingsBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeUserSettings()

        setListeners()

    }

    private fun setListeners() {

        binding.backUpSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked -> // do something, the isChecked will be


            mainViewModel.selectBackupEnabled(isChecked)

            when {
                !mainViewModel.isAuthenticated() -> {
                    mainViewModel.selectBackupEnabled(false)
                    Toast.makeText(
                        requireContext(),
                        "You need to be logged in to use this feature",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {

                    lifecycleScope.launch {
                        val authModel = mainViewModel.getAuthModel()
                        authModel!!.isBackupEnabled = isChecked

                        mainViewModel.saveAuth(authModel)
                    }

                }
            }
        })

        binding.darkThemeSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->

            mainViewModel.selectDarkTheme(isChecked)
            lifecycleScope.launch {
                val auth = mainViewModel.getAuthModel()
                auth!!.isDarkThemeEnabled = isChecked
                mainViewModel.saveAuth(auth)
            }
        })


        binding.btnLogin.setOnClickListener {
            mainViewModel.selectFragment(SignUpFragment())
            dismiss()
        }

        binding.btnLogout.setOnClickListener {
            mainViewModel.doLogOut()
            dismiss()
        }

        binding.importRow.setOnClickListener {
            listener.importDb()
        }
    }

    private fun observeUserSettings() {


        if (!mainViewModel.isAuthenticated()) {
            binding.logOutText.visibility = View.GONE
            binding.btnLogout.visibility = View.GONE
            binding.importRow.visibility = View.GONE
        }else{
            binding.btnLogin.visibility = View.GONE
            binding.loginText.visibility = View.GONE
        }

        setSwitchMode()

    }

    private fun setSwitchMode() {


        activity?.let {
            mainViewModel.isBackupSelected.observe(it, Observer { checked ->
                binding.backUpSwitch.isChecked = checked
            })
        }

        activity?.let {
            mainViewModel.isDarkSelected.observe(it, Observer { check ->
                binding.darkThemeSwitch.isChecked = check
            })
        }

    }


    interface ImportDbListener {
        fun importDb()
    }

    override fun onAttach(context: Context) {
        listener = try {
            context as ImportDbListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + "add listener")
        }
        super.onAttach(context)
    }

}