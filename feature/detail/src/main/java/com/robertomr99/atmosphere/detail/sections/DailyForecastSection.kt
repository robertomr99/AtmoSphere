package com.robertomr99.atmosphere.detail.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.robertomr99.atmosphere.common.SectionWithTransparentBackground
import com.robertomr99.atmosphere.common.getWeatherIcon
import com.robertomr99.atmosphere.detail.DetailViewModel

lateinit var temperatureUnit: String

@Composable
fun DailyForecastSection(dailyForecasts: List<DetailViewModel.DailyForecast>, unit: String) {
    temperatureUnit = unit

    SectionWithTransparentBackground {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Previsión (5 Días)",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Column {
                dailyForecasts.forEach { forecast ->
                    DailyForecastItem(forecast)
                }
            }
        }
    }
}

@Composable
private fun DailyForecastItem(forecast: DetailViewModel.DailyForecast) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = forecast.dayName,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1.5f)
        )

        Icon(
            painter = painterResource(id = getWeatherIcon(forecast.weatherIcon)),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp),
            tint = Color.Unspecified
        )
        
        Spacer(Modifier.weight(0.25f))

        Text(
            text = "${forecast.minTemp}°",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
        )
        Spacer(Modifier.weight(0.25f))

        TemperatureRangeBar(
            minTemp = forecast.minTemp,
            maxTemp = forecast.maxTemp,
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.weight(0.25f))

        Text(
            text = "${forecast.maxTemp}°",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
        )
    }
}

@Composable
fun TemperatureRangeBar(
    minTemp: Int,
    maxTemp: Int,
    modifier: Modifier = Modifier
) {

    val minC = convertToCelsius(minTemp, temperatureUnit)
    val maxC = convertToCelsius(maxTemp, temperatureUnit)

    val minColor = when {
        minC >= 25.0f -> Color(0xFFFF9800)
        minC >= 15.0f -> Color(0xFF4CAF50)
        minC >= 5.0f -> Color(0xFF2196F3)
        else -> Color(0xFF3F51B5)
    }

    val maxColor = when {
        maxC >= 30.0f -> Color(0xFFFF5722)
        maxC >= 25.0f -> Color(0xFFFFC107)
        maxC >= 15.0f -> Color(0xFF4CAF50)
        else -> Color(0xFF2196F3)
    }

    val barColors = listOf(minColor, maxColor)

    Box(
        modifier = modifier
            .width(60.dp)
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(Color(0x40FFFFFF))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(
                    brush = Brush.horizontalGradient(colors = barColors)
                )
        )
    }
}

fun convertToCelsius(temp: Int, unit: String): Float {
    return when (unit) {
        "" -> temp - 273.15f
        "imperial" -> (temp - 32) * 5f / 9f
        else -> temp.toFloat()
    }
}

