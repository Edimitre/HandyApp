package com.edimitre.handyapp.activity


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.edimitre.handyapp.R
import com.edimitre.handyapp.data.model.Settings
import com.edimitre.handyapp.data.view_model.SettingsViewModel
import com.edimitre.handyapp.databinding.ActivityMainBinding
import com.edimitre.handyapp.fragment.settings.SettingsFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), SettingsFragment.ThemeChangeListener {

    @Inject
    lateinit var auth: FirebaseAuth

    private lateinit var settingsViewModel: SettingsViewModel

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        checkInitialSettings()
        initToolBar()
        setListeners()
        observeUserSettings()
    }

    private fun initViewModel() {

        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
    }

    private fun initToolBar() {
        binding.toolbar.inflateMenu(R.menu.toolbar_menu)

        setToolbarItems()

    }

    private fun setToolbarItems() {

        val calendarButton = binding.toolbar.menu.findItem(R.id.btn_calendar_pick)
        val closeSearchButton = binding.toolbar.menu.findItem(R.id.btn_close_date_search)
        val searchButton = binding.toolbar.menu.findItem(R.id.btn_search_db)

        calendarButton.isVisible = false
        closeSearchButton.isVisible = false
        searchButton.isVisible = false
    }

    private fun checkInitialSettings() {

        settingsViewModel.userSettings.observe(this) {

            if (it == null) {
                settingsViewModel.saveSettings(
                    Settings(
                        0,
                        isBackupEnabled = false,
                        isDarkThemeEnabled = false
                    )
                )
                println("initial settings saved")
            }
        }

    }

    private fun setListeners() {

        binding.expensesCard.setOnClickListener {

            navigateToExpensesPage()
        }

        binding.remindersAndNotesCard.setOnClickListener {
            navigateToRemindersPage()
        }



        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.btn_settings -> {
                    openSettingsDialog()
                    true
                }

                else -> {
                    true
                }
            }
        }
    }

    private fun isLoggedIn(): Boolean {


        return auth.currentUser != null
    }

    private fun openSettingsDialog() {
        val settingsDialog = SettingsFragment()
        settingsDialog.show(supportFragmentManager, "settings dialog")
    }

    private fun navigateToExpensesPage() {
        startActivity(Intent(this@MainActivity, ShopsExpensesActivity::class.java))
    }

    private fun navigateToRemindersPage() {
        startActivity(Intent(this@MainActivity, ReminderNotesActivity::class.java))
    }

    override fun setBackupEnabled(enabled: Boolean) {
        if (!isLoggedIn()){
            openSignUpDialog()
        }
    }

    private fun openSignUpDialog(){

        val dialog = MaterialAlertDialogBuilder(this,)

        dialog.setTitle("This feature require to be registered !")
        dialog.setMessage(
            "Do you want to signup/login ?"
        )
        dialog.setPositiveButton("Yes") { _, _ ->

            navigateToSignUpPage()
        }

        dialog.setNegativeButton("No") { _, _ ->

        }
        dialog.show()

    }

    override fun saveSettings(settings: Settings) {

        when {
            isLoggedIn() -> {
                settingsViewModel.saveSettings(settings)
            }
            else -> {
                settings.isBackupEnabled = false
                settingsViewModel.saveSettings(settings)
            }
        }

        Toast.makeText(this, "Settings saved ", Toast.LENGTH_SHORT).show()
    }

    override fun changeTheme(theme: String) {


        when (theme) {
            "Dark" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            else -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    private fun navigateToSignUpPage() {
        startActivity(Intent(this@MainActivity, LoginSignUpActivity::class.java))

    }

    private fun observeUserSettings() {
        settingsViewModel.userSettings.observe(this) {

            when {
                it!!.isDarkThemeEnabled -> {
                    changeTheme("Dark")
                }
                else -> {
                    changeTheme("Light")
                }
            }
        }
    }

}