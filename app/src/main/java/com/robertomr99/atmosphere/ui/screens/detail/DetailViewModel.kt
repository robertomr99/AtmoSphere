package com.robertomr99.atmosphere.ui.screens.detail

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robertomr99.atmosphere.data.WeatherRepository
import com.robertomr99.atmosphere.data.forecast.CustomList
import com.robertomr99.atmosphere.data.forecast.ForecastResult
import com.robertomr99.atmosphere.data.weather.WeatherResult
import com.robertomr99.atmosphere.data.weather.Wind
import com.robertomr99.atmosphere.ui.screens.NavigationState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _state = MutableStateFlow(UiState())
        val state : StateFlow<UiState> = _state.asStateFlow()

    var cityName: String = ""

    fun loadCityWeather(city: String, region: String, temperatureUnit: String) {
        viewModelScope.launch {
            _state.value = state.value.copy(loading = true)
            try {
                val weatherResult = repository.getWeatherForCity(city, temperatureUnit, region)
                val forecastResult = repository.getForecastForCity(city, temperatureUnit, region)

                if (weatherResult != null && forecastResult != null) {
                    cityName = weatherResult.name ?: city
                    delay(500)
                    _state.value = UiState(
                        loading = false,
                        weatherResult = weatherResult,
                        forecastResult = forecastResult
                    )
                } else {
                    _state.value = state.value.copy(loading = false)
                    NavigationState.setCityError("No se encontró la ciudad '$city'. Inténtalo de nuevo.")
                }
            } catch (e: Exception) {
                _state.value = state.value.copy(loading = false)
                NavigationState.setCityError("No se encontró la ciudad '$city'. Inténtalo de nuevo.")
            }
        }
    }

    fun getFeelsLikeTemp(): Int? {
        return state.value.weatherResult.main?.feelsLike?.toInt()
    }

    fun getHumidity(): Int? {
        return state.value.weatherResult.main?.humidity
    }

    fun getWindResult(): Wind? {
        return state.value.weatherResult.wind
    }

    fun updateCityFav(isFavCity: Boolean){
        Log.i("Rob", "isFavCity: $isFavCity cityName: $cityName")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getHourlyForecastToday(): List<HourlyForecast> {
        val list = getHourlyForecastToday(state.value.forecastResult)
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
        return getDailyMinMaxForecast(state.value.forecastResult, days)
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

            val minTemp = forecastsForDay.minOfOrNull { it.main?.tempMin ?: Double.MIN_VALUE }?.toInt() ?: 0
            val maxTemp = forecastsForDay.maxOfOrNull { it.main?.tempMax ?: Double.MAX_VALUE }?.toInt() ?: 0
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