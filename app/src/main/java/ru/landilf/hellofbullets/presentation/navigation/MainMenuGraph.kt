package ru.landilf.hellofbullets.presentation.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ru.landilf.hellofbullets.R
import ru.landilf.hellofbullets.presentation.common.PlaceholderScreen
import ru.landilf.hellofbullets.presentation.equipment.EquipmentScreen
import ru.landilf.hellofbullets.presentation.equipment.EquipmentViewModel
import ru.landilf.hellofbullets.presentation.selectmode.SelectModeScreen
import ru.landilf.hellofbullets.presentation.settings.SettingsScreen
import ru.landilf.hellofbullets.presentation.settings.SettingsViewModel
import ru.landilf.hellofbullets.presentation.shop.ShopScreen
import ru.landilf.hellofbullets.presentation.shop.ShopViewModel
import ru.landilf.hellofbullets.presentation.skills.SkillsScreen
import ru.landilf.hellofbullets.presentation.skills.SkillsViewModel

fun NavGraphBuilder.mainMenuGraph(
    navController: NavController
) {
    composable(AppDestination.SelectMode.route) {
        SelectModeScreen(
            onSurvivalClick = { navController.navigate(AppDestination.SurvivalHome.route) },
            onDuelClick = { navController.navigate(AppDestination.Duel.route) }
        )
    }

    composable(AppDestination.Duel.route) {
        PlaceholderScreen(
            titleRes = R.string.select_mode_duel,
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

    composable(AppDestination.Skills.route) {
        val viewModel: SkillsViewModel = hiltViewModel()
        val state = viewModel.uiState.collectAsStateWithLifecycle()

        SkillsScreen(
            state = state.value,
            onAction = viewModel::onAction
        )
    }

    composable(AppDestination.Equipment.route) {
        val viewModel: EquipmentViewModel = hiltViewModel()
        val state = viewModel.uiState.collectAsStateWithLifecycle()

        EquipmentScreen(
            state = state.value,
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
}