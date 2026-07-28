package ru.landilf.hellofbullets.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AppNavHost(
    navController: NavHostController
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val navigationRoutes = setOf(
        AppDestination.Shop.route,
        AppDestination.Skills.route,
        AppDestination.SelectMode.route,
        AppDestination.Equipment.route,
        AppDestination.Settings.route,
        AppDestination.SurvivalHome.route,
        AppDestination.Duel.route
    )

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = currentRoute in navigationRoutes,
                enter = expandVertically(
                    expandFrom = Alignment.Bottom,
                    animationSpec = tween(durationMillis = 200)
                ) + fadeIn(animationSpec = tween(durationMillis = 150)),
                exit = shrinkVertically(
                    shrinkTowards = Alignment.Bottom,
                    animationSpec = tween(durationMillis = 200)
                ) + fadeOut(animationSpec = tween(durationMillis = 150))
            ) {
                AppBottomNavigation(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppDestination.SelectMode.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            mainMenuGraph(
                navController = navController
            )

            survivalGraph(
                navController = navController
            )
        }
    }
}
