package ru.landilf.hellofbullets.domain.model.player

import ru.landilf.hellofbullets.domain.model.common.Vector2

data class PlayerRuntimeState(
    val currentHp: Int,
    val position: Vector2,
    val isAlive: Boolean
)
