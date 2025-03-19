package com.example.notebook

import android.app.Application
import androidx.room.Room

class DataBaseInit: Application() {
    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).fallbackToDestructiveMigration().build()
    }
}