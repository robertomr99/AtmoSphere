package com.robertomr99.atmosphere

import com.robertomr99.atmosphere.domain.weather.entities.CityCoordinatesResponse
import com.robertomr99.atmosphere.domain.weather.entities.ForecastEntity
import com.robertomr99.atmosphere.domain.weather.entities.ForecastResult
import com.robertomr99.atmosphere.domain.weather.entities.WeatherEntity
import com.robertomr99.atmosphere.domain.weather.entities.WeatherResult
import com.robertomr99.atmosphere.domain.weather.entities.WeatherWithForecasts
import com.robertomr99.atmosphere.domain.weather.entities.forecast.CustomList
import com.robertomr99.atmosphere.domain.weather.entities.weather.Sys
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

fun sampleCityCoordinatesResponse(
    name: String,
    country: String
) = CityCoordinatesResponse(
    name = name,
    lat = 40.4168,
    lon = -3.7038,
    country = country,
    state = null
)

fun sampleWeatherEntity(name: String, country: String) = WeatherEntity(
    cityId = "${name.lowercase()}_${country.lowercase()}",
    name = name,
    country = country,
    temperatureUnit = "metric",
    temp = 25.0,
    maxTemp = 30.0,
    minTemp = 20.0,
    feelsLike = 27.0,
    humidity = 65,
    deg = 180,
    gust = 8.1,
    speed = 5.2,
    weatherId = 800,
    weatherDescription = "cielo claro"
)

fun sampleWeatherWithForecasts(
    cityId: String = "madrid_es",
    name: String = "Madrid"
) = WeatherWithForecasts(
    weather = WeatherEntity(
        cityId = cityId,
        name = name,
        country = "ES",
        temperatureUnit = "metric",
        temp = 25.0,
        maxTemp = 30.0,
        minTemp = 20.0,
        feelsLike = 27.0,
        humidity = 65,
        deg = 180,
        gust = 8.1,
        speed = 5.2,
        weatherId = 800,
        weatherDescription = "cielo claro"
    ),
    forecastList = listOf(
        ForecastEntity(
            id = 1,
            cityOwnerId = cityId,
            hour = 12,
            temp = 26,
            tempMin = 22,
            tempMax = 28,
            weatherIcon = "02d"
        ),
        ForecastEntity(
            id = 2,
            cityOwnerId = cityId,
            hour = 15,
            temp = 28,
            tempMin = 24,
            tempMax = 32,
            weatherIcon = "01d"
        ),
        ForecastEntity(
            id = 3,
            cityOwnerId = cityId,
            hour = 18,
            temp = 24,
            tempMin = 19,
            tempMax = 27,
            weatherIcon = "10d"
        ),
        ForecastEntity(
            id = 4,
            cityOwnerId = cityId,
            hour = 21,
            temp = 22,
            tempMin = 18,
            tempMax = 24,
            weatherIcon = "01n"
        )
    )
)

 fun sampleWeatherResultComplete(name: String) = WeatherResult(
    name = name,
    main = WeatherMain(temp = 25.0, tempMin = 20.0, tempMax = 30.0),
    weather = arrayListOf(Weather(id = 800, main = "Clear", description = "cielo claro")),
    sys = Sys(country = "ES")
)

 fun sampleForecastResultComplete() = ForecastResult(
    cod = "200",
    cnt = 40,
    list = List(40) { index ->
        CustomList(
            dt = 1627776000 + (index * 3600),
            main = ForecastMain(temp = 25.0 + index),
            weather = arrayListOf(Weather(id = 800, main = "Clear", description = "claro"))
        )
    }
)