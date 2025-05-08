package com.robertomr99.atmosphere.data.datasource.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.robertomr99.atmosphere.data.CityEntity

@Dao
interface CityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCity(cityEntity: CityEntity)

    @Query("DELETE FROM CityEntity WHERE name = :cityName")
    suspend fun deleteCityByName(cityName: String)

    @Query("SELECT * FROM CityEntity")
    suspend fun findAll() : List<CityEntity>
}