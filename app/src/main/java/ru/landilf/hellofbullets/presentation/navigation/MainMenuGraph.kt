package ru.landilf.hellofbullets.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ru.landilf.hellofbullets.R
import ru.landilf.hellofbullets.presentation.common.PlaceholderScreen
import ru.landilf.hellofbullets.presentation.mainmenu.MainMenuAction
import ru.landilf.hellofbullets.presentation.mainmenu.MainMenuScreen
import ru.landilf.hellofbullets.presentation.mainmenu.MainMenuUiState
import ru.landilf.hellofbullets.presentation.selectmode.SelectModeScreen

fun NavGraphBuilder.mainMenuGraph(
    navController: NavController,
    mainMenuUiState: MainMenuUiState,
    onExit: () -> Unit
) {
    composable(AppDestination.MainMenu.route) {
        MainMenuScreen(
            state = mainMenuUiState,
            onClickAction = { action ->
                when (action) {
                    MainMenuAction.SelectMode ->
                        navController.navigate(AppDestination.SelectMode.route)

                    MainMenuAction.Skills ->
                        navController.navigate(AppDestination.Skills.route)

                    MainMenuAction.Equipment ->
                        navController.navigate(AppDestination.Equipment.route)

                    MainMenuAction.Shop ->
                        navController.navigate(AppDestination.Shop.route)

                    MainMenuAction.Settings ->
                        navController.navigate(AppDestination.Settings.route)

                    MainMenuAction.Exit ->
                        onExit()
                }
            }
        )
    }

    composable(AppDestination.SelectMode.route) {
        SelectModeScreen(
            onSurvivalClick = { navController.navigate(AppDestination.SurvivalHome.route) },
            onDuelClick = { navController.navigate(AppDestination.Duel.route) },
            onBackClick = { navController.popBackStack() }
        )
    }

    composable(AppDestination.Skills.route) {
        PlaceholderScreen(
            titleRes = R.string.main_menu_skills,
            onBackClick = { navController.popBackStack() }
        )
    }

    composable(AppDestination.Equipment.route) {
        PlaceholderScreen(
            titleRes = R.string.main_menu_equipment,
            onBackClick = { navController.popBackStack() }
        )
    }

    composable(AppDestination.Shop.route) {
        PlaceholderScreen(
            titleRes = R.string.main_menu_shop,
            onBackClick = { navController.popBackStack() }
        )
    }

    composable(AppDestination.Settings.route) {
        PlaceholderScreen(
            titleRes = R.string.main_menu_settings,
            onBackClick = { navController.popBackStack() }
        )
    }

    composable(AppDestination.Duel.route) {
        PlaceholderScreen(
            titleRes = R.string.select_mode_duel,
            onBackClick = { navController.popBackStack() }
        )
    }
}