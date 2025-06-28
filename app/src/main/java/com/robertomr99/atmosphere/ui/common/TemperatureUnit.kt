package com.robertomr99.atmosphere.ui.common

import com.robertomr99.atmosphere.data.KELVIN
import com.robertomr99.atmosphere.data.forecast.ForecastResult
import com.robertomr99.atmosphere.data.weather.WeatherResult

enum class TemperatureUnit {
    CELSIUS,
    FAHRENHEIT,
    KELVIN
}

fun unitsMapper(temperatureUnit: TemperatureUnit): String {
    return when(temperatureUnit){
        TemperatureUnit.CELSIUS -> "metric"
        TemperatureUnit.FAHRENHEIT -> "imperial"
        TemperatureUnit.KELVIN -> "standard"
    }
}

fun stringToTemperatureUnit(unit: String): TemperatureUnit {
    return when (unit.lowercase()) {
        "metric" -> TemperatureUnit.CELSIUS
        "imperial" -> TemperatureUnit.FAHRENHEIT
        "standard" -> TemperatureUnit.KELVIN
        else -> TemperatureUnit.CELSIUS
    }
}

fun convertTemperature(temp: Double, fromUnit: TemperatureUnit, toUnit: TemperatureUnit): Double {
    val tempInKelvin = when (fromUnit) {
        TemperatureUnit.CELSIUS -> temp + 273.15
        TemperatureUnit.FAHRENHEIT -> (temp - 32) * 5 / 9 + 273.15
        TemperatureUnit.KELVIN -> temp
    }

    return when (toUnit) {
        TemperatureUnit.CELSIUS -> tempInKelvin - 273.15
        TemperatureUnit.FAHRENHEIT -> (tempInKelvin - 273.15) * 9 / 5 + 32
        TemperatureUnit.KELVIN -> tempInKelvin
    }
}

private fun convertWeatherTemperature(
    weather: WeatherResult,
    fromUnits: String,
    toUnits: String
): WeatherResult {
    if (fromUnits == toUnits) return weather.copy(temperatureUnit = toUnits)

    val fromUnit = stringToTemperatureUnit(fromUnits)
    val toUnit = stringToTemperatureUnit(toUnits)

    return weather.copy(
        main = weather.main?.copy(
            temp = weather.main!!.temp?.let {
                convertTemperature(it, fromUnit, toUnit)
            },
            tempMin = weather.main!!.tempMin?.let {
                convertTemperature(it, fromUnit, toUnit)
            },
            tempMax = weather.main!!.tempMax?.let {
                convertTemperature(it, fromUnit, toUnit)
            },
            feelsLike = weather.main!!.feelsLike?.let {
                convertTemperature(it, fromUnit, toUnit)
            }
        ),
        temperatureUnit = toUnits
    )
}

private fun convertForecastTemperature(
    forecast: ForecastResult,
    fromUnits: String,
    toUnits: String
): ForecastResult {
    if (fromUnits == toUnits) return forecast

    val fromUnit = stringToTemperatureUnit(fromUnits)
    val toUnit = stringToTemperatureUnit(toUnits)

    return forecast.copy(
        list = forecast.list?.map { customList ->
            customList.copy(
                main = customList.main?.copy(
                    temp = customList.main.temp?.let {
                        convertTemperature(it, fromUnit, toUnit)
                    },
                    tempMin = customList.main.tempMin?.let {
                        convertTemperature(it, fromUnit, toUnit)
                    },
                    tempMax = customList.main.tempMax?.let {
                        convertTemperature(it, fromUnit, toUnit)
                    }
                )
            )
        }
    )
}


 fun normalizeWeatherToKelvin(weather: WeatherResult, currentUnits: String): WeatherResult {
    return convertWeatherTemperature(weather, currentUnits, KELVIN)
}

 fun normalizeForecastToKelvin(forecast: ForecastResult, currentUnits: String): ForecastResult {
    return convertForecastTemperature(forecast, currentUnits, KELVIN)
}

 fun convertWeatherFromKelvin(weather: WeatherResult, targetUnits: String): WeatherResult {
    return convertWeatherTemperature(weather, KELVIN, targetUnits)
}

 fun convertForecastFromKelvin(forecast: ForecastResult, targetUnits: String): ForecastResult {
    return convertForecastTemperature(forecast, KELVIN, targetUnits)
}
