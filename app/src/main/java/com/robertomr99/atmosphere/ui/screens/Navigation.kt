package com.robertomr99.atmosphere.ui.screens

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
import com.robertomr99.atmosphere.App
import com.robertomr99.atmosphere.data.RegionRepository
import com.robertomr99.atmosphere.data.WeatherRepository
import com.robertomr99.atmosphere.data.datasource.LocationDataSource
import com.robertomr99.atmosphere.data.datasource.RegionDataSource
import com.robertomr99.atmosphere.data.datasource.WeatherDataSource
import com.robertomr99.atmosphere.data.datasource.local.DataStoreManager
import com.robertomr99.atmosphere.data.datasource.local.WeatherLocalDataSource
import com.robertomr99.atmosphere.ui.screens.detail.DetailScreen
import com.robertomr99.atmosphere.ui.screens.detail.DetailViewModel
import com.robertomr99.atmosphere.ui.screens.home.HomeScreen
import com.robertomr99.atmosphere.ui.screens.home.HomeViewModel

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
    val weatherRepository = WeatherRepository(
        RegionRepository(
            RegionDataSource(
                app = app,
                locationDataSource = LocationDataSource(app)
            )
        ),
        WeatherDataSource(),
        WeatherLocalDataSource(app.db.WeatherDao()),
        DataStoreManager(app)
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
                vm = viewModel { HomeViewModel(weatherRepository) }
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
                vm = viewModel{DetailViewModel(weatherRepository)}
            )
        }
    }
}