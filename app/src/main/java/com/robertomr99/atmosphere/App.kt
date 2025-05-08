package com.robertomr99.atmosphere

import android.app.Application
import androidx.room.Room
import com.robertomr99.atmosphere.data.datasource.database.CityDatabase


class App : Application(){

    lateinit var db: CityDatabase
        private set

    override fun onCreate() {
        super.onCreate()

        db = Room
            .databaseBuilder(this, CityDatabase::class.java, "city-db")
            .build()
    }
}