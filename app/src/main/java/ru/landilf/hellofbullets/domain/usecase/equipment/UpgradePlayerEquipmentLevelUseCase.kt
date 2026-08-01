package ru.landilf.hellofbullets.domain.usecase.equipment

import ru.landilf.hellofbullets.domain.model.equipment.Item
import ru.landilf.hellofbullets.domain.repository.EquipmentDefinitionRepository
import ru.landilf.hellofbullets.domain.usecase.player.GetOrCreatePlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.player.SavePlayerStateUseCase
import javax.inject.Inject

class UpgradePlayerEquipmentLevelUseCase @Inject constructor(
    private val getOrCreatePlayerStateUseCase: GetOrCreatePlayerStateUseCase,
    private val savePlayerStateUseCase: SavePlayerStateUseCase,
    private val equipmentDefinitionRepository: EquipmentDefinitionRepository,
    private val upgradeEquipmentLevelUseCase: UpgradeEquipmentLevelUseCase
) {
    suspend operator fun invoke(
        itemId: Long,
        fifthLevelUpgradeTarget: FifthLevelUpgradeTarget
    ): Item {
        val playerState = getOrCreatePlayerStateUseCase()
        val item = requireNotNull(
            playerState.inventory.ownedItems.firstOrNull { it.id == itemId }
        ) {
            "Предмет с id $itemId не найден в инвентаре"
        }
        val definition = checkNotNull(
            equipmentDefinitionRepository.getDefinitionById(item.definitionId)
        ) {
            "Не найдено определение предмета с id ${item.definitionId}"
        }

        val updatedItem = upgradeEquipmentLevelUseCase(
            item = item,
            definition = definition,
            fifthLevelUpgradeTarget = fifthLevelUpgradeTarget
        )
        val updatedPlayerState = playerState.replaceItem(updatedItem)

        savePlayerStateUseCase(updatedPlayerState)

        return updatedItem
    }
}