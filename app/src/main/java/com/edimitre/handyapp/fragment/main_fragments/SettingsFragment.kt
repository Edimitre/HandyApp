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
import androidx.work.WorkManager
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.HandyAppEnvironment.TAG
import com.edimitre.handyapp.data.util.SystemService
import com.edimitre.handyapp.data.view_model.MainViewModel
import com.edimitre.handyapp.databinding.FragmentSettingsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

        setButtonVisibility()

        observeSwitchesStatus()

        setListeners()

    }

    private fun setListeners() {

        binding.backUpSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->


            mainViewModel.setBackupEnabled(isChecked)

            when {
                !mainViewModel.isAuthenticated() -> {
                    mainViewModel.setBackupEnabled(false)
                    Toast.makeText(
                        requireContext(),
                        "You need to be logged in to use this feature",
                        Toast.LENGTH_SHORT
                    ).show()

                    setBackupWorker(false)
                }
                else -> {

                    lifecycleScope.launch {

                        runBlocking {
                            val authModel = mainViewModel.getAuthModel()
                            authModel!!.isBackupEnabled = isChecked

                            mainViewModel.saveAuth(authModel)

                            when {
                                isChecked -> {askForImportBackupStart()}
                            }

                        }

                    }

                }
            }
        })

        binding.darkThemeSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->

            mainViewModel.setDarkTheme(isChecked)

            lifecycleScope.launch {

                runBlocking {
                    val auth = mainViewModel.getAuthModel()
                    auth!!.isDarkThemeEnabled = isChecked
                    mainViewModel.saveAuth(auth)
                }

            }
        })

        binding.btnLogin.setOnClickListener {
            mainViewModel.setActiveFragment(SignUpFragment())
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

    private fun askForImportBackupStart() {

        val dialog = MaterialAlertDialogBuilder(requireContext())
        dialog.setTitle(HandyAppEnvironment.TITLE)
        dialog.setMessage(
            "do you want to perform a restore of your data right now ?..\n" +
                    "select yes only if app is installed fresh and you have the old data \n" +
                    "select no if you are a fresh registered user \n" +
                    "click outside to close the warning and cancel all \n" +
                    "you can retry at another time.."

        )
        dialog.setPositiveButton("Yes") { dlg, _ ->


            listener.importDb().also {
                setBackupWorker(true)
            }

            dlg.dismiss()
            dismiss()
        }

        dialog.setNegativeButton("No") { _, _ ->

            setBackupWorker(true)
            dismiss()
        }


        dialog.show()
    }

    private fun setButtonVisibility() {


        if (!mainViewModel.isAuthenticated()) {
            binding.logOutText.visibility = View.GONE
            binding.btnLogout.visibility = View.GONE
            binding.importRow.visibility = View.GONE
        } else {
            binding.btnLogin.visibility = View.GONE
            binding.loginText.visibility = View.GONE
        }


    }

    private fun observeSwitchesStatus() {


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

    private fun setBackupWorker(active: Boolean) {

        val backupWorkList =
            WorkManager.getInstance(requireContext()).getWorkInfosByTag("backup_worker").get()

        when (active) {
            true -> {
                if (backupWorkList.isNotEmpty()) {
                    val workInfo = backupWorkList[0]
                    val status = workInfo.state.name
                    Log.e(TAG, "status of backup work $status")
                    if (status != "ENQUEUED") {
                        systemService.startBackupWorker()
                    }
                } else {
                    systemService.startBackupWorker()
                }
            }
            false -> {
                if (backupWorkList.isNotEmpty()) {
                    val workInfo = backupWorkList[0]
                    val status = workInfo.state.name
                    if (status == "ENQUEUED") {
                        systemService.stopBackupWorker()
                    }
                }
            }
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