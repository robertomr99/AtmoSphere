package com.robertomr99.atmosphere.data.datasource.local

import androidx.room.Embedded
import androidx.room.Relation
import com.robertomr99.atmosphere.data.ForecastEntity
import com.robertomr99.atmosphere.data.WeatherEntity
import com.robertomr99.atmosphere.data.forecast.CustomList
import com.robertomr99.atmosphere.data.forecast.ForecastResult
import com.robertomr99.atmosphere.data.weather.Sys
import com.robertomr99.atmosphere.data.weather.Weather
import com.robertomr99.atmosphere.data.weather.WeatherResult
import com.robertomr99.atmosphere.data.weather.Wind
import com.robertomr99.atmosphere.data.forecast.Main as ForecastMain
import com.robertomr99.atmosphere.data.weather.Main as WeatherMain

data class WeatherWithForecasts(
    @Embedded val weather: WeatherEntity,
    @Relation(
        parentColumn = "cityId",
        entityColumn = "cityOwnerId"
    )
    val forecastList: List<ForecastEntity>,
)

fun WeatherWithForecasts.toWeatherResult(): WeatherResult {
    return WeatherResult(
        weather = arrayListOf(Weather(
            description = weather.weatherDescription,
            id = weather.weatherId
        )),
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