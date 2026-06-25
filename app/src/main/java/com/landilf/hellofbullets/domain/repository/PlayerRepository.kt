package com.landilf.hellofbullets.domain.repository

import com.landilf.hellofbullets.domain.model.player.PlayerState

interface PlayerRepository {
    suspend fun getPlayerState(): PlayerState?
    suspend fun savePlayerState(state: PlayerState)
    suspend fun clearPlayerState()
}