package ru.landilf.hellofbullets.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.landilf.hellofbullets.domain.model.player.PlayerState

interface PlayerRepository {
    suspend fun getPlayerState(): PlayerState?
    suspend fun savePlayerState(state: PlayerState)
    suspend fun clearPlayerState()
    fun observePlayerState(): Flow<PlayerState?>
}