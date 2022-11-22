package com.edimitre.handyapp.data.room_database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.data.dao.ShopExpenseDao
import com.edimitre.handyapp.data.model.Expense
import com.edimitre.handyapp.data.model.Shop


@Database(
    entities = [Shop::class, Expense::class],
    version = 1,
    exportSchema = false
)
abstract class HandyDb : RoomDatabase() {


    abstract fun getShopExpenseDao(): ShopExpenseDao


    companion object {

        private var INSTANCE: HandyDb? = null

        fun getInstance(context: Context): HandyDb {

            var instance = INSTANCE

            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    HandyDb::class.java,
                    HandyAppEnvironment.DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
            }
            return instance

        }
    }
}