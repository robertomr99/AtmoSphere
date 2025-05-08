package com.robertomr99.atmosphere.data.datasource

import com.robertomr99.atmosphere.data.datasource.remote.GeoCodingClient
import com.robertomr99.atmosphere.data.datasource.remote.WeatherClient
import com.robertomr99.atmosphere.data.forecast.ForecastResult
import com.robertomr99.atmosphere.data.datasource.remote.getFirstCityCoordinates
import com.robertomr99.atmosphere.data.weather.WeatherResult

class WeatherDataSource {

    suspend fun getWeatherForCity(cityName: String, units: String, region: String): WeatherResult? {
        val coordinates = GeoCodingClient.instance.getFirstCityCoordinates(cityName)

        return coordinates?.let { city ->
            if(units.isEmpty()){
                WeatherClient.instance.fetchWeatherUnitDefault(
                    lat = city.lat,
                    lon = city.lon,
                    lang = region
                )
            }else{
                WeatherClient.instance.fetchWeather(
                    lat = city.lat,
                    lon = city.lon,
                    units = units,
                    lang = region
                )
            }
        }
    }

    suspend fun getForecastForCity(cityName: String, units: String, region: String): ForecastResult? {
        val coordinates = GeoCodingClient.instance.getFirstCityCoordinates(cityName)

        return coordinates?.let { city ->
            if(units.isEmpty()){
                WeatherClient.instance.fetchForecastUnitDefault(
                    lat = city.lat,
                    lon = city.lon,
                    lang = region
                )
            }else{
                WeatherClient.instance.fetchForecast(
                    lat = city.lat,
                    lon = city.lon,
                    units = units,
                    lang = region
                )
            }
        }
    }

}