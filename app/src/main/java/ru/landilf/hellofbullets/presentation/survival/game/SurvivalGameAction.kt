package ru.landilf.hellofbullets.presentation.survival.game

import ru.landilf.hellofbullets.domain.model.common.Vector2

sealed interface SurvivalGameAction {
    data object OnBackClick : SurvivalGameAction
    data class OnPlayerDrag(val dragDelta: Vector2) : SurvivalGameAction
}