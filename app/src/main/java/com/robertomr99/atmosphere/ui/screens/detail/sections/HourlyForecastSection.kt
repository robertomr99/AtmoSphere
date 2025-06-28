package com.robertomr99.atmosphere.ui.screens.detail.sections

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.robertomr99.atmosphere.ui.common.SectionWithTransparentBackground
import com.robertomr99.atmosphere.ui.common.getWeatherIcon
import com.robertomr99.atmosphere.ui.screens.detail.DetailViewModel

@Composable
fun HourlyForecastSection(hourlyForecasts: List<DetailViewModel.HourlyForecast>) {
    SectionWithTransparentBackground {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Próximas horas",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                hourlyForecasts.forEach { forecast ->
                    HourlyForecastItem(forecast)
                }
            }
        }
    }
}

@Composable
private fun HourlyForecastItem(forecast: DetailViewModel.HourlyForecast) {
    Column(
        modifier = Modifier
            .width(64.dp)
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "${forecast.hour}h",
            style = MaterialTheme.typography.labelMedium,
            color = Color.White
        )
        Icon(
            painter = painterResource(id = getWeatherIcon(forecast.weatherIcon)),
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = Color.Unspecified
        )
        Text(
            text = "${forecast.temperature}°",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
    }
}