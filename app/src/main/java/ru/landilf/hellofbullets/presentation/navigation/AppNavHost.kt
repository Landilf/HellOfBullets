package ru.landilf.hellofbullets.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import ru.landilf.hellofbullets.presentation.mainmenu.MainMenuUiState

@Composable
fun AppNavHost(
    navController: NavHostController,
    mainMenuUiState: MainMenuUiState,
    onExit: () -> Unit
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val navigationRoutes = setOf(
        AppDestination.Shop.route,
        AppDestination.Skills.route,
        AppDestination.SelectMode.route,
        AppDestination.Equipment.route,
        AppDestination.Settings.route
    )

    Scaffold(
        bottomBar = {
            if (currentRoute in navigationRoutes) {
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
                navController = navController,
                mainMenuUiState = mainMenuUiState,
                onExit = onExit
            )

            survivalGraph(
                navController = navController
            )
        }
    }
}
