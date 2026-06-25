package com.landilf.hellofbullets.domain.usecase

import com.landilf.hellofbullets.domain.model.player.PlayerState
import com.landilf.hellofbullets.domain.repository.PlayerRepository

class SavePlayerStateUseCase(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(state: PlayerState) {
        playerRepository.savePlayerState(state)
    }
}