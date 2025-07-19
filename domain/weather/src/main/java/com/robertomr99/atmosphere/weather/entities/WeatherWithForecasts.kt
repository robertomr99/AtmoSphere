package com.robertomr99.atmosphere.weather.entities

import com.robertomr99.atmosphere.weather.entities.forecast.CustomList
import com.robertomr99.atmosphere.weather.entities.weather.Sys
import com.robertomr99.atmosphere.weather.entities.weather.Weather
import com.robertomr99.atmosphere.weather.entities.weather.Wind
import com.robertomr99.atmosphere.weather.entities.forecast.Main as ForecastMain
import com.robertomr99.atmosphere.weather.entities.weather.Main as WeatherMain

data class WeatherWithForecasts(
    val weather: WeatherEntity,
    val forecastList: List<ForecastEntity>,
)

fun WeatherWithForecasts.toWeatherResult(): WeatherResult {
    return WeatherResult(
        weather = arrayListOf(
            Weather(
            description = weather.weatherDescription,
            id = weather.weatherId
        )
        ),
        name = weather.name,
        main = WeatherMain(
            feelsLike = weather.feelsLike,
            temp = weather.temp,
            tempMin = weather.minTemp,
            tempMax = weather.maxTemp,
            humidity = weather.humidity
        ),
        sys = Sys(
            country = weather.country
        ),
        wind = Wind(
            deg = weather.deg,
            gust = weather.gust,
            speed = weather.speed
        ),
        temperatureUnit = weather.temperatureUnit
    )
}

fun WeatherWithForecasts.toForecastResult(): ForecastResult {
    if (forecastList.isEmpty()) {
        return ForecastResult(
            cod = "200",
            message = 0,
            cnt = 0,
            list = emptyList(),
            city = null
        )
    }

    val customListItems = forecastList.map { forecast ->
        CustomList(
            dt = forecast.hour,
            main = ForecastMain(
                temp = forecast.temp.toDouble(),
                tempMin = forecast.tempMin.toDouble(),
                tempMax = forecast.tempMax.toDouble()
            ),
            weather = arrayListOf(
                Weather(
                    icon = forecast.weatherIcon
                )
            )
        )
    }

    return ForecastResult(
        cod = "200",
        message = 0,
        cnt = customListItems.size,
        list = customListItems,
        city = null
    )
}