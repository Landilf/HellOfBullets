package ru.landilf.hellofbullets.presentation.survival.game

sealed interface SurvivalGameAction {
    data object OnBackClick : SurvivalGameAction
}