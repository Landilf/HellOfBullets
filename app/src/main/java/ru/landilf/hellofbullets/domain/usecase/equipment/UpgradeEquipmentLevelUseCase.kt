package ru.landilf.hellofbullets.domain.usecase.equipment

import ru.landilf.hellofbullets.domain.model.equipment.ArmorItem
import ru.landilf.hellofbullets.domain.model.equipment.ArtifactItem
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType
import ru.landilf.hellofbullets.domain.model.equipment.Item
import ru.landilf.hellofbullets.domain.model.equipment.WeaponItem
import ru.landilf.hellofbullets.domain.model.equipment.definition.ArmorDefinition
import ru.landilf.hellofbullets.domain.model.equipment.definition.ArtifactDefinition
import ru.landilf.hellofbullets.domain.model.equipment.definition.EquipmentDefinition
import ru.landilf.hellofbullets.domain.model.equipment.definition.WeaponDefinition
import ru.landilf.hellofbullets.domain.repository.EquipmentStatConfigRepository
import javax.inject.Inject

class UpgradeEquipmentLevelUseCase @Inject constructor(
    private val equipmentStatConfigRepository: EquipmentStatConfigRepository
) {
    operator fun invoke(
        item: Item,
        definition: EquipmentDefinition,
        fifthLevelUpgradeTarget: FifthLevelUpgradeTarget
    ): Item {
        require(item.definitionId == definition.id) {
            "Определение не соответствует улучшаемому предмету"
        }
        require(item.level < item.maxLevel) {
            "Нельзя улучшить предмет максимального уровня"
        }

        val nextLevel = item.level + 1
        val baseIncrement = baseIncrementFor(nextLevel)

        return when (item) {
            is WeaponItem -> {
                require(definition is WeaponDefinition) {
                    "Для оружия требуется определение оружия"
                }

                upgradeWeapon(
                    item = item,
                    definition = definition,
                    nextLevel = nextLevel,
                    baseIncrement = baseIncrement,
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
                    baseIncrement = baseIncrement,
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
                    baseIncrement = baseIncrement,
                    fifthLevelUpgradeTarget = fifthLevelUpgradeTarget
                )
            }
        }
    }

    private fun baseIncrementFor(level: Int): Float {
        return when (level) {
            in 1..10 -> 1f
            in 11..20 -> 5f
            in 21..30 -> 10f
            in 31..40 -> 15f
            in 41..50 -> 20f
            else -> error("Не определён базовый прирост для уровня $level")
        }
    }

    private fun upgradeWeapon(
        item: WeaponItem,
        definition: WeaponDefinition,
        nextLevel: Int,
        baseIncrement: Float,
        fifthLevelUpgradeTarget: FifthLevelUpgradeTarget
    ): WeaponItem {
        val primaryFirstIncrement =
            baseIncrement * definition.primaryFirstGrowthMultiplierFor(item.specializationCoef)

        if (nextLevel % LEVEL_STEP != 0) {
            return item.copy(
                level = nextLevel,
                damage = item.damage + primaryFirstIncrement
            )
        }

        return when (fifthLevelUpgradeTarget) {
            FifthLevelUpgradeTarget.PRIMARY_SECOND -> {
                item.copy(
                    level = nextLevel,
                    damage = item.damage + primaryFirstIncrement,
                    attackSpeed = item.attackSpeed + baseIncrement *
                            definition.primarySecondGrowthMultiplierFor(item.specializationCoef)
                )
            }

            FifthLevelUpgradeTarget.ADDITIONAL -> {
                item.copy(
                    level = nextLevel,
                    damage = item.damage + primaryFirstIncrement,
                    additionalStatValue = item.additionalStatValue + additionalStatIncrement(
                        statType = item.additionalStatType,
                        baseIncrement = baseIncrement
                    )
                )
            }
        }
    }

    private fun upgradeArmor(
        item: ArmorItem,
        definition: ArmorDefinition,
        nextLevel: Int,
        baseIncrement: Float,
        fifthLevelUpgradeTarget: FifthLevelUpgradeTarget
    ): ArmorItem {
        val primaryFirstIncrement =
            baseIncrement * definition.primaryFirstGrowthMultiplierFor(item.specializationCoef)

        if (nextLevel % LEVEL_STEP != 0) {
            return item.copy(
                level = nextLevel,
                hp = item.hp + primaryFirstIncrement
            )
        }

        return when (fifthLevelUpgradeTarget) {
            FifthLevelUpgradeTarget.PRIMARY_SECOND -> {
                item.copy(
                    level = nextLevel,
                    hp = item.hp + primaryFirstIncrement,
                    defense = item.defense + baseIncrement *
                            definition.primarySecondGrowthMultiplierFor(item.specializationCoef)
                )
            }

            FifthLevelUpgradeTarget.ADDITIONAL -> {
                item.copy(
                    level = nextLevel,
                    hp = item.hp + primaryFirstIncrement,
                    additionalStatValue = item.additionalStatValue + additionalStatIncrement(
                        statType = item.additionalStatType,
                        baseIncrement = baseIncrement
                    )
                )
            }
        }
    }

    private fun upgradeArtifact(
        item: ArtifactItem,
        definition: ArtifactDefinition,
        nextLevel: Int,
        baseIncrement: Float,
        fifthLevelUpgradeTarget: FifthLevelUpgradeTarget
    ): ArtifactItem {
        val primaryFirstIncrement =
            baseIncrement * definition.primaryFirstGrowthMultiplierFor(item.specializationCoef)

        if (nextLevel % LEVEL_STEP != 0) {
            return item.copy(
                level = nextLevel,
                cooldownReductionPercent = item.cooldownReductionPercent + primaryFirstIncrement
            )
        }

        return when (fifthLevelUpgradeTarget) {
            FifthLevelUpgradeTarget.PRIMARY_SECOND -> {
                item.copy(
                    level = nextLevel,
                    cooldownReductionPercent = item.cooldownReductionPercent + primaryFirstIncrement,
                    durationBonusPercent = item.durationBonusPercent + baseIncrement *
                            definition.primarySecondGrowthMultiplierFor(item.specializationCoef)
                )
            }

            FifthLevelUpgradeTarget.ADDITIONAL -> {
                item.copy(
                    level = nextLevel,
                    cooldownReductionPercent = item.cooldownReductionPercent + primaryFirstIncrement,
                    additionalStatValue = item.additionalStatValue + additionalStatIncrement(
                        statType = item.additionalStatType,
                        baseIncrement = baseIncrement
                    )
                )
            }
        }
    }

    private fun additionalStatIncrement(
        statType: EquipmentStatType,
        baseIncrement: Float
    ): Float {
        return baseIncrement *
                equipmentStatConfigRepository
                    .getAdditionalStatConfig(statType)
                    .levelGrowthMultiplier
    }

    private companion object {
        const val LEVEL_STEP = 5
    }
}