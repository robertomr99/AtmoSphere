package com.robertomr99.atmosphere.data

import com.robertomr99.atmosphere.data.forecast.ForecastResult
import com.robertomr99.atmosphere.data.weather.WeatherResult

class WeatherRepository {

    suspend fun getWeatherForCity(cityName: String, units: String, lang: String): WeatherResult? {
        val coordinates = GeoCodingClient.instance.getFirstCityCoordinates(cityName)

        return coordinates?.let { city ->
            WeatherClient.instance.fetchWeather(
                lat = city.lat,
                lon = city.lon,
                units = units,
                lang = lang
            )
        }
    }

    suspend fun getForecastForCity(cityName: String, units: String, lang: String): ForecastResult? {
        val coordinates = GeoCodingClient.instance.getFirstCityCoordinates(cityName)

        return coordinates?.let { city ->
            WeatherClient.instance.fetchForecast(
                lat = city.lat,
                lon = city.lon,
                units = units,
                lang = lang
            )
        }
    }
}