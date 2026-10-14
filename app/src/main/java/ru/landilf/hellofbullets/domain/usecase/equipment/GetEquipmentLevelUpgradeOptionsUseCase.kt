package ru.landilf.hellofbullets.domain.usecase.equipment

import ru.landilf.hellofbullets.domain.engine.equipment.EquipmentLevelUpgradeCostCalculator
import ru.landilf.hellofbullets.domain.engine.equipment.EquipmentLevelUpgradeStatCalculator
import ru.landilf.hellofbullets.domain.model.equipment.Item
import ru.landilf.hellofbullets.domain.model.equipment.definition.EquipmentDefinition
import ru.landilf.hellofbullets.domain.model.equipment.upgrade.EquipmentLevelUpgradeOption
import ru.landilf.hellofbullets.domain.model.equipment.upgrade.EquipmentLevelUpgradeOptions
import ru.landilf.hellofbullets.domain.model.equipment.upgrade.EquipmentLevelUpgradeRules.isFifthLevel
import ru.landilf.hellofbullets.domain.model.equipment.upgrade.EquipmentStatChange
import ru.landilf.hellofbullets.domain.model.equipment.upgrade.EquipmentStatSource
import ru.landilf.hellofbullets.domain.model.equipment.upgrade.statValue
import ru.landilf.hellofbullets.domain.repository.EquipmentDefinitionRepository
import ru.landilf.hellofbullets.domain.usecase.player.GetOrCreatePlayerStateUseCase
import javax.inject.Inject

class GetEquipmentLevelUpgradeOptionsUseCase @Inject constructor(
    private val getOrCreatePlayerStateUseCase: GetOrCreatePlayerStateUseCase,
    private val equipmentDefinitionRepository: EquipmentDefinitionRepository,
    private val equipmentLevelUpgradeCostCalculator: EquipmentLevelUpgradeCostCalculator,
    private val equipmentLevelUpgradeStatCalculator: EquipmentLevelUpgradeStatCalculator
) {
    suspend operator fun invoke(
        itemId: Long
    ): EquipmentLevelUpgradeOptions {
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

        return EquipmentLevelUpgradeOptions(
            itemId = item.id,
            currentLevel = item.level,
            maxLevel = item.maxLevel,
            availableSilver = playerState.playerProfile.silverAmount,
            options = createOptions(
                item = item,
                definition = definition
            )
        )
    }

    private fun createOptions(
        item: Item,
        definition: EquipmentDefinition
    ): List<EquipmentLevelUpgradeOption> {
        val options = mutableListOf<EquipmentLevelUpgradeOption>()
        var totalCost = 0
        var guaranteedPrimaryFirstIncrement = 0f
        val randomUpgradeLevels = mutableListOf<Int>()

        for (targetLevel in (item.level + 1)..item.maxLevel) {
            val levelCost = equipmentLevelUpgradeCostCalculator(
                baseCost = definition.baseLevelUpgradeCost,
                targetLevel = targetLevel
            )

            totalCost += levelCost
            guaranteedPrimaryFirstIncrement += equipmentLevelUpgradeStatCalculator.primaryFirstIncrement(
                item = item,
                definition = definition,
                targetLevel = targetLevel
            )

            if (isFifthLevel(targetLevel)) {
                randomUpgradeLevels += targetLevel
            }

            options += EquipmentLevelUpgradeOption(
                levelsToUpgrade = targetLevel - item.level,
                targetLevel = targetLevel,
                totalCost = totalCost,
                guaranteedStatChanges = listOf(
                    EquipmentStatChange(
                        source = EquipmentStatSource.PRIMARY_FIRST,
                        statType = definition.primaryFirstStatType,
                        previousValue = item.statValue(EquipmentStatSource.PRIMARY_FIRST),
                        updatedValue =
                            item.statValue(EquipmentStatSource.PRIMARY_FIRST) +
                                    guaranteedPrimaryFirstIncrement
                    )
                ),
                randomUpgradeLevels = randomUpgradeLevels.toList()
            )
        }

        return options
    }
}