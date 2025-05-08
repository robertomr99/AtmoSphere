package com.robertomr99.atmosphere.data.datasource.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.robertomr99.atmosphere.data.CityEntity

@Database(entities = [CityEntity::class], version = 1, exportSchema = false)
abstract class CityDatabase : RoomDatabase() {
    abstract fun cityDao(): CityDao
}