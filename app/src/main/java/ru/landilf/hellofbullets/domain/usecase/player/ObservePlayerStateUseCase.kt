package ru.landilf.hellofbullets.domain.usecase.player

import kotlinx.coroutines.flow.Flow
import ru.landilf.hellofbullets.domain.model.player.PlayerState
import ru.landilf.hellofbullets.domain.repository.PlayerRepository
import javax.inject.Inject

class ObservePlayerStateUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    operator fun invoke(): Flow<PlayerState?> {
        return playerRepository.observePlayerState()
    }
}