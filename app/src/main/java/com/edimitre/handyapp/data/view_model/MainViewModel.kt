package com.edimitre.handyapp.data.view_model

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.data.dao.AuthDao
import com.edimitre.handyapp.data.model.firebase.AuthModel
import com.edimitre.handyapp.fragment.main_fragments.NavigationFragment
import com.edimitre.handyapp.fragment.main_fragments.SignUpFragment
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val authDao: AuthDao,
) : ViewModel() {


    private val mutableIsBackupSelected: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val isBackupSelected: LiveData<Boolean> get() = mutableIsBackupSelected

    private val mutableIsDarkSelected: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val isDarkSelected: LiveData<Boolean> get() = mutableIsDarkSelected


    private val mutableDoingWork: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val isDoingWork: LiveData<Boolean> get() = mutableDoingWork

    private val mutableFragmentSelected: MutableLiveData<Fragment> =
        MutableLiveData<Fragment>(SignUpFragment())
    val fragmentSelected: LiveData<Fragment> get() = mutableFragmentSelected


    private val mutableHasConnection: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val hasConnection: LiveData<Boolean> get() = mutableHasConnection



    fun selectDarkTheme(isDark: Boolean) {
        mutableIsDarkSelected.value = isDark
    }

    fun selectBackupEnabled(isEnabled: Boolean) {
        mutableIsBackupSelected.value = isEnabled
    }

    fun setHasConnection(hasConnection: Boolean) {
        mutableHasConnection.value = hasConnection
    }

    fun selectFragment(fragment: Fragment) {
        mutableFragmentSelected.value = fragment
    }

    private fun setDoingWork(doingWork: Boolean) {
        mutableDoingWork.value = doingWork
    }

    fun doLogin(email: String, password: String) {
        setDoingWork(true)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                when {
                    it.isSuccessful -> {
                        setDoingWork(false)

                        viewModelScope.launch {
                            val authModel = authDao.getAuthModelOnCoroutine()
                            authModel!!.uid = auth.currentUser!!.uid
                            authModel.isSignedIn = true
                            saveAuth(authModel)
                        }

                        selectFragment(NavigationFragment())

                    }

                    else -> {
                        Log.e(HandyAppEnvironment.TAG, "exception happened : ${it.exception}")
                        setDoingWork(false)
                    }

                }
            }
    }

    fun signUpUser(email: String, password: String) {

        setDoingWork(true)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val authModel =
                        AuthModel(
                            0,
                            auth.currentUser!!.uid,
                            auth.currentUser!!.email!!,
                            password,
                            isSignedIn = true,
                            isBackupEnabled = false,
                            isDarkThemeEnabled = false
                        )

                    saveAuth(authModel)

                    setDoingWork(false)

                    selectFragment(NavigationFragment())
                    Log.e(
                        HandyAppEnvironment.TAG,
                        "sign up success for user with email : ${auth.currentUser!!.email}"
                    )


                } else {
                    Log.e(HandyAppEnvironment.TAG, "signup error reason : ${task.exception} ")
                }
            }

    }

    fun doLogOut() {
        auth.signOut()

        viewModelScope.launch {
            val authModel = getAuthModel()
            authModel!!.isBackupEnabled = false
            authModel.isSignedIn = false

            saveAuth(authModel)
//            selectBackupEnabled(false)
        }


    }

    fun saveAuth(authModel: AuthModel): Job = viewModelScope.launch {



        authDao.save(authModel)

    }

    suspend fun getAuthModel(): AuthModel? {

        return authDao.getAuthModelOnCoroutine()
    }

    fun isAuthenticated(): Boolean {

        return auth.currentUser != null
    }


}