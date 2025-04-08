package com.robertomr99.atmosphere.ui.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.robertomr99.atmosphere.ui.common.SectionWithTransparentBackground
import com.robertomr99.atmosphere.ui.common.getWeatherIcon

@Composable
fun DailyForecastSection(dailyForecasts: List<DetailViewModel.DailyForecast>) {
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
            modifier = Modifier.weight(1.5f)
        )

        Icon(
            painter = painterResource(id = getWeatherIcon(forecast.weatherIcon)),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .weight(1f),
            tint = Color.Unspecified
        )

        Text(
            text = "${forecast.minTemp}°",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "${forecast.maxTemp}°",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
    }

}
