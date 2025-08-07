package com.robertomr99.atmosphere.domain.weather.usecases

import com.robertomr99.atmosphere.domain.weather.entities.CityCoordinatesResponse
import com.robertomr99.atmosphere.domain.weather.entities.ForecastResult
import com.robertomr99.atmosphere.domain.weather.entities.WeatherResult
import com.robertomr99.atmosphere.domain.weather.entities.forecast.CustomList
import com.robertomr99.atmosphere.domain.weather.entities.forecast.Main as ForecastMain
import com.robertomr99.atmosphere.domain.weather.entities.weather.Main as WeatherMain
import com.robertomr99.atmosphere.domain.weather.entities.weather.Weather

fun sampleWeatherResult(name: String = "Madrid") = WeatherResult(
    name = name,
    main = WeatherMain(temp = 25.0, tempMin = 20.0, tempMax = 30.0),
    weather = arrayListOf(Weather(id = 800, main = "Clear", description = "cielo claro"))
)

fun sampleWeatherResultList(
    cities: List<String> = listOf("Madrid", "Barcelona", "Valencia", "Sevilla", "Bilbao")
) = cities.map { cityName ->
    sampleWeatherResult(name = cityName)
}

fun sampleForecastResult() = ForecastResult(
    cod = "200",
    cnt = 40,
    list = listOf(
        CustomList(
            dt = 1627776000,
            main = ForecastMain(temp = 26.0),
            weather = arrayListOf(Weather(id = 801, main = "Clouds", description = "pocas nubes"))
        )
    )
)

fun sampleCityCoordinates(cityName: String): List<CityCoordinatesResponse> {
    return listOf(
        CityCoordinatesResponse(
            name = cityName,
            lat = 40.4168,
            lon = -3.7038,
            country = "ES",
            state = "Madrid"
        )
    )
}