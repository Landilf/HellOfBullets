package ru.landilf.hellofbullets.domain.usecase.equipment

import ru.landilf.hellofbullets.domain.engine.equipment.EquipmentLevelUpgradeCostCalculator
import ru.landilf.hellofbullets.domain.generator.EquipmentRandomGenerator
import ru.landilf.hellofbullets.domain.model.equipment.Item
import ru.landilf.hellofbullets.domain.model.equipment.definition.EquipmentDefinition
import ru.landilf.hellofbullets.domain.model.equipment.upgrade.EquipmentLevelUpgradeResult
import ru.landilf.hellofbullets.domain.model.equipment.upgrade.EquipmentLevelUpgradeRules.isFifthLevel
import ru.landilf.hellofbullets.domain.model.equipment.upgrade.EquipmentLevelUpgradeStep
import ru.landilf.hellofbullets.domain.model.equipment.upgrade.EquipmentStatChange
import ru.landilf.hellofbullets.domain.model.equipment.upgrade.EquipmentStatSource
import ru.landilf.hellofbullets.domain.model.equipment.upgrade.FifthLevelUpgradeTarget
import ru.landilf.hellofbullets.domain.model.equipment.upgrade.statValue
import ru.landilf.hellofbullets.domain.model.player.PlayerStateUpdate
import ru.landilf.hellofbullets.domain.repository.EquipmentDefinitionRepository
import ru.landilf.hellofbullets.domain.repository.PlayerRepository
import javax.inject.Inject

class UpgradePlayerEquipmentLevelsUseCase @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val equipmentDefinitionRepository: EquipmentDefinitionRepository,
    private val equipmentLevelUpgradeCostCalculator: EquipmentLevelUpgradeCostCalculator,
    private val upgradeEquipmentLevelUseCase: UpgradeEquipmentLevelUseCase,
    private val equipmentRandomGenerator: EquipmentRandomGenerator
) {
    suspend operator fun invoke(
        itemId: Long,
        levelsToUpgrade: Int
    ): EquipmentLevelUpgradeResult {
        require(levelsToUpgrade > 0) {
            "Количество уровней для улучшения должно быть положительным"
        }

        return playerRepository.updatePlayerState { playerState ->
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

            val targetLevel = item.level + levelsToUpgrade
            require(targetLevel <= item.maxLevel) {
                "Нельзя улучшить предмет выше максимального уровня"
            }

            val spentSilver = calculateTotalCost(
                definition = definition,
                fromLevel = item.level,
                targetLevel = targetLevel
            )
            require(playerState.playerProfile.silverAmount >= spentSilver) {
                "Недостаточно серебра для улучшения предмета"
            }

            val upgradeResult = applyUpgrades(
                item = item,
                definition = definition,
                levelsToUpgrade = levelsToUpgrade,
                spentSilver = spentSilver
            )

            PlayerStateUpdate(
                updatedState = playerState.copy(
                    playerProfile = playerState.playerProfile.copy(
                        silverAmount = playerState.playerProfile.silverAmount - spentSilver
                    )
                ).replaceItem(upgradeResult.upgradedItem),
                result = upgradeResult
            )
        }
    }

    private fun calculateTotalCost(
        definition: EquipmentDefinition,
        fromLevel: Int,
        targetLevel: Int
    ): Int {
        return ((fromLevel + 1)..targetLevel).sumOf { level ->
            equipmentLevelUpgradeCostCalculator(
                baseCost = definition.baseLevelUpgradeCost,
                targetLevel = level
            )
        }
    }

    private fun applyUpgrades(
        item: Item,
        definition: EquipmentDefinition,
        levelsToUpgrade: Int,
        spentSilver: Int
    ): EquipmentLevelUpgradeResult {
        var currentItem = item
        val steps = buildList {
            repeat(levelsToUpgrade) {
                val updatedItem = upgradeEquipmentLevelUseCase(
                    item = currentItem,
                    definition = definition,
                    fifthLevelUpgradeTarget = selectFifthLevelUpgradeTarget(
                        targetLevel = currentItem.level + 1
                    )
                )

                add(
                    EquipmentLevelUpgradeStep(
                        targetLevel = updatedItem.level,
                        statChanges = createStatChanges(
                            previousItem = currentItem,
                            updatedItem = updatedItem,
                            definition = definition
                        )
                    )
                )
                currentItem = updatedItem
            }
        }

        return EquipmentLevelUpgradeResult(
            originalItem = item,
            upgradedItem = currentItem,
            spentSilver = spentSilver,
            steps = steps
        )
    }

    private fun selectFifthLevelUpgradeTarget(
        targetLevel: Int
    ): FifthLevelUpgradeTarget? {
        if (!isFifthLevel(targetLevel)) {
            return null
        }

        return FifthLevelUpgradeTarget.entries[
            equipmentRandomGenerator.nextInt(
                until = FifthLevelUpgradeTarget.entries.size
            )
        ]
    }

    private fun createStatChanges(
        previousItem: Item,
        updatedItem: Item,
        definition: EquipmentDefinition
    ): List<EquipmentStatChange> {
        return EquipmentStatSource.entries.mapNotNull { source ->
            val previousValue = previousItem.statValue(source)
            val updatedValue = updatedItem.statValue(source)

            if (previousValue == updatedValue) {
                return@mapNotNull null
            }

            EquipmentStatChange(
                source = source,
                statType = when (source) {
                    EquipmentStatSource.PRIMARY_FIRST -> definition.primaryFirstStatType
                    EquipmentStatSource.PRIMARY_SECOND -> definition.primarySecondStatType
                    EquipmentStatSource.ADDITIONAL -> previousItem.additionalStatType
                },
                previousValue = previousValue,
                updatedValue = updatedValue
            )
        }
    }
}