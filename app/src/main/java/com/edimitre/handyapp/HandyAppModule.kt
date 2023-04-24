package com.edimitre.handyapp

import android.app.Application
import com.edimitre.handyapp.data.dao.*
import com.edimitre.handyapp.data.room_database.HandyDb
import com.edimitre.handyapp.data.service.FileService
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
    fun provideShopExpenseDao(db: HandyDb): ShopExpenseDao {

        return db.getShopExpenseDao()
    }


    @Singleton
    @Provides
    fun provideWorkDayDao(db: HandyDb): WorkDayDao {

        return db.getWorkDayDao()
    }


    @Singleton
    @Provides
    fun provideReminderNotesDao(db: HandyDb): ReminderNotesDao {

        return db.getReminderNotesDao()
    }

    @Singleton
    @Provides
    fun provideAuthDao(db: HandyDb): AuthDao {

        return db.getAuthDao()
    }

    @Singleton
    @Provides
    fun provideNewsDao(db: HandyDb): NewsDao {

        return db.getNewsDao()
    }


    @Singleton
    @Provides
    fun provideSystemService(context: Application): SystemService {
        return SystemService(context)
    }

    @Singleton
    @Provides
    fun provideFileService(context: Application): FileService {

        return FileService(context)
    }

    @Singleton
    @Provides
    fun provideFirebaseProvider(): Firebase {
        return Firebase
    }

    @Singleton
    @Provides
    fun provideFirebaseAuthentication(): FirebaseAuth {
        return Firebase.auth
    }

    @Singleton
    @Provides
    fun provideFirebaseDatabase(): FirebaseFirestore {

        return FirebaseFirestore.getInstance()

    }
}