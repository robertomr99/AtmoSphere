package com.robertomr99.atmosphere.ui.common

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
import com.robertomr99.atmosphere.R

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
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .padding(horizontal = 32.dp, vertical = 16.dp)
            .fillMaxWidth()
            .background(
                color = Color.DarkGray.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        content()
    }
}