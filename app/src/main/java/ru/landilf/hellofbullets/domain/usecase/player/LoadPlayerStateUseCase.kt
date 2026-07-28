package ru.landilf.hellofbullets.domain.usecase.player

import ru.landilf.hellofbullets.domain.model.player.PlayerState
import ru.landilf.hellofbullets.domain.repository.PlayerRepository
import javax.inject.Inject

class LoadPlayerStateUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(): PlayerState? {
        return playerRepository.getPlayerState()
    }
}