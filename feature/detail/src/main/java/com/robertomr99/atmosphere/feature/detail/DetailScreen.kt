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
import androidx.hilt.navigation.compose.hiltViewModel
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

@SuppressLint("DefaultLocale")
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun DetailScreen(
    cityName: String,
    temperatureUnit: String,
    onBack: () -> Unit,
    vm: DetailViewModel = hiltViewModel()
) {
    val stateValue by vm.state.collectAsState()
    val isError by NavigationState.cityError.collectAsState()

    DetailScreen(
        state = stateValue,
        cityName = cityName,
        displayedCityName = vm.cityName,
        country = vm.country,
        temperatureUnit = temperatureUnit,
        errorMessage = isError,
        isFavCity = when (val currentState = stateValue) {
            is Result.Success -> currentState.data.isFavCity
            else -> false
        },
        onBack = onBack,
        onLoadCityWeather = { city, unit -> vm.loadCityWeather(city, unit) },
        onUpdateCityFav = vm::updateCityFav,
        onErrorDetected = onBack,
        vm = vm
    )
}

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
    state: Result<DetailViewModel.WeatherData>,
    cityName: String,
    displayedCityName: String,
    country: String,
    temperatureUnit: String,
    errorMessage: String?,
    isFavCity: Boolean,
    onBack: () -> Unit,
    onLoadCityWeather: (String, String) -> Unit,
    onUpdateCityFav: (Boolean) -> Unit,
    onErrorDetected: () -> Unit,
    vm: DetailViewModel? = null
) {
    key(cityName) {
        LaunchedEffect(errorMessage) {
            if (!errorMessage.isNullOrEmpty()) {
                onErrorDetected()
            }
        }

        LaunchedEffect(cityName) {
            if (cityName.isNotBlank()) {
                onLoadCityWeather(cityName, temperatureUnit)
            }
        }

        Screen {
            val detailState = rememberDetailState(
                isFavCity = isFavCity
            )

            AcScaffold(
                state = state,
                modifier = Modifier.nestedScroll(detailState.scrollBehavior.nestedScrollConnection),
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "$displayedCityName, $country",
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
                                    onUpdateCityFav(isFav)
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
                        if (vm != null) {
                            DetailScreenGroup(weatherData, vm, temperatureUnit)
                        } else {
                            DetailScreenGroup(weatherData, temperatureUnit)
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DetailScreenGroup(
    weatherData: DetailViewModel.WeatherData,
    temperatureUnit: String
) {
    WeatherTitleSection(weatherData.weatherResult)
    HourlyForecastSection(emptyList())
    DailyForecastSection(emptyList(), temperatureUnit)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 16.dp)
    ) {
        FeelsLikeTempSection(
            feelsLikeTemp = weatherData.weatherResult.main?.feelsLike?.toInt(),
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp)
                .height(100.dp)
        )
        HumiditySection(
            humidity = weatherData.weatherResult.main?.humidity,
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp)
                .height(100.dp)
        )
    }

    WindSection(
        wind = weatherData.weatherResult.wind,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 16.dp)
    )
}