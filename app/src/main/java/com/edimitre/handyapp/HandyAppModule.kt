package com.edimitre.handyapp

import android.app.Application
import com.edimitre.handyapp.data.dao.ShopExpenseDao
import com.edimitre.handyapp.data.room_database.HandyDb
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


}