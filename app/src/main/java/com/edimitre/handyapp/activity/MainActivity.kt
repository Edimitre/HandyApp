package com.edimitre.handyapp.activity


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.HandyAppEnvironment.TAG
import com.edimitre.handyapp.R
import com.edimitre.handyapp.data.model.firebase.AuthModel
import com.edimitre.handyapp.data.util.SystemService
import com.edimitre.handyapp.data.view_model.MainViewModel
import com.edimitre.handyapp.data.worker.ImportDBWorker
import com.edimitre.handyapp.databinding.ActivityMainBinding
import com.edimitre.handyapp.fragment.main_fragments.NavigationFragment
import com.edimitre.handyapp.fragment.main_fragments.SettingsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), SettingsFragment.ImportDbListener {

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var systemService: SystemService

    private var backUpDb = Firebase.firestore

    private val mainViewModel: MainViewModel by viewModels()

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // notification channel
        systemService.createNotificationChannel()

        setInitialSettings()

        initToolBar()

        observeActiveFragment()

        observeTheme()

        startNotificationWorker()
    }

    private fun setInitialSettings() {

        val hasConnection = hasConnection(this)

        lifecycleScope.launch {
            var auth = mainViewModel.getAuthModel()
            when (auth) {
                null -> {
                    Log.e(TAG, "creating initial settings ")
                    auth = AuthModel(
                        0,
                        "",
                        "",
                        "",
                        false,
                        isBackupEnabled = false,
                        isDarkThemeEnabled = false
                    )
                    mainViewModel.saveAuth(auth)
                    mainViewModel.selectDarkTheme(false)
                    mainViewModel.selectBackupEnabled(false)
                    mainViewModel.setHasConnection(hasConnection)
                }
                else -> {
                    mainViewModel.setHasConnection(hasConnection)
                    mainViewModel.selectBackupEnabled(auth.isBackupEnabled)
                    mainViewModel.selectDarkTheme(auth.isDarkThemeEnabled)

                }
            }
        }

    }

    private fun initToolBar() {
        binding.toolbar.inflateMenu(R.menu.toolbar_menu)

        setToolbarItems()

        setListeners()
    }

    private fun setToolbarItems() {

        val calendarButton = binding.toolbar.menu.findItem(R.id.btn_calendar_pick)
        val closeSearchButton = binding.toolbar.menu.findItem(R.id.btn_close_date_search)
        val searchButton = binding.toolbar.menu.findItem(R.id.btn_search_db)

        calendarButton.isVisible = false
        closeSearchButton.isVisible = false
        searchButton.isVisible = false
    }

    private fun observeActiveFragment() {

        mainViewModel.selectFragment(NavigationFragment())

        mainViewModel.fragmentSelected.observe(this) {
            loadFragment(it)
        }
    }

    private fun observeTheme() {

        mainViewModel.isDarkSelected.observe(this) {
            when (it) {
                true -> {
                    changeTheme("Dark")
                }
                false -> {
                    changeTheme("Light")
                }
            }

        }
    }

    private fun setListeners() {

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

    private fun openSettingsDialog() {
        val settingsDialog = SettingsFragment()
        settingsDialog.show(supportFragmentManager, "settings_dialog")
    }

    private fun changeTheme(theme: String) {
        when (theme) {
            "Dark" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            "Light" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            else -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frag_container, fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()
    }

    private fun hasConnection(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            } else {
                TODO("VERSION.SDK_INT < M")
            }
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
//                Log.e(HandyAppEnvironment.TAG, "Has cellular connection")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
//                Log.e(HandyAppEnvironment.TAG, "Has wifi connection")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
//                Log.e(HandyAppEnvironment.TAG, "Has ethernet connection")
                return true
            }
        }
        return false
    }

    override fun importDb() {
        getBackUpData()
    }

    private fun getBackUpData() {

        lifecycleScope.launch {
            val authModel = mainViewModel.getAuthModel()
            val reference = backUpDb.collection("handy_app").document(authModel!!.uid)
            reference.get()
                .addOnSuccessListener { refDoc ->
                    when {
                        refDoc.data.isNullOrEmpty() -> {
                            systemService.notify(
                                HandyAppEnvironment.TITLE,
                                "there are no data to import "
                            )
                        }
                        else -> {
                            val response = refDoc.data.toString()
                            startImportWorker(response)
                        }
                    }
                }
        }

    }

    private fun startNotificationWorker() {
        systemService.startNotificationWorker()
    }

    private fun startImportWorker(importData: String) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val data: Data = Data.Builder().putString("backup_data", importData).build()

        val importWork = OneTimeWorkRequest.Builder(ImportDBWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data)
            .addTag("import_work")
            .build()
        val workManager = WorkManager.getInstance(this@MainActivity)

        workManager.enqueue(importWork)
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.e(TAG, "On Destroy")
        val backupWorkList = WorkManager.getInstance(this).getWorkInfosByTag("backup_worker").get()

        backupWorkList.forEach { work ->


            when {
                work != null -> {
                    lifecycleScope.launch {
                        val auth = mainViewModel.getAuthModel()
                        when {
                            auth!!.isBackupEnabled -> {
                                when (work.state.name) {
                                    "ENQUEUED" -> {

                                    }
                                    else -> {
                                        systemService.startBackupWorker()
                                    }
                                }
                            }
                            else -> {
                                systemService.stopBackupWorker()
                            }
                        }
                    }
                }
                else -> {
                    lifecycleScope.launch {
                        val auth = mainViewModel.getAuthModel()
                        when {
                            auth!!.isBackupEnabled -> {
                                systemService.startBackupWorker()
                            }
                            else -> {
                                systemService.stopBackupWorker()
                            }
                        }
                    }
                }
            }
        }
//
//        val work = backupWorkList[0]


    }

}