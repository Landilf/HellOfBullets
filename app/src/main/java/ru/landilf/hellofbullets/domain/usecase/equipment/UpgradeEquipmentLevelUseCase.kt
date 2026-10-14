package ru.landilf.hellofbullets.domain.usecase.equipment

import ru.landilf.hellofbullets.domain.engine.equipment.EquipmentLevelUpgradeStatCalculator
import ru.landilf.hellofbullets.domain.model.equipment.ArmorItem
import ru.landilf.hellofbullets.domain.model.equipment.ArtifactItem
import ru.landilf.hellofbullets.domain.model.equipment.Item
import ru.landilf.hellofbullets.domain.model.equipment.WeaponItem
import ru.landilf.hellofbullets.domain.model.equipment.definition.ArmorDefinition
import ru.landilf.hellofbullets.domain.model.equipment.definition.ArtifactDefinition
import ru.landilf.hellofbullets.domain.model.equipment.definition.EquipmentDefinition
import ru.landilf.hellofbullets.domain.model.equipment.definition.WeaponDefinition
import ru.landilf.hellofbullets.domain.model.equipment.upgrade.EquipmentLevelUpgradeRules.isFifthLevel
import ru.landilf.hellofbullets.domain.model.equipment.upgrade.FifthLevelUpgradeTarget
import javax.inject.Inject

class UpgradeEquipmentLevelUseCase @Inject constructor(
    private val equipmentLevelUpgradeStatCalculator: EquipmentLevelUpgradeStatCalculator
) {
    operator fun invoke(
        item: Item,
        definition: EquipmentDefinition,
        fifthLevelUpgradeTarget: FifthLevelUpgradeTarget?
    ): Item {
        require(item.definitionId == definition.id) {
            "Определение не соответствует улучшаемому предмету"
        }
        require(item.level < item.maxLevel) {
            "Нельзя улучшить предмет максимального уровня"
        }

        val nextLevel = item.level + 1

        if (isFifthLevel(nextLevel)) {
            requireNotNull(fifthLevelUpgradeTarget) {
                "Для каждого пятого уровня нужно выбрать улучшаемую характеристику"
            }
        }

        return when (item) {
            is WeaponItem -> {
                require(definition is WeaponDefinition) {
                    "Для оружия требуется определение оружия"
                }

                upgradeWeapon(
                    item = item,
                    definition = definition,
                    nextLevel = nextLevel,
                    fifthLevelUpgradeTarget = fifthLevelUpgradeTarget
                )
            }

            is ArmorItem -> {
                require(definition is ArmorDefinition) {
                    "Для брони требуется определение брони"
                }

                upgradeArmor(
                    item = item,
                    definition = definition,
                    nextLevel = nextLevel,
                    fifthLevelUpgradeTarget = fifthLevelUpgradeTarget
                )
            }

            is ArtifactItem -> {
                require(definition is ArtifactDefinition) {
                    "Для артефакта требуется определение артефакта"
                }

                upgradeArtifact(
                    item = item,
                    definition = definition,
                    nextLevel = nextLevel,
                    fifthLevelUpgradeTarget = fifthLevelUpgradeTarget
                )
            }
        }
    }

    private fun upgradeWeapon(
        item: WeaponItem,
        definition: WeaponDefinition,
        nextLevel: Int,
        fifthLevelUpgradeTarget: FifthLevelUpgradeTarget?
    ): WeaponItem {
        val primaryFirstIncrement = equipmentLevelUpgradeStatCalculator.primaryFirstIncrement(
            item = item,
            definition = definition,
            targetLevel = nextLevel
        )

        if (!isFifthLevel(nextLevel)) {
            return item.copy(
                level = nextLevel,
                damage = item.damage + primaryFirstIncrement
            )
        }

        return when (requireNotNull(fifthLevelUpgradeTarget)) {
            FifthLevelUpgradeTarget.PRIMARY_SECOND -> {
                item.copy(
                    level = nextLevel,
                    damage = item.damage + primaryFirstIncrement,
                    attackSpeed = item.attackSpeed + equipmentLevelUpgradeStatCalculator.primarySecondIncrement(
                        item = item,
                        definition = definition,
                        targetLevel = nextLevel
                    )
                )
            }

            FifthLevelUpgradeTarget.ADDITIONAL -> {
                item.copy(
                    level = nextLevel,
                    damage = item.damage + primaryFirstIncrement,
                    additionalStatValue = item.additionalStatValue + equipmentLevelUpgradeStatCalculator.additionalStatIncrement(
                        statType = item.additionalStatType,
                        targetLevel = nextLevel
                    )
                )
            }
        }
    }

    private fun upgradeArmor(
        item: ArmorItem,
        definition: ArmorDefinition,
        nextLevel: Int,
        fifthLevelUpgradeTarget: FifthLevelUpgradeTarget?
    ): ArmorItem {
        val primaryFirstIncrement = equipmentLevelUpgradeStatCalculator.primaryFirstIncrement(
            item = item,
            definition = definition,
            targetLevel = nextLevel
        )

        if (!isFifthLevel(nextLevel)) {
            return item.copy(
                level = nextLevel,
                hp = item.hp + primaryFirstIncrement
            )
        }

        return when (requireNotNull(fifthLevelUpgradeTarget)) {
            FifthLevelUpgradeTarget.PRIMARY_SECOND -> {
                item.copy(
                    level = nextLevel,
                    hp = item.hp + primaryFirstIncrement,
                    defense = item.defense + equipmentLevelUpgradeStatCalculator.primarySecondIncrement(
                        item = item,
                        definition = definition,
                        targetLevel = nextLevel
                    )
                )
            }

            FifthLevelUpgradeTarget.ADDITIONAL -> {
                item.copy(
                    level = nextLevel,
                    hp = item.hp + primaryFirstIncrement,
                    additionalStatValue = item.additionalStatValue + equipmentLevelUpgradeStatCalculator.additionalStatIncrement(
                        statType = item.additionalStatType,
                        targetLevel = nextLevel
                    )
                )
            }
        }
    }

    private fun upgradeArtifact(
        item: ArtifactItem,
        definition: ArtifactDefinition,
        nextLevel: Int,
        fifthLevelUpgradeTarget: FifthLevelUpgradeTarget?
    ): ArtifactItem {
        val primaryFirstIncrement = equipmentLevelUpgradeStatCalculator.primaryFirstIncrement(
            item = item,
            definition = definition,
            targetLevel = nextLevel
        )

        if (!isFifthLevel(nextLevel)) {
            return item.copy(
                level = nextLevel,
                cooldownReductionPercent = item.cooldownReductionPercent + primaryFirstIncrement
            )
        }

        return when (requireNotNull(fifthLevelUpgradeTarget)) {
            FifthLevelUpgradeTarget.PRIMARY_SECOND -> {
                item.copy(
                    level = nextLevel,
                    cooldownReductionPercent = item.cooldownReductionPercent + primaryFirstIncrement,
                    durationBonusPercent = item.durationBonusPercent + equipmentLevelUpgradeStatCalculator.primarySecondIncrement(
                        item = item,
                        definition = definition,
                        targetLevel = nextLevel
                    )
                )
            }

            FifthLevelUpgradeTarget.ADDITIONAL -> {
                item.copy(
                    level = nextLevel,
                    cooldownReductionPercent = item.cooldownReductionPercent + primaryFirstIncrement,
                    additionalStatValue = item.additionalStatValue + equipmentLevelUpgradeStatCalculator.additionalStatIncrement(
                        statType = item.additionalStatType,
                        targetLevel = nextLevel
                    )
                )
            }
        }
    }
}