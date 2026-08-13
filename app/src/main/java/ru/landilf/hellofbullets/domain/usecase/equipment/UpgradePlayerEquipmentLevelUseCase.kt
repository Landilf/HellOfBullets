package ru.landilf.hellofbullets.domain.usecase.equipment

import ru.landilf.hellofbullets.domain.engine.equipment.EquipmentLevelUpgradeCostCalculator
import ru.landilf.hellofbullets.domain.generator.EquipmentRandomGenerator
import ru.landilf.hellofbullets.domain.model.equipment.Item
import ru.landilf.hellofbullets.domain.model.equipment.upgrade.EquipmentLevelUpgradeRules.isFifthLevel
import ru.landilf.hellofbullets.domain.model.equipment.upgrade.FifthLevelUpgradeTarget
import ru.landilf.hellofbullets.domain.repository.EquipmentDefinitionRepository
import ru.landilf.hellofbullets.domain.usecase.player.GetOrCreatePlayerStateUseCase
import ru.landilf.hellofbullets.domain.usecase.player.SavePlayerStateUseCase
import javax.inject.Inject

class UpgradePlayerEquipmentLevelUseCase @Inject constructor(
    private val getOrCreatePlayerStateUseCase: GetOrCreatePlayerStateUseCase,
    private val savePlayerStateUseCase: SavePlayerStateUseCase,
    private val equipmentDefinitionRepository: EquipmentDefinitionRepository,
    private val equipmentLevelUpgradeCostCalculator: EquipmentLevelUpgradeCostCalculator,
    private val upgradeEquipmentLevelUseCase: UpgradeEquipmentLevelUseCase,
    private val equipmentRandomGenerator: EquipmentRandomGenerator
) {
    suspend operator fun invoke(
        itemId: Long
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

        val fifthLevelUpgradeTarget = selectFifthLevelUpgradeTarget(
            nextLevel = item.level + 1
        )

        val updatedItem = upgradeEquipmentLevelUseCase(
            item = item,
            definition = definition,
            fifthLevelUpgradeTarget = fifthLevelUpgradeTarget
        )

        val upgradeCost = equipmentLevelUpgradeCostCalculator(
            baseCost = definition.baseLevelUpgradeCost,
            targetLevel = updatedItem.level
        )

        require(playerState.playerProfile.silverAmount >= upgradeCost) {
            "Недостаточно серебра для улучшения предмета"
        }

        val updatedPlayerState = playerState
            .copy(
                playerProfile = playerState.playerProfile.copy(
                    silverAmount = playerState.playerProfile.silverAmount - upgradeCost
                )
            )
            .replaceItem(updatedItem)

        savePlayerStateUseCase(updatedPlayerState)

        return updatedItem
    }

    private fun selectFifthLevelUpgradeTarget(
        nextLevel: Int
    ): FifthLevelUpgradeTarget? {
        if (!isFifthLevel(nextLevel)) {
            return null
        }

        return FifthLevelUpgradeTarget.entries[equipmentRandomGenerator.nextInt(
            FifthLevelUpgradeTarget.entries.size
        )]
    }
}