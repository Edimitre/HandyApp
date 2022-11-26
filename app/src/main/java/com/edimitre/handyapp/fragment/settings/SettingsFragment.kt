package com.edimitre.handyapp.fragment.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.lifecycle.ViewModelProvider
import com.edimitre.handyapp.activity.LoginSignUpActivity
import com.edimitre.handyapp.data.model.Settings
import com.edimitre.handyapp.data.util.SystemService
import com.edimitre.handyapp.data.view_model.SettingsViewModel
import com.edimitre.handyapp.databinding.FragmentSettingsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : BottomSheetDialogFragment() {


    private var listener: ThemeChangeListener? = null

    private lateinit var settingsViewModel: SettingsViewModel

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var systemService: SystemService

    lateinit var binding: FragmentSettingsBinding

    var userSettings: Settings? = null

    private var isBackUpChecked = false
    private var isDarkThemeChecked = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()

        observeUserSettings()

        setListeners()


    }

    private fun initViewModel() {

        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
    }



    private fun setListeners() {


        binding.backUpSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked -> // do something, the isChecked will be
            isBackUpChecked = when {
                isChecked -> {
                    listener!!.setBackupEnabled(true)
                    true
                }
                else -> {
                    false
                }
            }
        })

        binding.darkThemeSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->

            isDarkThemeChecked = when {
                isChecked -> {
                    true
                }
                else -> {
                    false
                }
            }
        })

        binding.btnSaveSettings.setOnClickListener {
            saveSettings()
            dismiss()
        }
    }

    private fun saveSettings() {

        listener!!.saveSettings(Settings(1,isBackUpChecked,isDarkThemeChecked))

//        when{
//            isDarkThemeChecked -> {
//                listener!!.changeTheme("Dark")
//            }else -> {
//                listener!!.changeTheme("Light")
//            }
//        }
    }


    private fun observeUserSettings() {
        settingsViewModel.userSettings.observe(this) {
            userSettings = it
            setSwitchMode(userSettings!!)
        }
    }

    private fun setSwitchMode(userSettings: Settings) {

        binding.backUpSwitch.isChecked = userSettings.isBackupEnabled
        binding.darkThemeSwitch.isChecked = userSettings.isDarkThemeEnabled

    }

    interface ThemeChangeListener {
        fun setBackupEnabled(enabled:Boolean)
        fun saveSettings(settings: Settings)
        fun changeTheme(theme: String)
    }

    override fun onAttach(context: Context) {
        listener = try {
            context as ThemeChangeListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + "add listener")
        }
        super.onAttach(context)
    }

}