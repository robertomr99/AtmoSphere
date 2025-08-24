package com.robertomr99.atmosphere.feature.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.robertomr99.atmosphere.domain.weather.TemperatureUnit
import com.robertomr99.atmosphere.feature.common.Result
import com.robertomr99.atmosphere.sampleCityCoordinates
import com.robertomr99.atmosphere.sampleFavCityPreviewWeatherList
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun whenLoadingState_showProgress(): Unit = with(composeTestRule) {

        setContent {
            HomeScreen(
                state = Result.Loading,
                region = "ES",
                temperatureUnit = TemperatureUnit.CELSIUS,
                errorMessage = "",
                citySuggestions = sampleCityCoordinates("ES"),
                onCityClick =  { _,_ ->},
                onCityQueryChanged = { _ ->},
                onUnitSelected = {_ ->},
                onRemoveCity = { _,_ ->},
                onClearCitySuggestions = {},
                onRequestLocationPermission = {},
                onClearError = {},
            )
        }

        onNodeWithTag(SHIMMER_CITY_CARD_TAG).assertIsDisplayed()
    }

    @Test
    fun whenErrorState_showError() : Unit = with(composeTestRule){

        setContent {
            HomeScreen(
                state = Result.Error(RuntimeException("An error ocurred")),
                region = "ES",
                temperatureUnit = TemperatureUnit.CELSIUS,
                errorMessage = "",
                citySuggestions = sampleCityCoordinates("ES"),
                onCityClick =  { _,_ ->},
                onCityQueryChanged = { _ ->},
                onUnitSelected = {_ ->},
                onRemoveCity = { _,_ ->},
                onClearCitySuggestions = {},
                onRequestLocationPermission = {},
                onClearError = {},
            )
        }

        onNodeWithText("Error al cargar las ciudades favoritas:", substring = true).assertExists()

    }

    @Test
    fun whenSuccessState_showCityWeather() : Unit = with(composeTestRule){

        setContent {
            HomeScreen(
                state = Result.Success(sampleFavCityPreviewWeatherList()),
                region = "ES",
                temperatureUnit = TemperatureUnit.CELSIUS,
                errorMessage = "",
                citySuggestions = sampleCityCoordinates("ES"),
                onCityClick =  { _,_ ->},
                onCityQueryChanged = { _ ->},
                onUnitSelected = {_ ->},
                onRemoveCity = { _,_ ->},
                onClearCitySuggestions = {},
                onRequestLocationPermission = {},
                onClearError = {},
            )
        }
        onNodeWithText("Ciudad 1", substring = true).assertExists()
    }

    @Test
    fun whenFavCityClicked_listenerIsCalled() : Unit = with(composeTestRule){

        var cityName = ""
        var units = ""

        setContent {
            HomeScreen(
                state = Result.Success(sampleFavCityPreviewWeatherList()),
                region = "ES",
                temperatureUnit = TemperatureUnit.CELSIUS,
                errorMessage = "",
                citySuggestions = sampleCityCoordinates("ES"),
                onCityClick =  { name ,metric ->
                    cityName = name
                    units = metric
                },
                onCityQueryChanged = { _ ->},
                onUnitSelected = {_ ->},
                onRemoveCity = { _,_ ->},
                onClearCitySuggestions = {},
                onRequestLocationPermission = {},
                onClearError = {},
            )
        }
        onNodeWithText("Ciudad 1", substring = true, useUnmergedTree = true).performClick()

        assertEquals("Ciudad 1,ES", cityName)
        assertEquals("metric", units)
    }

}