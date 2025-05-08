package com.robertomr99.atmosphere.ui.screens.detail

import ShimmerCityDetail
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.robertomr99.atmosphere.ui.common.getWeatherAnimation
import com.robertomr99.atmosphere.ui.screens.NavigationState
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
fun DetailScreen(
    cityName: String,
    temperatureUnit: String,
    onBack: () -> Unit,
    vm: DetailViewModel
) {
    key(cityName) {
        val state by vm.state.collectAsState()
        val detailState = rememberDetailState()
        val isError by NavigationState.cityError.collectAsState()

        LaunchedEffect(isError) {
            if (!isError.isNullOrEmpty()) {
                onBack()
            }
        }

        LaunchedEffect(cityName) {
            if (cityName.isNotBlank()) {
                vm.loadCityWeather(cityName, temperatureUnit)
            }
        }

        Screen {
            Box(modifier = Modifier.fillMaxSize()) {
                if (!state.loading && state.weatherResult.weather.isNotEmpty()) {
                    val weatherId = state.weatherResult.weather.firstOrNull()?.id ?: 0
                    val backgroundComposable = getWeatherAnimation(weatherId)
                    backgroundComposable()
                }

                Scaffold(
                    modifier = Modifier.nestedScroll(detailState.scrollBehavior.nestedScrollConnection),
                    topBar = {
                        TopAppBar(
                            title = { Text(
                                text = vm.cityName,
                                fontSize = 18.sp
                            ) },
                            scrollBehavior = detailState.scrollBehavior,
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.DarkGray.copy(alpha = 0.3f),
                                titleContentColor = Color.White,
                                navigationIconContentColor = Color.White
                            ),
                            navigationIcon = {
                                IconButton(onClick = onBack) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            },
                            actions = {
                                TopBarDetailActions(
                                    detailState,
                                    onFavClick = {
                                        vm.updateCityFav(it)
                                    }
                                )
                            }
                        )
                    },
                    containerColor = Color.Transparent
                ) { paddingValues ->
                    if (state.loading) {
                        ShimmerCityDetail()
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                                .verticalScroll(detailState.scrollState),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            DetailScreenGroup(state, vm, temperatureUnit)
                        }
                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DetailScreenGroup(
    state: DetailViewModel.UiState,
    vm: DetailViewModel,
    temperatureUnit: String
) {
    WeatherTitleSection(state)
    HourlyForecastSection(vm.getHourlyForecastToday())
    DailyForecastSection(vm.getDailyMinMaxForecast(), temperatureUnit)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 16.dp)
    ) {
        FeelsLikeTempSection(
            feelsLikeTemp = vm.getFeelsLikeTemp(),
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp)
                .height(100.dp)
        )
        HumiditySection(
            humidity = vm.getHumidity(),
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp)
                .height(100.dp)
        )
    }

    WindSection(
        wind = vm.getWindResult(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 16.dp)
    )
}