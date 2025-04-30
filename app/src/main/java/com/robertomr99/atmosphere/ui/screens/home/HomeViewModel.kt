package com.robertomr99.atmosphere.ui.screens.home

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robertomr99.atmosphere.data.WeatherRepository
import com.robertomr99.atmosphere.data.weather.WeatherResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    data class UiState(
        val loading: Boolean = false,
        val favCitiesWeatherResult: List<FavCityPreviewWeather> = emptyList(),
    )

    private val _state = MutableStateFlow(UiState())
    val state : StateFlow<UiState> = _state.asStateFlow()

    private val _temperatureUnit = MutableStateFlow(TemperatureUnit.CELSIUS)
    val temperatureUnit : StateFlow<TemperatureUnit> = _temperatureUnit.asStateFlow()

    private val _region = MutableStateFlow("ES")
    val region : StateFlow<String> = _region.asStateFlow()

    private val repository = WeatherRepository()

    private val cities = mutableListOf(
        "London",
        "New York",
        "Tokyo",
        "Sydney",
        "Paris",
        "Moscow",
        "São Paulo",
        "Dubai",
        "Cape Town",
        "Toronto",
    )

    init {
        observeTemperatureUnit()
    }

    private fun observeTemperatureUnit() {
        viewModelScope.launch {
            _temperatureUnit.collect {
                loadFavsCitiesWeather()
            }
        }
    }


    fun loadFavsCitiesWeather() {
        viewModelScope.launch {
            _state.value = state.value.copy(loading = true)
            try {
                val weatherResult: MutableList<FavCityPreviewWeather> = mutableListOf()
                cities.forEach {
                    val weatherCity = repository.getWeatherForCity(it, unitsMapper(_temperatureUnit.value), _region.value)
                    weatherCity?.let { result ->
                        weatherResult.add(favCityPreviewWeatherMapper(result))
                    }
                }
                _state.value = UiState(
                    loading = false,
                    favCitiesWeatherResult = weatherResult.toList()
                )
            } catch (e: Exception) {
                _state.value = state.value.copy(loading = false)
            }
        }
    }

    fun setTemperatureUnit(unit: TemperatureUnit){
        _temperatureUnit.value = unit
    }

    fun setRegion(regionResult : String){
        _region.value = regionResult
    }

    fun removeCity(cityName: String) {
        _state.value = _state.value.copy(
            favCitiesWeatherResult = _state.value.favCitiesWeatherResult.filterNot { it.name == cityName }
        )
        cities.remove(cityName)
    }

    data class FavCityPreviewWeather(
        val name: String,
        val weatherId : Int,
        val temp: String,
        val minTemp: String,
        val maxTemp: String,
        val description: String
    )

    @SuppressLint("DefaultLocale")
    fun favCityPreviewWeatherMapper(cityWeather : WeatherResult) : FavCityPreviewWeather{
        return FavCityPreviewWeather(
            name = cityWeather.name ?: "",
            weatherId = cityWeather.weather.firstOrNull()?.id ?: 0,
            temp = String.format("%.0f", cityWeather.main?.temp) ,
            minTemp = String.format("%.0f", cityWeather.main?.tempMin) ,
            maxTemp = String.format("%.0f", cityWeather.main?.tempMax),
            description = cityWeather.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: ""
        )
    }

}