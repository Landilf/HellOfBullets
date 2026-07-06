package ru.landilf.hellofbullets.presentation.navigation

sealed class AppDestination(val route: String) {
    data object MainMenu : AppDestination("main_menu")
    data object SelectMode : AppDestination("select_mode")
    data object Skills : AppDestination("skills")
    data object Equipment : AppDestination("equipment")
    data object Shop : AppDestination("shop")
    data object Settings : AppDestination("settings")

    data object Survival : AppDestination("survival")
    data object Duel : AppDestination("duel")

    data object SurvivalGame : AppDestination("survival_game")
    data object SurvivalRecords : AppDestination("survival_records")
}