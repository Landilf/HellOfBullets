package ru.landilf.hellofbullets.domain.usecase

import ru.landilf.hellofbullets.domain.model.player.Inventory
import ru.landilf.hellofbullets.domain.model.player.PlayerBuild
import ru.landilf.hellofbullets.domain.model.player.PlayerProfile
import ru.landilf.hellofbullets.domain.model.player.PlayerState
import javax.inject.Inject

class GetOrCreatePlayerStateUseCase @Inject constructor(
    private val loadPlayerStateUseCase: LoadPlayerStateUseCase,
    private val savePlayerStateUseCase: SavePlayerStateUseCase
) {
    suspend operator fun invoke(): PlayerState {
        return loadPlayerStateUseCase() ?: createInitialPlayerState().also {
            savePlayerStateUseCase(it)
        }
    }

    private fun createInitialPlayerState(): PlayerState {
        return PlayerState(
            playerProfile = PlayerProfile(
                id = 1L,
                name = "Player",
                level = 1,
                expAmount = 0,
                silverAmount = 0
            ),
            playerBuild = PlayerBuild(
                equippedWeaponItem = null,
                equippedArmorItem = null,
                equippedArtifactItem = null,
                firstSkillSlot = null,
                secondSkillSlot = null
            ),
            inventory = Inventory(
                ownedItems = emptyList()
            )
        )
    }
}