package com.robertomr99.atmosphere.data

import com.robertomr99.atmosphere.data.datasource.WeatherDataSource
import com.robertomr99.atmosphere.data.datasource.local.CityLocalDataSource
import com.robertomr99.atmosphere.data.forecast.ForecastResult
import com.robertomr99.atmosphere.data.weather.WeatherResult

class WeatherRepository(
    private val regionRepository: RegionRepository,
    private val weatherDataSource : WeatherDataSource,
    private val cityLocalDataSource: CityLocalDataSource
) {

    suspend fun deleteFavouriteCity(cityName: String){
        cityLocalDataSource.deleteCityByName(cityName)
    }

    suspend fun saveFavouriteCity(cityEntity: CityEntity){
        cityLocalDataSource.saveCity(cityEntity)
    }

    suspend fun getWeatherForFavouritesCities(units: String): List<WeatherResult> {

        val citiesWeatherResult = mutableListOf<WeatherResult>()
        val cities = cityLocalDataSource.findAll()

        if(cities.isNotEmpty()){
            cities.forEach {
                weatherDataSource.getWeatherForCity(
                    cityName = it.name,
                    units = units,
                    region = regionRepository.findLastRegion()
                ).also { cityWeather ->
                    if(cityWeather != null) citiesWeatherResult.add(cityWeather)
                }
            }
        }

        return citiesWeatherResult
    }

    suspend fun getWeatherForCity( cityName: String, units: String): WeatherResult? {
      return weatherDataSource.getWeatherForCity(
            cityName = cityName,
            units = units,
            region = regionRepository.findLastRegion()
      )
    }

    suspend fun getForecastForCity( cityName: String, units: String): ForecastResult? {
        return weatherDataSource.getForecastForCity(
            cityName = cityName,
            units = units,
            region = regionRepository.findLastRegion()
        )
    }

}