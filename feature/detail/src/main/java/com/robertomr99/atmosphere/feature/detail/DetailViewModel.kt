package com.robertomr99.atmosphere.feature.detail

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robertomr99.atmosphere.feature.common.NavigationState
import com.robertomr99.atmosphere.feature.common.stateAsResultIn
import com.robertomr99.atmosphere.domain.weather.entities.ForecastResult
import com.robertomr99.atmosphere.domain.weather.entities.WeatherResult
import com.robertomr99.atmosphere.domain.weather.entities.weather.Wind
import com.robertomr99.atmosphere.domain.weather.usecases.DeleteFavouriteCityUseCase
import com.robertomr99.atmosphere.domain.weather.usecases.FetchWeatherAndForecastUseCase
import com.robertomr99.atmosphere.domain.weather.usecases.FindFavCityUseCase
import com.robertomr99.atmosphere.domain.weather.usecases.SaveFavouriteCityUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.robertomr99.atmosphere.feature.common.Result

class DetailViewModel(
    private val findFavCityUseCase: FindFavCityUseCase,
    private val fetchWeatherAndForecastUseCase: FetchWeatherAndForecastUseCase,
    private val saveFavouriteCityUseCase: SaveFavouriteCityUseCase,
    private val deleteFavouriteCityUseCase: DeleteFavouriteCityUseCase
) : ViewModel() {

    data class WeatherData(
        val weatherResult: WeatherResult,
        val forecastResult: ForecastResult,
        val isFavCity: Boolean
    )

    data class LoadParams(
        val city: String,
        val temperatureUnit: String
    )

    var cityName: String = ""
        private set
    var country: String = ""
        private set
    var temperatureUnit: String = ""
        private set

    private val _loadTrigger = MutableStateFlow<LoadParams?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<Result<WeatherData>> = _loadTrigger
        .flatMapLatest { params ->
            if (params == null) {
                emptyFlow()
            } else {
                loadWeatherData(params.city, params.temperatureUnit)
            }
        }
        .stateAsResultIn(viewModelScope)

    fun loadCityWeather(city: String, temperatureUnit: String) {
        cityName = city.substringBefore(",").trim()
        country = city.substringAfter(",").trim()
        this.temperatureUnit = temperatureUnit

        _loadTrigger.value = LoadParams(city, temperatureUnit)
    }

    private fun loadWeatherData(city: String, temperatureUnit: String): Flow<WeatherData> = flow {
        val cityNameParsed = city.substringBefore(",").trim()
        val countryParsed = city.substringAfter(",").trim()

        val isFavCity = withContext(Dispatchers.IO) {
            findFavCityUseCase(cityNameParsed, countryParsed).first()
        } > 0

        val weatherResults = withContext(Dispatchers.IO) {
            try {
                fetchWeatherAndForecastUseCase(city, countryParsed, temperatureUnit, isFavCity).first()
            } catch (e: Exception) {
                Log.w("DetailViewModel", "Flow cancelled, retrying...")
                fetchWeatherAndForecastUseCase(city, countryParsed, temperatureUnit, isFavCity).first()
            }
        }

        val weather = weatherResults.first
        val forecast = weatherResults.second

        if (weather != null && forecast != null) {
            if (forecast.list.isNullOrEmpty()) {
                Log.w("DetailViewModel", "⚠️ Weather OK but Forecast is empty for $cityNameParsed")
            } else {
                Log.d("DetailViewModel", "✅ Both Weather and Forecast OK for $cityNameParsed (${forecast.list!!.size} items)")
            }

            delay(500)

            emit(
                WeatherData(
                weatherResult = weather.copy(name = cityNameParsed),
                forecastResult = forecast,
                isFavCity = isFavCity,
            )
            )
        } else {
            Log.e("DetailViewModel", "❌ Missing data - Weather: ${weather != null}, Forecast: ${forecast != null}")
            NavigationState.setCityError("No se encontró la ciudad '$city'. Inténtalo de nuevo.")
            throw Exception("No se encontró la ciudad '$city'")
        }
    }

    fun getFeelsLikeTemp(): Int? {
        return when (val currentState = state.value) {
            is Result.Success -> currentState.data.weatherResult.main?.feelsLike?.toInt()
            else -> null
        }
    }

    fun getHumidity(): Int? {
        return when (val currentState = state.value) {
            is Result.Success -> currentState.data.weatherResult.main?.humidity
            else -> null
        }
    }

    fun getWindResult(): Wind? {
        return when (val currentState = state.value) {
            is Result.Success -> currentState.data.weatherResult.wind
            else -> null
        }
    }

    fun updateCityFav(isFavCity: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            when (val currentState = state.value) {
                is Result.Success -> {
                    if(isFavCity){
                        saveFavouriteCityUseCase(
                            flowOf(currentState.data.weatherResult),
                            flowOf(currentState.data.forecastResult),
                            temperatureUnit
                        )
                    } else {
                        deleteFavouriteCityUseCase(cityName, currentState.data.weatherResult.sys?.country!!)
                    }
                }
                else -> {  }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getHourlyForecastToday(): List<HourlyForecast> {
        return when (val currentState = state.value) {
            is Result.Success -> {
                val list = getHourlyForecastToday(currentState.data.forecastResult)
                Log.d("DetailViewModel", "Hourly forecasts count: ${list.size}")
                // ✅ Log adicional para debug
                if (list.isEmpty()) {
                    Log.w("DetailViewModel", "⚠️ No hourly forecasts generated from ForecastResult with ${currentState.data.forecastResult.list?.size} items")
                }
                list
            }
            else -> emptyList()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getHourlyForecastToday(forecastResult: ForecastResult?): List<HourlyForecast> {
        // ✅ Log del estado inicial
        Log.d("DetailViewModel", "Processing forecast with ${forecastResult?.list?.size} items")

        if (forecastResult?.list.isNullOrEmpty()) {
            Log.w("DetailViewModel", "ForecastResult list is null or empty")
            return emptyList()
        }

        val result = forecastResult!!.list!!.take(10).mapNotNull { forecast ->
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

        Log.d("DetailViewModel", "Generated ${result.size} hourly forecasts from ${forecastResult.list!!.size} items")
        return result
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDailyMinMaxForecast(days: Int = 5): List<DailyForecast> {
        return when (val currentState = state.value) {
            is Result.Success -> getDailyMinMaxForecast(currentState.data.forecastResult, days)
            else -> emptyList()
        }
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
        }.filterKeys { it != null }.mapKeys { it.key!! }

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