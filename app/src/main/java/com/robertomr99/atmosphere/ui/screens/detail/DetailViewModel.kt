package com.robertomr99.atmosphere.ui.screens.detail

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robertomr99.atmosphere.data.WeatherRepository
import com.robertomr99.atmosphere.data.forecast.CustomList
import com.robertomr99.atmosphere.data.forecast.ForecastResult
import com.robertomr99.atmosphere.data.weather.WeatherResult
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DetailViewModel : ViewModel() {

    data class UiState(
        val loading: Boolean = false,
        val weatherResult: WeatherResult = WeatherResult(),
        val forecastResult: ForecastResult = ForecastResult()
    )

    private val repository = WeatherRepository()
    var state by mutableStateOf(UiState())
        private set

    var cityName: String = ""
    var units: String = "metric"

    fun loadCityWeather(city: String, lang: String) {
        viewModelScope.launch {
            state = state.copy(loading = true)
            try {
                val weatherResult = repository.getWeatherForCity(city, units, lang)
                val forecastResult = repository.getForecastForCity(city, units, lang)

                if (weatherResult != null && forecastResult != null) {
                    cityName = weatherResult.name ?: city
                    state = UiState(
                        loading = false,
                        weatherResult = weatherResult,
                        forecastResult = forecastResult
                    )
                } else {
                    state = state.copy(loading = false)
                }
            } catch (e: Exception) {
                state = state.copy(loading = false)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getHourlyForecastToday(): List<HourlyForecast> {
        val list = getHourlyForecastToday(state.forecastResult)
        Log.d("DetailViewModel", "Hourly forecasts count: ${list.size}")
        return list
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getHourlyForecastToday(forecastResult: ForecastResult?): List<HourlyForecast> {
        if (forecastResult?.list.isNullOrEmpty()) return emptyList()

        return forecastResult!!.list!!.take(10).mapNotNull { forecast ->
            val hour = forecast.dt?.let {
                Instant.ofEpochSecond(it.toLong())
                    .atZone(ZoneId.systemDefault())
                    .hour
            } ?: return@mapNotNull null

            val temp = forecast.main?.temp?.toInt() ?: 0
            val icon = forecast.weather?.firstOrNull()?.icon ?: "01d"

            HourlyForecast(
                hour = hour,
                temperature = temp,
                weatherIcon = icon
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDailyMinMaxForecast(days: Int = 5): List<DailyForecast> {
        return getDailyMinMaxForecast(state.forecastResult, days)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDailyMinMaxForecast(forecastResult: ForecastResult?, days: Int = 5): List<DailyForecast> {
        if (forecastResult?.list.isNullOrEmpty()) return emptyList()

        val dailyForecasts = mutableListOf<DailyForecast>()
        val calendar = Calendar.getInstance()

        val dailyGroups = forecastResult?.list!!.groupBy { forecast ->
            forecast.dt?.let {
                LocalDateTime.ofEpochSecond(it.toLong(), 0, ZoneOffset.UTC).toLocalDate()
            }
        }.filterKeys { it != null }.mapValues { it.value } as Map<LocalDate, List<CustomList>>

        val sortedDays = dailyGroups.keys.toList().sorted().take(days)

        sortedDays.forEach { date ->
            val forecastsForDay = dailyGroups[date] ?: emptyList()

            val minTemp = forecastsForDay.minOfOrNull { it.main?.tempMin ?: Double.MAX_VALUE }?.toInt() ?: 0
            val maxTemp = forecastsForDay.maxOfOrNull { it.main?.tempMax ?: Double.MIN_VALUE }?.toInt() ?: 0
            val weatherIcon = forecastsForDay.firstOrNull()?.weather?.firstOrNull()?.icon ?: "01d"

            val dayName = when {
                date.isEqual(LocalDate.now()) -> "Hoy"
                date.isEqual(LocalDate.now().plusDays(1)) -> "Mañana"
                else -> {
                    try {
                        calendar.time = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())
                        when (calendar.get(Calendar.DAY_OF_WEEK)) {
                            Calendar.MONDAY -> "Lun"
                            Calendar.TUESDAY -> "Mar"
                            Calendar.WEDNESDAY -> "Mié"
                            Calendar.THURSDAY -> "Jue"
                            Calendar.FRIDAY -> "Vie"
                            Calendar.SATURDAY -> "Sáb"
                            Calendar.SUNDAY -> "Dom"
                            else -> date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                        }
                    } catch (e: Exception) {
                        date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    }
                }
            }

            dailyForecasts.add(
                DailyForecast(
                    dayName = dayName,
                    minTemp = minTemp,
                    maxTemp = maxTemp,
                    weatherIcon = weatherIcon
                )
            )
        }

        return dailyForecasts
    }

    data class DailyForecast(
        val dayName: String,
        val minTemp: Int,
        val maxTemp: Int,
        val weatherIcon: String
    )

    data class HourlyForecast(
        val hour: Int,
        val temperature: Int,
        val weatherIcon: String
    )
}