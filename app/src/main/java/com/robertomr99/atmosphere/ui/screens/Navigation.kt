package com.robertomr99.atmosphere.ui.screens

import AndroidLogger
import android.location.Geocoder
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.location.LocationServices
import com.robertomr99.atmosphere.App
import com.robertomr99.atmosphere.data.repository.RegionRepository
import com.robertomr99.atmosphere.data.repository.WeatherRepository
import com.robertomr99.atmosphere.framework.DataStoreManager
import com.robertomr99.atmosphere.framework.GeocoderRegionDataSource
import com.robertomr99.atmosphere.framework.PlayServicesLocationDataSource
import com.robertomr99.atmosphere.framework.WeatherRoomDataSource
import com.robertomr99.atmosphere.framework.WeatherServerDataSource
import com.robertomr99.atmosphere.framework.remote.GeoCodingClient
import com.robertomr99.atmosphere.framework.remote.WeatherClient
import com.robertomr99.atmosphere.ui.screens.detail.DetailScreen
import com.robertomr99.atmosphere.ui.screens.detail.DetailViewModel
import com.robertomr99.atmosphere.ui.screens.home.HomeScreen
import com.robertomr99.atmosphere.ui.screens.home.HomeViewModel
import com.robertomr99.atmosphere.usecases.DeleteFavouriteCityUseCase
import com.robertomr99.atmosphere.usecases.FetchFavouritesCitiesUseCase
import com.robertomr99.atmosphere.usecases.FetchSuggestionsForCityUseCase
import com.robertomr99.atmosphere.usecases.FetchWeatherAndForecastUseCase
import com.robertomr99.atmosphere.usecases.FindFavCityUseCase
import com.robertomr99.atmosphere.usecases.SaveFavouriteCityUseCase

sealed class NavScreen(val route: String) {
    data object Home: NavScreen("home")
    data object Detail: NavScreen("detail/{${NavArgs.City.key}}/{${NavArgs.TemperatureUnit.key}}") {
        fun createRoute(city: String, temperatureUnit: String) =
            "detail/$city/$temperatureUnit"
    }
}

enum class NavArgs(val key: String){
    City("city"),
    TemperatureUnit("temperatureUnit")
}

private const val TRANSITION_DURATION = 600

val enterTransition = slideInHorizontally(
    initialOffsetX = { fullWidth -> fullWidth },
    animationSpec = tween(600)
)

val exitTransition = slideOutHorizontally(
    targetOffsetX = { fullWidth -> -fullWidth },
    animationSpec = tween(600)
)

val popEnterTransition = slideInHorizontally(
    initialOffsetX = { fullWidth -> -fullWidth },
    animationSpec = tween(600)
)

val popExitTransition = slideOutHorizontally(
    targetOffsetX = { fullWidth -> fullWidth },
    animationSpec = tween(600)
)

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun Navigation(){
    val navController = rememberNavController()
    val app = LocalContext.current.applicationContext as App
    val logger = AndroidLogger()
    val weatherRepository = WeatherRepository(
        RegionRepository(
            GeocoderRegionDataSource(
                geocoder = Geocoder(app),
                locationDataSource = PlayServicesLocationDataSource(
                    LocationServices.getFusedLocationProviderClient(
                        app
                    )
                )
            )
        ),
        weatherLocalDataSource = WeatherRoomDataSource(app.db.WeatherDao()),
        weatherRemoteDataSource = WeatherServerDataSource(GeoCodingClient.instance, WeatherClient.instance),
        dataStoreManager =  DataStoreManager(app),
        logger = logger
    )

    NavHost(navController = navController, startDestination = NavScreen.Home.route){

        composable(
            route = NavScreen.Home.route,
            exitTransition = {
                fadeOut(animationSpec = tween(TRANSITION_DURATION))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(TRANSITION_DURATION))
            }
        ){
            HomeScreen(
                onClick = { city, temperatureUnit ->
                    navController.navigate(
                        NavScreen.Detail.createRoute(
                            city,
                            temperatureUnit
                        )
                    )
                },
                vm = viewModel { HomeViewModel(
                    FetchFavouritesCitiesUseCase(
                        weatherRepository
                    ),
                    FetchSuggestionsForCityUseCase(
                        weatherRepository
                    ),
                    DeleteFavouriteCityUseCase(weatherRepository)
                    )
                }
            )
        }

        composable(
            route = NavScreen.Detail.route,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition },
            arguments = listOf(
                navArgument(NavArgs.City.key) {type = NavType.StringType},
                navArgument(NavArgs.TemperatureUnit.key) {type = NavType.StringType}
            )

        ){ backStackEntry ->
            val city = backStackEntry.arguments?.getString(NavArgs.City.key)
            val temperatureUnit = backStackEntry.arguments?.getString(NavArgs.TemperatureUnit.key)

            DetailScreen(
                cityName = city!!,
                temperatureUnit = temperatureUnit!!,
                onBack = {
                    navController.popBackStack()
                },
                vm = viewModel{ DetailViewModel(
                    FindFavCityUseCase(weatherRepository),
                    FetchWeatherAndForecastUseCase(
                        weatherRepository
                    ),
                    SaveFavouriteCityUseCase(weatherRepository),
                    DeleteFavouriteCityUseCase(weatherRepository)
                    )
                }
            )
        }
    }
}