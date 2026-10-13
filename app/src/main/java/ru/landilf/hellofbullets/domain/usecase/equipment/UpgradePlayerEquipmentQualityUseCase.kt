package ru.landilf.hellofbullets.domain.usecase.equipment

import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQualityUpgradeResult
import ru.landilf.hellofbullets.domain.model.player.PlayerStateUpdate
import ru.landilf.hellofbullets.domain.repository.EquipmentDefinitionRepository
import ru.landilf.hellofbullets.domain.repository.PlayerRepository
import javax.inject.Inject

class UpgradePlayerEquipmentQualityUseCase @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val equipmentDefinitionRepository: EquipmentDefinitionRepository,
    private val upgradeEquipmentQualityUseCase: UpgradeEquipmentQualityUseCase
) {
    suspend operator fun invoke(
        itemId: Long,
        materialItemIds: List<Long>
    ): EquipmentQualityUpgradeResult {
        return playerRepository.updatePlayerState { playerState ->
            val item = requireNotNull(
                playerState.inventory.ownedItems.firstOrNull { it.id == itemId }
            ) {
                "Предмет с id $itemId не найден в инвентаре"
            }
            val equippedItemIds = setOfNotNull(
                playerState.playerBuild.equippedWeaponItem?.id,
                playerState.playerBuild.equippedArmorItem?.id,
                playerState.playerBuild.equippedArtifactItem?.id
            )
            require(materialItemIds.none { it in equippedItemIds }) {
                "Нельзя использовать экипированный предмет как материал"
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

            PlayerStateUpdate(
                updatedState = updatedPlayerState,
                result = result
            )
        }
    }
}