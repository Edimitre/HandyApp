package com.edimitre.handyapp.activity

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.R
import com.edimitre.handyapp.data.dao.AuthDao
import com.edimitre.handyapp.data.model.firebase.AuthModel
import com.edimitre.handyapp.data.util.SystemService
import com.edimitre.handyapp.fragment.login_signup.LogInFragment
import com.edimitre.handyapp.fragment.login_signup.SignUpFragment
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LoginSignUpActivity : AppCompatActivity(), SignUpFragment.AuthListener,
    LogInFragment.LoginListener {



    @Inject
    lateinit var authDao: AuthDao

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var systemService: SystemService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login_sign_up)

        doInitialCheck()

    }

    private fun isLoggedIn():Boolean{
        return auth.currentUser != null
    }


    private fun doInitialCheck(){

        if (isOnline(this)&& !isLoggedIn()){
            openLoginFragment()
        }else{
            openSignUpFragment()
            Log.e(HandyAppEnvironment.TAG, "no internet available ")
        }
    }

    private fun openSignUpFragment() {
        val signUpFragment = SignUpFragment()
        loadFragment(signUpFragment)
    }

    private fun openLoginFragment() {
        val loginFragment = LogInFragment()
        loadFragment(loginFragment)
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frag_container, fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()
    }

    private fun isOnline(context: Context): Boolean {
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
                Log.e(HandyAppEnvironment.TAG, "Has cellular connection")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.e(HandyAppEnvironment.TAG, "Has wifi connection")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.e(HandyAppEnvironment.TAG, "Has ethernet connection")
                return true
            }
        }
        return false
    }

    fun doLogOut() {
        auth.signOut()
        val thread = Thread{
            val auth = authDao.getMainUserForBackup()
            auth!!.isSignedIn = false
            authDao.saveMainUserOnThread(auth)
        }
        thread.start()
        thread.join()

    }

    // comes from signup fragment
    override fun executeAuthentication(email: String, password: String) {


        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val authModel =
                        AuthModel(auth.uid!!, auth.currentUser!!.email!!, password, true)

                    val thread = Thread{
                        authDao.saveMainUserOnThread(authModel)
                    }
                    thread.start()
                    thread.join()


                    Log.e(
                        HandyAppEnvironment.TAG,
                        "sign up success for user with email : ${auth.currentUser!!.email}"
                    )

                    systemService.startBackupWorker()


                } else {
                    Log.e(HandyAppEnvironment.TAG, "signup error reason : ${task.exception} ")
                }
            }

    }
    override fun switchLoginFragment() {
        openLoginFragment()
    }

    // comes from login fragment
    override fun executeLogin(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                when {
                    it.isSuccessful -> {

                        navigateToMainPage()

                        Toast.makeText(this, "back up activated ", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Log.e(HandyAppEnvironment.TAG, "login error reason : ${it.exception} ")
                    }

                }


            }
    }
    override fun switchSignUpFragment() {
        openSignUpFragment()
    }


    private fun navigateToMainPage() {
        startActivity(Intent(this@LoginSignUpActivity, MainActivity::class.java))
    }
}