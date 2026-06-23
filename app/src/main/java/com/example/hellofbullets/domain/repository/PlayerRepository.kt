package com.example.hellofbullets.domain.repository

import com.example.hellofbullets.domain.model.player.PlayerState

interface PlayerRepository {
    suspend fun getPlayerState(): PlayerState?
    suspend fun savePlayerState(state: PlayerState)
    suspend fun clearPlayerState()
}