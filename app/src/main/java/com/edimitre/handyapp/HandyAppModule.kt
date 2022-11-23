package com.edimitre.handyapp

import android.app.Application
import com.edimitre.handyapp.data.dao.ReminderNotesDao
import com.edimitre.handyapp.data.dao.ShopExpenseDao
import com.edimitre.handyapp.data.room_database.HandyDb
import com.edimitre.handyapp.data.service.SignUpLoginService
import com.edimitre.handyapp.data.util.SystemService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object HandyAppModule {


    @Singleton
    @Provides
    fun provideHandyDb(context: Application): HandyDb {
        return HandyDb.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideShopExpenseDao(db:HandyDb):ShopExpenseDao{

        return db.getShopExpenseDao()
    }


    @Singleton
    @Provides
    fun provideReminderNotesDao(db:HandyDb):ReminderNotesDao{

        return db.getReminderNotesDao()
    }

    @Singleton
    @Provides
    fun getSystemService(context: Application): SystemService {
        return SystemService(context)
    }

    @Singleton
    @Provides
    fun getSignUpLoginService(auth: FirebaseAuth):SignUpLoginService{

        return SignUpLoginService(auth);
    }

    @Singleton
    @Provides
    fun getFirebase(): Firebase {
        return Firebase
    }

    @Singleton
    @Provides
    fun getFirebaseAuthentication(): FirebaseAuth {
        return Firebase.auth
    }

    @Singleton
    @Provides
    fun getFirebaseDatabase(): FirebaseFirestore {

        return FirebaseFirestore.getInstance()

    }
}