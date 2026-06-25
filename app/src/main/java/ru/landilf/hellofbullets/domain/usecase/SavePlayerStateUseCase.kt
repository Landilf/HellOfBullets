package ru.landilf.hellofbullets.domain.usecase

import ru.landilf.hellofbullets.domain.model.player.PlayerState
import ru.landilf.hellofbullets.domain.repository.PlayerRepository

class SavePlayerStateUseCase(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(state: PlayerState) {
        playerRepository.savePlayerState(state)
    }
}