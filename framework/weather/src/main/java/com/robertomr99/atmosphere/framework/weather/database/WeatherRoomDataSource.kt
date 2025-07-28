package com.robertomr99.atmosphere.framework.weather.database

import android.util.Log
import com.robertomr99.atmosphere.domain.weather.data.WeatherLocalDataSource
import com.robertomr99.atmosphere.domain.weather.entities.ForecastEntity
import com.robertomr99.atmosphere.domain.weather.entities.WeatherEntity
import com.robertomr99.atmosphere.domain.weather.entities.WeatherWithForecasts
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class WeatherRoomDataSource(private val weatherDao: WeatherDao) :
    WeatherLocalDataSource {

    override val weatherList = weatherDao.getAll().map { listOfDbWeatherEntities ->
        listOfDbWeatherEntities.map { it.toDomainWeather() }
    }

    override fun findWeatherFavByCity(cityName: String, country: String) = weatherDao.findWeatherFavByCity(cityName, country)

    override fun getWeatherWithForecastsByCity(cityName: String, country: String): Flow<WeatherWithForecasts?> {
        return weatherDao.getWeatherWithForecastsByCity(cityName, country).map { it.toDomainWeatherWithForecast() }
    }

    override suspend fun saveWeatherWithForecasts(weatherEntity: WeatherEntity, forecastList: List<ForecastEntity>) {
        try {
            Log.d("WeatherLocalDataSource", "Guardando weather y forecasts en transacción para ${weatherEntity.cityId}")
            Log.d("WeatherLocalDataSource", "Forecasts: ${forecastList.size} elementos")

            weatherDao.saveWeatherWithForecasts(weatherEntity.toDbWeather(), forecastList.toDbForecastList())

            val forecastCount = weatherDao.getForecastCountByCityId(weatherEntity.cityId)
            Log.d("WeatherLocalDataSource", "Forecasts guardados en BD: $forecastCount")

        } catch (e: Exception) {
            Log.e("WeatherLocalDataSource", "Error guardando weather con forecasts: ${e.message}")
            throw e
        }
    }

    // Weather solamente (usado en updateCityData cuando no incluye forecast)
    override suspend fun saveWeather(weatherEntity: WeatherEntity) {
        weatherDao.saveWeather(weatherEntity.toDbWeather())
    }

    override suspend fun deleteCityByName(cityName: String, country: String) {
        weatherDao.deleteWeatherByNameAndCountry(cityName, country)
    }

    // DB to Domain

    private fun DbWeatherEntity.toDomainWeather() =
        WeatherEntity(
            cityId = cityId,
            name = name,
            country = country,
            temperatureUnit = temperatureUnit,
            temp = temp,
            maxTemp = maxTemp,
            minTemp = minTemp,
            feelsLike = feelsLike,
            humidity = humidity,
            deg = deg,
            gust = gust,
            speed = speed,
            weatherId = weatherId,
            weatherDescription = weatherDescription
        )

    private fun DbForecastEntity.toDomainForecast() =
        ForecastEntity(
            id = 0,
            cityOwnerId = cityOwnerId,
            hour = hour,
            temp = temp,
            tempMin = tempMin,
            tempMax = tempMax,
            weatherIcon = weatherIcon
        )

    private fun List<DbForecastEntity>.toDomainForecastList() = map {it.toDomainForecast() }

    private fun DbWeatherWithForecastsEntity.toDomainWeatherWithForecast() =
        WeatherWithForecasts(
            weather = weather.toDomainWeather(),
            forecastList = forecastList.toDomainForecastList()
        )

    // Domain to DB

    private fun WeatherEntity.toDbWeather() = DbWeatherEntity(
        cityId = cityId,
        name = name,
        country = country,
        temperatureUnit = temperatureUnit,
        temp = temp,
        maxTemp = maxTemp,
        minTemp = minTemp,
        feelsLike = feelsLike,
        humidity = humidity,
        deg = deg,
        gust = gust,
        speed = speed,
        weatherId = weatherId,
        weatherDescription = weatherDescription
    )

    private fun ForecastEntity.toDbForecast() = DbForecastEntity(
        id = 0,
        cityOwnerId = cityOwnerId,
        hour = hour,
        temp = temp,
        tempMin = tempMin,
        tempMax = tempMax,
        weatherIcon = weatherIcon
    )

    private fun List<ForecastEntity>.toDbForecastList() = map {it.toDbForecast() }

}