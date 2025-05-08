package com.robertomr99.atmosphere.data.datasource.local

import com.robertomr99.atmosphere.data.CityEntity
import com.robertomr99.atmosphere.data.datasource.database.CityDao

class CityLocalDataSource(private val cityDao: CityDao) {

    suspend fun findAll() = cityDao.findAll()

    suspend fun saveCity(cityEntity: CityEntity) = cityDao.saveCity(cityEntity)

    suspend fun deleteCityByName(cityName: String) = cityDao.deleteCityByName(cityName)

}