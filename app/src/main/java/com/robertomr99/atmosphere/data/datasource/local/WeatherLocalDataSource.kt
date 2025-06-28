package com.robertomr99.atmosphere.data.datasource.local

import android.util.Log
import com.robertomr99.atmosphere.data.ForecastEntity
import com.robertomr99.atmosphere.data.WeatherEntity
import com.robertomr99.atmosphere.data.datasource.database.WeatherDao

class WeatherLocalDataSource(private val weatherDao: WeatherDao) {

    val weatherList = weatherDao.getAll()

    fun findWeatherFavByCity(cityName: String, country: String) = weatherDao.findWeatherFavByCity(cityName, country)

    fun getWeatherWithForecastsByCity(cityName: String, country: String) = weatherDao.getWeatherWithForecastsByCity(cityName, country)

    suspend fun saveWeatherWithForecasts(weatherEntity: WeatherEntity, forecastList: List<ForecastEntity>) {
        try {
            Log.d("WeatherLocalDataSource", "Guardando weather y forecasts en transacción para ${weatherEntity.cityId}")
            Log.d("WeatherLocalDataSource", "Forecasts: ${forecastList.size} elementos")

            weatherDao.saveWeatherWithForecasts(weatherEntity, forecastList)

            val forecastCount = weatherDao.getForecastCountByCityId(weatherEntity.cityId)
            Log.d("WeatherLocalDataSource", "Forecasts guardados en BD: $forecastCount")

        } catch (e: Exception) {
            Log.e("WeatherLocalDataSource", "Error guardando weather con forecasts: ${e.message}")
            throw e
        }
    }

    // weather solamente (usado en updateCityData cuando no incluye forecast)
    suspend fun saveWeather(weatherEntity: WeatherEntity) {
        weatherDao.saveWeather(weatherEntity)
    }

    suspend fun deleteCityByName(cityName: String, country: String) {
        weatherDao.deleteWeatherByNameAndCountry(cityName, country)
    }


}