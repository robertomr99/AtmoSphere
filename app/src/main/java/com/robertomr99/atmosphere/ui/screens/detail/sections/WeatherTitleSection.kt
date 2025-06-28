package com.robertomr99.atmosphere.ui.screens.detail.sections

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robertomr99.atmosphere.data.weather.WeatherResult
import com.robertomr99.atmosphere.ui.common.SectionWithTransparentBackground

@SuppressLint("DefaultLocale")
@Composable
fun WeatherTitleSection(
    weatherResult: WeatherResult
) {
    SectionWithTransparentBackground{
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = weatherResult.name ?: "",
                color = Color.White,
                fontSize = 24.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold
            )

            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = "${String.format("%.0f", weatherResult.main?.temp ?: 0.0)}°",
                color = Color.White,
                fontSize = 84.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = weatherResult.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() }
                    ?: "",
                color = Color.White,
                fontSize = 18.sp
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "Máx. ${String.format("%.0f", weatherResult.main?.tempMax ?: 0.0)}°",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Mín. ${String.format("%.0f", weatherResult.main?.tempMin ?: 0.0)}°",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}