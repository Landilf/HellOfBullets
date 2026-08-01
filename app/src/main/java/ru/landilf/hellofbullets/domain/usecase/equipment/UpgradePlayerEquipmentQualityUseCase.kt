package ru.landilf.hellofbullets.domain.usecase.equipment

import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQualityUpgradeResult
import ru.landilf.hellofbullets.domain.repository.EquipmentDefinitionRepository
import ru.landilf.hellofbullets.domain.usecase.player.GetOrCreatePlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.player.SavePlayerStateUseCase
import javax.inject.Inject

class UpgradePlayerEquipmentQualityUseCase @Inject constructor(
    private val getOrCreatePlayerStateUseCase: GetOrCreatePlayerStateUseCase,
    private val savePlayerStateUseCase: SavePlayerStateUseCase,
    private val equipmentDefinitionRepository: EquipmentDefinitionRepository,
    private val upgradeEquipmentQualityUseCase: UpgradeEquipmentQualityUseCase
) {
    suspend operator fun invoke(
        itemId: Long,
        materialItemIds: List<Long>
    ): EquipmentQualityUpgradeResult {
        val playerState = getOrCreatePlayerStateUseCase()
        val item = requireNotNull(
            playerState.inventory.ownedItems.firstOrNull { it.id == itemId }
        ) {
            "Предмет с id $itemId не найден в инвентаре"
        }
        val materials = materialItemIds.map { materialItemId ->
            requireNotNull(
                playerState.inventory.ownedItems.firstOrNull { it.id == materialItemId }
            ) {
                "Материал с id $materialItemId не найден в инвентаре"
            }
        }
        val definition = checkNotNull(
            equipmentDefinitionRepository.getDefinitionById(item.definitionId)
        ) {
            "Не найдено определение предмета с id ${item.definitionId}"
        }

        val result = upgradeEquipmentQualityUseCase(
            item = item,
            definition = definition,
            materials = materials
        )
        val updatedPlayerState = playerState.replaceItem(
            updatedItem = result.upgradedItem,
            removedItemIds = result.consumedMaterialIds.toSet()
        )

        savePlayerStateUseCase(updatedPlayerState)

        return result
    }
}