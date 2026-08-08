package ru.landilf.hellofbullets.presentation.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ru.landilf.hellofbullets.R
import ru.landilf.hellofbullets.presentation.common.PlaceholderScreen
import ru.landilf.hellofbullets.presentation.selectmode.SelectModeScreen
import ru.landilf.hellofbullets.presentation.settings.SettingsScreen
import ru.landilf.hellofbullets.presentation.settings.SettingsViewModel
import ru.landilf.hellofbullets.presentation.shop.ShopScreen
import ru.landilf.hellofbullets.presentation.shop.ShopViewModel

fun NavGraphBuilder.mainMenuGraph(
    navController: NavController
) {
    composable(AppDestination.SelectMode.route) {
        SelectModeScreen(
            onSurvivalClick = { navController.navigate(AppDestination.SurvivalHome.route) },
            onDuelClick = { navController.navigate(AppDestination.Duel.route) }
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
        val viewModel: ShopViewModel = hiltViewModel()
        val state = viewModel.uiState.collectAsStateWithLifecycle()

        ShopScreen(
            state = state.value,
            events = viewModel.events,
            onAction = viewModel::onAction
        )
    }

    composable(AppDestination.Settings.route) {
        val viewModel: SettingsViewModel = hiltViewModel()
        val state = viewModel.uiState.collectAsStateWithLifecycle()

        SettingsScreen(
            state = state.value,
            events = viewModel.events,
            onAction = viewModel::onAction
        )
    }

    composable(AppDestination.Duel.route) {
        PlaceholderScreen(
            titleRes = R.string.select_mode_duel,
            onBackClick = { navController.popBackStack() }
        )
    }
}