package com.robertomr99.atmosphere.feature.detail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.robertomr99.atmosphere.feature.common.Result
import com.robertomr99.atmosphere.sampleForecastResult
import com.robertomr99.atmosphere.sampleWeatherResult
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test

class DetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun whenLoadingState_showProgress(): Unit = with(composeTestRule) {
        setContent {
            DetailScreen(
            state = Result.Loading,
            cityName = "",
            displayedCityName = "",
            country = "",
            temperatureUnit = "",
            errorMessage = "",
            isFavCity = true,
            onBack = {},
            onLoadCityWeather = {_,_ ->},
            onUpdateCityFav = { _ ->},
            onErrorDetected = { }
            )
        }

        onNodeWithTag(SHIMMER_CITY_DETAIL_TAG).assertIsDisplayed()
    }

    @Test
    fun whenErrorState_showError() : Unit = with(composeTestRule){
        setContent {
            DetailScreen(
                state = Result.Error(RuntimeException("An error ocurred")),
                cityName = "",
                displayedCityName = "",
                country = "",
                temperatureUnit = "",
                errorMessage = "",
                isFavCity = true,
                onBack = {},
                onLoadCityWeather = {_,_ ->},
                onUpdateCityFav = { _ ->},
                onErrorDetected = { }
            )
        }

        onNodeWithText("Error al cargar los datos del clima:", substring = true).assertExists()
    }

    @Test
    fun whenSuccessState_weatherIsShown() : Unit = with(composeTestRule){

        val weatherData = DetailViewModel.WeatherData(
            weatherResult = sampleWeatherResult("Madrid"),
            forecastResult = sampleForecastResult(),
            isFavCity = true
        )


        setContent {
            DetailScreen(
                state = Result.Success(weatherData),
                cityName = "",
                displayedCityName = "",
                country = "",
                temperatureUnit = "",
                errorMessage = "",
                isFavCity = true,
                onBack = {},
                onLoadCityWeather = {_,_ ->},
                onUpdateCityFav = { _ ->},
                onErrorDetected = { }
            )
        }

        onNodeWithText("Madrid", substring = true).assertExists()
    }

    @Test
    fun whenFavoriteClicked_listenerIsCalled() : Unit = with(composeTestRule){

        var clicked = false

        val weatherData = DetailViewModel.WeatherData(
            weatherResult = sampleWeatherResult("Madrid"),
            forecastResult = sampleForecastResult(),
            isFavCity = clicked
        )


        setContent {
            DetailScreen(
                state = Result.Success(weatherData),
                cityName = "",
                displayedCityName = "",
                country = "",
                temperatureUnit = "",
                errorMessage = "",
                isFavCity = clicked,
                onBack = {},
                onLoadCityWeather = {_,_ ->},
                onUpdateCityFav = { favorite ->
                    clicked = favorite
                },
                onErrorDetected = { }
            )
        }

        onNodeWithContentDescription("Fav City Button").performClick()
        assertTrue(clicked)
    }

    @Test
    fun whenBackClicked_listenerIsCalled() : Unit = with(composeTestRule){

        var clicked = false

        val weatherData = DetailViewModel.WeatherData(
            weatherResult = sampleWeatherResult("Madrid"),
            forecastResult = sampleForecastResult(),
            isFavCity = false
        )


        setContent {
            DetailScreen(
                state = Result.Success(weatherData),
                cityName = "",
                displayedCityName = "",
                country = "",
                temperatureUnit = "",
                errorMessage = "",
                isFavCity = false,
                onBack = { clicked = true},
                onLoadCityWeather = {_,_ ->},
                onUpdateCityFav = { _ ->},
                onErrorDetected = { }
            )
        }

        onNodeWithContentDescription("Back").performClick()
        assertTrue(clicked)
    }
}