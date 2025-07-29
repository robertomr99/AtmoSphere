package com.robertomr99.atmosphere.feature.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robertomr99.atmosphere.domain.region.usecases.GetCurrentRegionUseCase
import com.robertomr99.atmosphere.feature.common.Result
import com.robertomr99.atmosphere.feature.common.stateAsResultIn
import com.robertomr99.atmosphere.domain.weather.TemperatureUnit
import com.robertomr99.atmosphere.domain.weather.entities.CityCoordinatesResponse
import com.robertomr99.atmosphere.domain.weather.entities.WeatherResult
import com.robertomr99.atmosphere.domain.weather.unitsMapper
import com.robertomr99.atmosphere.domain.weather.usecases.DeleteFavouriteCityUseCase
import com.robertomr99.atmosphere.domain.weather.usecases.FetchFavouritesCitiesUseCase
import com.robertomr99.atmosphere.domain.weather.usecases.FetchSuggestionsForCityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val fetchFavouritesCitiesUseCase: FetchFavouritesCitiesUseCase,
    private val fetchSuggestionsForCityUseCase: FetchSuggestionsForCityUseCase,
    private val deleteFavouriteCityUseCase: DeleteFavouriteCityUseCase,
    private val getCurrentRegionUseCase: GetCurrentRegionUseCase
) : ViewModel() {

    private val _temperatureUnit = MutableStateFlow(TemperatureUnit.CELSIUS)
    val temperatureUnit: StateFlow<TemperatureUnit> = _temperatureUnit.asStateFlow()

    private val _region = MutableStateFlow("ES")
    val region: StateFlow<String> = _region.asStateFlow()

    private val _query = MutableStateFlow("")

    private val _refreshTrigger = MutableStateFlow(0L)

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<Result<List<FavCityPreviewWeather>>> = combine(
        _temperatureUnit,
        _refreshTrigger
    ) { temperatureUnit, _ ->
        temperatureUnit
    }
        .flatMapLatest { temperatureUnit ->
            fetchFavouritesCitiesUseCase(
                unitsMapper(
                    temperatureUnit
                )
            )
                .map { weatherCities -> favCityPreviewWeatherMapper(weatherCities) }
        }
        .stateAsResultIn(viewModelScope)


    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val citySuggestions: StateFlow<List<CityCoordinatesResponse>> = _query
        .debounce(300)
        .filter { it.length >= 2 }
        .distinctUntilChanged()
        .flatMapLatest { query ->
            fetchSuggestionsForCityUseCase(query)
                .catch { emit(emptyList()) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    fun loadFavsCitiesWeather() {
        _refreshTrigger.value = System.currentTimeMillis()
    }

    fun setTemperatureUnit(unit: TemperatureUnit) {
        _temperatureUnit.value = unit
    }

    fun removeCity(cityName: String, country: String) {
        viewModelScope.launch {
            try {
                deleteFavouriteCityUseCase(cityName, country)
                loadFavsCitiesWeather()
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error removing city: ${e.message}")
            }
        }
    }

    fun onCityQueryChanged(query: String) {
        _query.value = query
    }

    fun clearCitySuggestions() {
        _query.value = ""
    }

    fun requestLocationAndUpdateRegion() {
        viewModelScope.launch {
            try {
                val detectedRegion = getCurrentRegionUseCase()
                setRegion(detectedRegion)
                loadFavsCitiesWeather()
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error obteniendo región: ${e.message}")
                setRegion("ES")
                loadFavsCitiesWeather()
            }
        }
    }

    fun setRegion(regionResult : String){
        _region.value = regionResult
    }

    data class FavCityPreviewWeather(
        val name: String,
        val country: String,
        val weatherId: Int,
        val temp: String,
        val minTemp: String,
        val maxTemp: String,
        val description: String
    )

    @SuppressLint("DefaultLocale")
    private fun favCityPreviewWeatherMapper(
        listOfCityWeather: List<WeatherResult>,
    ): List<FavCityPreviewWeather> {
        return listOfCityWeather.map { cityWeather ->
            FavCityPreviewWeather(
                name = cityWeather.name ?: "",
                country = cityWeather.sys?.country ?: "",
                weatherId = cityWeather.weather.firstOrNull()?.id ?: 0,
                temp = String.format("%.0f", cityWeather.main?.temp),
                minTemp = String.format("%.0f", cityWeather.main?.tempMin),
                maxTemp = String.format("%.0f", cityWeather.main?.tempMax),
                description = cityWeather.weather.firstOrNull()?.description
                    ?.replaceFirstChar { it.uppercase() } ?: ""
            )
        }.sortedBy { it.name }
    }
}