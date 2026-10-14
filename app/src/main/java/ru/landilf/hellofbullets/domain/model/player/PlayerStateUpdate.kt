package ru.landilf.hellofbullets.domain.model.player

data class PlayerStateUpdate<T>(
    val updatedState: PlayerState,
    val result: T
)
