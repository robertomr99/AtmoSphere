package com.robertomr99.atmosphere.ui.screens.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.robertomr99.atmosphere.ui.common.getWeatherAnimationId
import com.robertomr99.atmosphere.ui.common.getWeatherGradientColors
import kotlinx.coroutines.launch

@Composable
fun WeatherCardWithImageAndGradient(
    cityWeather: HomeViewModel.FavCityPreviewWeather?,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    cityWeather?.let {
        val scope = rememberCoroutineScope()
        val offsetX = remember { Animatable(0f) }
        val maxSwipe = 300f
        val threshold = 150f

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .padding(vertical = 8.dp)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                if (offsetX.value < -threshold) {
                                    offsetX.animateTo(-maxSwipe, tween(300))
                                    onDelete()
                                } else {
                                    offsetX.animateTo(0f, tween(300))
                                }
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            scope.launch {
                                val newOffset = offsetX.value + dragAmount
                                offsetX.snapTo(newOffset.coerceIn(-maxSwipe, 0f))
                            }
                        }
                    )
                }
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Red.copy(alpha = 0.8f))
                    .padding(end = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.White
                )
            }

            Card(
                modifier = Modifier
                    .offset { IntOffset(offsetX.value.toInt(), 0) }
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                onClick = onClick
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = getWeatherAnimationId(cityWeather.weatherId)),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = getWeatherGradientColors(cityWeather.weatherId).let { (start, mid, end) ->
                                        listOf(
                                            start.copy(alpha = 0.6f),
                                            mid.copy(alpha = 0.5f),
                                            end.copy(alpha = 0.4f)
                                        )
                                    }
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = cityWeather.name,
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "${cityWeather.temp}°",
                                style = MaterialTheme.typography.headlineLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = cityWeather.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )

                            Text(
                                text = "Máx: ${cityWeather.maxTemp}° Mín: ${cityWeather.minTemp}°",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

