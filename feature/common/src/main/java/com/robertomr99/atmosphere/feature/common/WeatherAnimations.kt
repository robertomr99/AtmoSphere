package com.robertomr99.atmosphere.feature.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.robertomr99.atmosphere.common.R

fun getWeatherIcon(iconCode: String): Int {
    return when(iconCode) {
        "01d" -> R.drawable.ic_sunny
        "01n" -> R.drawable.ic_clear_night
        "02d", "03d", "04d" -> R.drawable.ic_partly_cloudy
        "02n", "03n", "04n" -> R.drawable.ic_cloudy_night
        "09d", "09n", "10d", "10n" -> R.drawable.ic_rain
        "11d", "11n" -> R.drawable.ic_storm
        "13d", "13n" -> R.drawable.ic_snow
        "50d", "50n" -> R.drawable.ic_fog
        else -> R.drawable.ic_sunny
    }
}


fun getWeatherAnimationId(weatherId: Int): Int {
    val resId = when (weatherId) {
        800 -> R.drawable.clear_sky
        801 -> R.drawable.few_clouds
        in 802..804 -> R.drawable.scattered_clouds
        in 500..531 -> R.drawable.rain
        in 200..232 -> R.drawable.thundestorm
        in 600..622 -> R.drawable.snow
        in 701..781 -> R.drawable.fog
        else -> R.drawable.clear_sky
    }
    return resId
}


fun getWeatherAnimation(weatherId: Int): @Composable () -> Unit {
    val resId = when (weatherId) {
        800 -> R.drawable.clear_sky
        801 -> R.drawable.few_clouds
        in 802..804 -> R.drawable.scattered_clouds
        in 500..531 -> R.drawable.rain
        in 200..232 -> R.drawable.thundestorm
        in 600..622 -> R.drawable.snow
        in 701..781 -> R.drawable.fog
        else -> R.drawable.clear_sky
    }
    return { BackgroundImage(imageResId = resId) }
}

fun getWeatherGradientColors(weatherId: Int): Triple<Color, Color, Color> {
    return when (weatherId) {
        800 -> Triple(
            Color(0xFF64B5F6),
            Color(0xFF2196F3),
            Color(0xFF1976D2)
        )
        801 -> Triple( // Pocas nubes
            Color(0xFF90CAF9),
            Color(0xFF64B5F6),
            Color(0xFF42A5F5)
        )
        in 802..804 -> Triple(
            Color(0xFFBBDEFB),
            Color(0xFF90CAF9),
            Color(0xFF64B5F6)
        )
        in 500..531 -> Triple(
            Color(0xFF78909C),
            Color(0xFF607D8B),
            Color(0xFF546E7A)
        )
        in 200..232 -> Triple(
            Color(0xFF455A64),
            Color(0xFF37474F),
            Color(0xFF263238)
        )
        in 600..622 -> Triple(
            Color(0xFFE3F2FD),
            Color(0xFFBBDEFB),
            Color(0xFF90CAF9)
        )
        in 701..781 -> Triple(
            Color(0xFFCFD8DC),
            Color(0xFFB0BEC5),
            Color(0xFF90A4AE)
        )
        else -> Triple(
            Color(0xFF64B5F6),
            Color(0xFF2196F3),
            Color(0xFF1976D2)
        )
    }
}

@Composable
private fun BackgroundImage(imageResId: Int) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = "Weather background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}

@Composable
fun SectionWithTransparentBackground(
    modifier: Modifier? = Modifier,
    content: @Composable () -> Unit
) {
    if (modifier != null) {
        Box(
            modifier = modifier
                .padding(horizontal = 32.dp, vertical = 16.dp)
                .fillMaxWidth()
                .background(
                    color = Color.DarkGray.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            content()
        }
    }
}

@Composable
fun CompactSectionBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                color = Color.DarkGray.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        content()
    }
}