package com.robertomr99.atmosphere.ui.screens.detail

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.robertomr99.atmosphere.ui.common.SectionWithTransparentBackground
import com.robertomr99.atmosphere.ui.common.getRegion
import com.robertomr99.atmosphere.ui.common.getWeatherAnimation
import com.robertomr99.atmosphere.ui.theme.AtmoSphereTheme

@Composable
fun Screen(content: @Composable () -> Unit) {
    AtmoSphereTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
            content = content
        )
    }
}

@SuppressLint("DefaultLocale")
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(cityName: String, onBack :() -> Unit,  vm: DetailViewModel = viewModel()){
    val ctx = LocalContext.current
    val state = vm.state
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val scrollState = rememberScrollState()

    LaunchedEffect(cityName) {
        if (cityName.isNotBlank()) {
            vm.loadCityWeather(cityName, ctx.getRegion())
        }
    }
    Screen {
        val nestedScrollConnection = scrollBehavior.nestedScrollConnection

        Box(modifier = Modifier.fillMaxSize()) {
            if (!state.loading && state.weatherResult.weather.isNotEmpty()) {
                val weatherId = state.weatherResult.weather.firstOrNull()?.id ?: 0
                val backgroundComposable = getWeatherAnimation(weatherId)
                backgroundComposable()
            }
        Scaffold(
            modifier = Modifier.nestedScroll(nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = {  Text(
                        text = vm.cityName,
                        fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = ""
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.DarkGray.copy(alpha = 0.3f),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    ),
                    scrollBehavior = scrollBehavior
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            if (state.loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    WeatherTitleSection(state)
                    HourlyForecastSection(vm.getHourlyForecastToday())
                    DailyForecastSection(vm.getDailyMinMaxForecast())
                }
            }
        }
    }
        }
}

@SuppressLint("DefaultLocale")
@Composable
private fun WeatherTitleSection(
    state: DetailViewModel.UiState
) {
    SectionWithTransparentBackground{
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = state.weatherResult.name ?: "",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = "${String.format("%.0f", state.weatherResult.main?.temp ?: 0.0)}°",
                color = Color.White,
                fontSize = 84.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = state.weatherResult.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() }
                    ?: "",
                color = Color.White,
                fontSize = 18.sp
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "Máx. ${String.format("%.0f", state.weatherResult.main?.tempMax ?: 0.0)}°",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Mín. ${String.format("%.0f", state.weatherResult.main?.tempMin ?: 0.0)}°",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
