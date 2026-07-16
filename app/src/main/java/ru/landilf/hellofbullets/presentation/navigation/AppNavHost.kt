package ru.landilf.hellofbullets.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.landilf.hellofbullets.R
import ru.landilf.hellofbullets.presentation.common.PlaceholderScreen
import ru.landilf.hellofbullets.presentation.mainmenu.MainMenuAction
import ru.landilf.hellofbullets.presentation.mainmenu.MainMenuScreen
import ru.landilf.hellofbullets.presentation.mainmenu.MainMenuUiState
import ru.landilf.hellofbullets.presentation.selectmode.SelectModeScreen
import ru.landilf.hellofbullets.presentation.survival.SurvivalHomeScreen
import ru.landilf.hellofbullets.presentation.survival.game.SurvivalGameAction
import ru.landilf.hellofbullets.presentation.survival.game.SurvivalGameScreen
import ru.landilf.hellofbullets.presentation.survival.game.SurvivalGameViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    mainMenuState: MainMenuUiState,
    onExit: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.MainMenu.route
    ) {
        composable(AppDestination.MainMenu.route) {
            MainMenuScreen(
                state = mainMenuState,
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
                onSurvivalClick = { navController.navigate(AppDestination.Survival.route) },
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

        composable(AppDestination.Survival.route) {
            SurvivalHomeScreen(
                onStartGameClick = { navController.navigate(AppDestination.SurvivalGame.route) },
                onShowRecordsClick = { navController.navigate(AppDestination.SurvivalRecords.route) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(AppDestination.Duel.route) {
            PlaceholderScreen(
                titleRes = R.string.select_mode_duel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(AppDestination.SurvivalGame.route) {
            val viewModel: SurvivalGameViewModel = hiltViewModel()
            val state = viewModel.uiState.collectAsState()

            SurvivalGameScreen(
                state = state.value,
                onAction = { action ->
                    when (action) {
                        SurvivalGameAction.OnBackClick -> navController.popBackStack()
                        is SurvivalGameAction.OnPlayerDrag -> viewModel.onAction(action)
                    }
                }
            )
        }

        composable(AppDestination.SurvivalRecords.route) {
            PlaceholderScreen(
                titleRes = R.string.button_show_records,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}