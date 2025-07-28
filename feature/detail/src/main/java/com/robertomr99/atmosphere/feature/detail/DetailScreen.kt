package com.robertomr99.atmosphere.feature.detail

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robertomr99.atmosphere.feature.common.AcScaffold
import com.robertomr99.atmosphere.feature.common.NavigationState
import com.robertomr99.atmosphere.feature.common.Result
import com.robertomr99.atmosphere.feature.common.getWeatherAnimation
import com.robertomr99.atmosphere.feature.common.theme.AtmoSphereTheme
import com.robertomr99.atmosphere.feature.detail.sections.DailyForecastSection
import com.robertomr99.atmosphere.feature.detail.sections.FeelsLikeTempSection
import com.robertomr99.atmosphere.feature.detail.sections.HourlyForecastSection
import com.robertomr99.atmosphere.feature.detail.sections.HumiditySection
import com.robertomr99.atmosphere.feature.detail.sections.WeatherTitleSection
import com.robertomr99.atmosphere.feature.detail.sections.WindSection
import org.koin.androidx.compose.koinViewModel

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
    vm: DetailViewModel = koinViewModel()
) {
    key(cityName) {
        val stateValue by vm.state.collectAsState()
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
            val detailState = rememberDetailState(
                isFavCity = when (val currentState = stateValue) {
                    is Result.Success -> currentState.data.isFavCity
                    else -> false
                }
            )

            AcScaffold(
                state = stateValue,
                modifier = Modifier.nestedScroll(detailState.scrollBehavior.nestedScrollConnection),
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "${vm.cityName}, ${vm.country}",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 18.sp,
                            )
                        },
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
                                onFavClick = { isFav ->
                                    vm.updateCityFav(isFav)
                                }
                            )
                        }
                    )
                },
                containerColor = Color.Transparent,
                loadingContent = {
                    ShimmerCityDetail()
                },
                errorContent = { paddingValues, errorMessage ->
                    Box(
                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error al cargar los datos del clima: $errorMessage",
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            ) { paddingValues, weatherData ->
                Box(modifier = Modifier.fillMaxSize()) {
                    if (weatherData.weatherResult.weather.isNotEmpty()) {
                        val weatherId = weatherData.weatherResult.weather.firstOrNull()?.id ?: 0
                        val backgroundComposable = getWeatherAnimation(weatherId)
                        backgroundComposable()
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .verticalScroll(detailState.scrollState),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        DetailScreenGroup(weatherData, vm, temperatureUnit)
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DetailScreenGroup(
    weatherData: DetailViewModel.WeatherData,
    vm: DetailViewModel,
    temperatureUnit: String
) {
    WeatherTitleSection(weatherData.weatherResult)
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