package com.robertomr99.atmosphere.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.robertomr99.atmosphere.ui.screens.detail.DetailScreen
import com.robertomr99.atmosphere.ui.screens.home.HomeScreen

sealed class NavScreen(val route: String) {
    data object Home: NavScreen("home")
    data object Detail: NavScreen("detail/{${NavArgs.City.key}}/{${NavArgs.Region.key}}/{${NavArgs.TemperatureUnit.key}}") {
        fun createRoute(city: String, region: String, temperatureUnit: String) =
            "detail/$city/$region/$temperatureUnit"
    }
}

enum class NavArgs(val key: String){
    City("city"),
    Region("region"),
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
            HomeScreen(onClick = { city, region, temperatureUnit ->
                navController.navigate(NavScreen.Detail.createRoute(city, region, temperatureUnit))
            })
        }

        composable(
            route = NavScreen.Detail.route,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition },
            arguments = listOf(
                navArgument(NavArgs.City.key) {type = NavType.StringType},
                navArgument(NavArgs.Region.key) {type = NavType.StringType},
                navArgument(NavArgs.TemperatureUnit.key) {type = NavType.StringType}
            )

        ){ backStackEntry ->
            val city = backStackEntry.arguments?.getString(NavArgs.City.key)
            val region = backStackEntry.arguments?.getString(NavArgs.Region.key)
            val temperatureUnit = backStackEntry.arguments?.getString(NavArgs.TemperatureUnit.key)

            DetailScreen(
                cityName = city!!,
                region = region!!,
                temperatureUnit = temperatureUnit!!,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}