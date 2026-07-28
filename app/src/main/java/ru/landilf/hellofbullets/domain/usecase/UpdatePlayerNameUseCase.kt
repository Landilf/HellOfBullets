package ru.landilf.hellofbullets.domain.usecase

import ru.landilf.hellofbullets.domain.repository.LeaderboardRepository
import javax.inject.Inject

class UpdatePlayerNameUseCase @Inject constructor(
    private val getOrCreatePlayerStateUseCase: GetOrCreatePlayerStateUseCase,
    private val savePlayerStateUseCase: SavePlayerStateUseCase,
    private val leaderboardRepository: LeaderboardRepository
) {
    suspend operator fun invoke(
        name: String
    ) {
        val normalizedName = name.trim()

        require(normalizedName.isNotEmpty()) {
            "Имя не может быть пустым"
        }
        require(normalizedName.length <= MAX_PLAYER_NAME_LENGTH) {
            "Имя должно содержать не более $MAX_PLAYER_NAME_LENGTH символов"
        }

        val playerState = getOrCreatePlayerStateUseCase()

        savePlayerStateUseCase(
            playerState.copy(
                playerProfile = playerState.playerProfile.copy(
                    name = normalizedName
                )
            )
        )

        val playerRecordId = playerState.playerProfile.id.toString()

        leaderboardRepository.getRecordById(playerRecordId)?.let { record ->
            leaderboardRepository.upsertRecord(
                record.copy(
                    playerName = normalizedName
                )
            )
        }
    }

    private companion object {
        const val MAX_PLAYER_NAME_LENGTH = 32
    }
}