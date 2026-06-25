package com.landilf.hellofbullets.domain.usecase

import com.landilf.hellofbullets.domain.model.player.PlayerState
import com.landilf.hellofbullets.domain.repository.PlayerRepository

class LoadPlayerStateUseCase(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(): PlayerState? {
        return playerRepository.getPlayerState()
    }
}