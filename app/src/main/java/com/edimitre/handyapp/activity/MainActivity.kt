package com.edimitre.handyapp.activity


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkManager
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.R
import com.edimitre.handyapp.data.util.SystemService
import com.edimitre.handyapp.data.view_model.MainViewModel
import com.edimitre.handyapp.databinding.ActivityMainBinding
import com.edimitre.handyapp.fragment.main_fragments.NavigationFragment
import com.edimitre.handyapp.fragment.main_fragments.SettingsFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), SettingsFragment.ImportDbListener {

    @Inject
    lateinit var systemService: SystemService

    private var backUpDb = Firebase.firestore

    private val mainViewModel: MainViewModel by viewModels()

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // create app notification channel
        systemService.createNotificationChannel()

        val connectStatus = hasConnection(this)
        mainViewModel.setHasConnection(connectStatus)

        initToolBar()

        observeActiveFragment()

        observeTheme()


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

        mainViewModel.setActiveFragment(NavigationFragment())

        mainViewModel.fragmentSelected.observe(this) {
            displayFragment(it)
        }
    }

    private fun displayFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frag_container, fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()
    }

    private fun observeTheme() {

        mainViewModel.isDarkSelected.observe(this) { dark ->


            when (dark) {
                true -> {
                    applyTheme("Dark")
                }
                false -> {
                    applyTheme("Light")
                }
            }

        }

    }

    private fun applyTheme(color: String) {
        when (color) {
            "Dark" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            "Light" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

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

    // return status of device internet connection
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

    // functions for importing database from firebase ...comes from settings fragment
    override fun importDb() {
        getBackUpDataFromFirebase()
    }

    override fun backupDb() {

        val uuid: UUID = systemService.startOneTimeBackupWork()

        WorkManager.getInstance(this@MainActivity)
            .getWorkInfoByIdLiveData(uuid).observe(this@MainActivity) {

                val running = it.progress.getBoolean("isRunning", false)
                setLoading(running)
            }
    }

    private fun getBackUpDataFromFirebase() {

        var uuid: UUID?

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
                            uuid = systemService.startImportWorker(response)

                            WorkManager.getInstance(this@MainActivity)
                                .getWorkInfoByIdLiveData(uuid!!).observe(this@MainActivity) {

                                    val running = it.progress.getBoolean("isRunning", false)
                                    setLoading(running)

                                }

                        }
                    }
                }
        }


    }

    private fun setLoading(value: Boolean) {

        if (value) {
            binding.progressLayout.visibility = View.VISIBLE
            binding.fragContainer.visibility = View.INVISIBLE

        } else {

            binding.progressLayout.visibility = View.INVISIBLE
            binding.fragContainer.visibility = View.VISIBLE

        }

    }



}