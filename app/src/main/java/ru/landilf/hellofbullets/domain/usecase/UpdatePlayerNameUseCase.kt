package ru.landilf.hellofbullets.domain.usecase

import ru.landilf.hellofbullets.domain.model.player.PlayerState
import javax.inject.Inject

class UpdatePlayerNameUseCase @Inject constructor(
    private val getOrCreatePlayerStateUseCase: GetOrCreatePlayerStateUseCase,
    private val savePlayerStateUseCase: SavePlayerStateUseCase
) {
    suspend operator fun invoke(
        name: String
    ) {
        val normalizedName = name.trim()

        require(normalizedName.isNotEmpty())
        require(normalizedName.length <= MAX_PLAYER_NAME_LENGTH)

        val playerState = getOrCreatePlayerStateUseCase()

        savePlayerStateUseCase(
            PlayerState(
                playerProfile = playerState.playerProfile.copy(
                    name = normalizedName
                ),
                playerBuild = playerState.playerBuild,
                inventory = playerState.inventory
            )
        )
    }

    private companion object {
        const val MAX_PLAYER_NAME_LENGTH = 32
    }
}