package com.robertomr99.atmosphere

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import com.robertomr99.atmosphere.framework.database.Database


class App : Application(){

    lateinit var db: Database
        private set

    override fun onCreate() {
        super.onCreate()

        db = Room
            .databaseBuilder(this, Database::class.java, "db")
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .build()
    }
}