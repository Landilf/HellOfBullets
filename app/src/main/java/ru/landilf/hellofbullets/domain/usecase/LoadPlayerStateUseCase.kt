package ru.landilf.hellofbullets.domain.usecase

import ru.landilf.hellofbullets.domain.model.player.PlayerState
import ru.landilf.hellofbullets.domain.repository.PlayerRepository

class LoadPlayerStateUseCase(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(): PlayerState? {
        return playerRepository.getPlayerState()
    }
}