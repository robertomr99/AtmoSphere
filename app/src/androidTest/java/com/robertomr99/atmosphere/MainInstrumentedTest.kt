package com.robertomr99.atmosphere

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.test.rule.GrantPermissionRule
import com.robertomr99.atmosphere.data.server.MockWebServerRule
import com.robertomr99.atmosphere.data.server.fromJson
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class MainInstrumentedTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val mockWebServerRule = MockWebServerRule()

    @get:Rule(order = 2)
    val locationPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        "android.permission.ACCESS_COARSE_LOCATION"
    )

    @get:Rule(order = 3)
    val androidComposeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        println("=== SETUP INICIADO ===")
        println("MockWebServer URL: ${mockWebServerRule.server.url("/")}")

        mockWebServerRule.server.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                println("=== MOCKWEBSERVER REQUEST RECIBIDO ===")
                println("URL: ${request.requestUrl}")
                println("Path: ${request.path}")

                return when {
                    request.path?.contains("/direct") == true -> {
                        println("→ Respondiendo con suggestions.json")
                        MockResponse().fromJson("suggestions.json")
                    }
                    request.path?.contains("/weather") == true -> {
                        println("→ Respondiendo con weather.json")
                        MockResponse().fromJson("weather.json")
                    }
                    request.path?.contains("/forecast") == true -> {
                        println("→ Respondiendo con forecast.json")
                        MockResponse().fromJson("forecast.json")
                    }
                    else -> {
                        println("→ Request no reconocido: ${request.path}")
                        MockResponse().setResponseCode(404)
                    }
                }
            }
        }

        println("=== SETUP COMPLETADO ===")
        hiltRule.inject()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun search_city_and_navigate_to_detail(): Unit = with(androidComposeRule) {
        // 1. Buscar la ciudad
        onNodeWithText("Buscar ciudad").performTextInput("Sevilla")
        onNodeWithText("Buscar ciudad").performImeAction()

        waitUntilAtLeastOneExists(hasTestTag("Suggestion_Sevilla_ES_Andalusia"), timeoutMillis = 5_000)
        onNodeWithTag("Suggestion_Sevilla_ES_Andalusia").performClick()

        waitUntilAtLeastOneExists(hasTestTag("CityDetailTitle"), timeoutMillis = 10_000)
        onNodeWithTag("CityDetailTitle").assertIsDisplayed()
    }

}
