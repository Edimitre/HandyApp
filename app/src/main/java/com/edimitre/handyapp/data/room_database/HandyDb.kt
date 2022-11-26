package com.edimitre.handyapp.data.room_database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.data.dao.AuthDao
import com.edimitre.handyapp.data.dao.ReminderNotesDao
import com.edimitre.handyapp.data.dao.SettingsDao
import com.edimitre.handyapp.data.dao.ShopExpenseDao
import com.edimitre.handyapp.data.model.*
import com.edimitre.handyapp.data.model.firebase.AuthModel


@Database(
    entities = [Shop::class, Expense::class,Note::class,Reminder::class, AuthModel::class,Settings::class],
    version = 1,
    exportSchema = false
)
abstract class HandyDb : RoomDatabase() {


    abstract fun getShopExpenseDao(): ShopExpenseDao

    abstract fun getReminderNotesDao():ReminderNotesDao

    abstract fun getAuthDao():AuthDao

    abstract fun getSettingsDao():SettingsDao

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