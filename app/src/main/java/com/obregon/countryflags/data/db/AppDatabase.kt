package com.obregon.countryflags.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.obregon.countryflags.data.db.dao.CountryDao
import com.obregon.countryflags.data.db.dao.FlagDao

@Database(entities = [Flag::class, Country::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getFlagDao(): FlagDao
    abstract fun getCountryDao(): CountryDao

    companion object {
        private const val DATABASE_NAME = "flags"
        private const val DATABASE_DIR = "database/flags.db"

        fun getInstance(context: Context): AppDatabase {
            return Room
                .databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .createFromAsset(DATABASE_DIR)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}

