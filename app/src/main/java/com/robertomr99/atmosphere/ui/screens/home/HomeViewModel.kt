package com.robertomr99.atmosphere.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robertomr99.atmosphere.data.forecast.City
import com.robertomr99.atmosphere.data.weather.Coord
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    var state by mutableStateOf(UiState())
        private set

    val cities = listOf(
        City(1, "New York", Coord(40.7128, -74.0060), "US", 8419600, -18000, 1618305600, 1618350000),
        City(2, "London", Coord(51.5074, -0.1278), "GB", 8982000, 0, 1618282800, 1618330800),
        City(3, "Tokyo", Coord(35.6895, 139.6917), "JP", 13929286, 32400, 1618261200, 1618305600),
        City(4, "Sydney", Coord(-33.8688, 151.2093), "AU", 5312163, 39600, 1618246800, 1618291200),
        City(5, "Paris", Coord(48.8566, 2.3522), "FR", 2148000, 3600, 1618275600, 1618320000),
        City(6, "Moscow", Coord(55.7558, 37.6173), "RU", 11920000, 10800, 1618279200, 1618323600),
        City(7, "São Paulo", Coord(-23.5505, -46.6333), "BR", 12330000, -10800, 1618293600, 1618338000),
        City(8, "Dubai", Coord(25.276987, 55.296249), "AE", 3331420, 14400, 1618268400, 1618312800),
        City(9, "Cape Town", Coord(-33.9249, 18.4241), "ZA", 433688, 7200, 1618272000, 1618316400),
        City(10, "Toronto", Coord(43.651070, -79.347015), "CA", 2731571, -18000, 1618302000, 1618346400)
    )

    fun onUiReady(){
        viewModelScope.launch {
            state = UiState(loading = true)
            state = UiState(loading = false, cities = cities)
        }
    }

    data class UiState(
        val loading: Boolean = false,
        val cities: List<City> = emptyList()
    )

}