package ru.landilf.hellofbullets.domain.usecase.equipment

import ru.landilf.hellofbullets.domain.model.player.EquipmentSlot
import ru.landilf.hellofbullets.domain.model.player.PlayerState
import ru.landilf.hellofbullets.domain.usecase.player.GetOrCreatePlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.player.SavePlayerStateUseCase
import javax.inject.Inject

class SetEquippedItemUseCase @Inject constructor(
    private val getOrCreatePlayerStateUseCase: GetOrCreatePlayerStateUseCase,
    private val savePlayerStateUseCase: SavePlayerStateUseCase
) {
    suspend operator fun invoke(
        slot: EquipmentSlot,
        itemId: Long?
    ): PlayerState {
        val playerState = getOrCreatePlayerStateUseCase()

        val item = itemId?.let { id ->
            requireNotNull(playerState.inventory.ownedItems.firstOrNull { it.id == id }) {
                "Предмет с id $id не найден в инвентаре"
            }
        }

        val updatedPlayerState = playerState.setEquippedItem(
            slot = slot,
            item = item
        )

        savePlayerStateUseCase(updatedPlayerState)

        return updatedPlayerState
    }
}