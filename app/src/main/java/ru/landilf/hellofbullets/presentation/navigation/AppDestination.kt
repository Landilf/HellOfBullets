package ru.landilf.hellofbullets.presentation.navigation

sealed class AppDestination(val route: String) {
    object MainMenu : AppDestination("main_menu")
    object SelectMode : AppDestination("select_mode")
    object Skills : AppDestination("skills")
    object Equipment : AppDestination("equipment")
    object Shop : AppDestination("shop")
    object Settings : AppDestination("settings")

    object Survival : AppDestination("survival")
    object Duel : AppDestination("duel")

    object SurvivalGame : AppDestination("survival_game")
    object SurvivalRecords : AppDestination("survival_records")
}