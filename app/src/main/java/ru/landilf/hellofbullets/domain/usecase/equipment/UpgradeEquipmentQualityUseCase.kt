package ru.landilf.hellofbullets.domain.usecase.equipment

import ru.landilf.hellofbullets.domain.model.equipment.ArmorItem
import ru.landilf.hellofbullets.domain.model.equipment.ArtifactItem
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQualityUpgradeResult
import ru.landilf.hellofbullets.domain.model.equipment.Item
import ru.landilf.hellofbullets.domain.model.equipment.WeaponItem
import ru.landilf.hellofbullets.domain.model.equipment.definition.ArmorDefinition
import ru.landilf.hellofbullets.domain.model.equipment.definition.ArtifactDefinition
import ru.landilf.hellofbullets.domain.model.equipment.definition.EquipmentDefinition
import ru.landilf.hellofbullets.domain.model.equipment.definition.WeaponDefinition
import javax.inject.Inject

class UpgradeEquipmentQualityUseCase @Inject constructor() {
    operator fun invoke(
        item: Item,
        definition: EquipmentDefinition,
        materials: List<Item>
    ): EquipmentQualityUpgradeResult {
        require(item.definitionId == definition.id) {
            "Определение не соответствует улучшаемому предмету"
        }

        val requiredMaterialsCount = requireNotNull(
            item.quality.materialsRequiredForUpgrade
        ) {
            "Нельзя повысить качество легендарного предмета"
        }
        val nextQuality = requireNotNull(item.quality.nextQuality) {
            "Не найдено следующее качество предмета"
        }

        require(materials.size == requiredMaterialsCount) {
            "Выбрано неверное количество материалов"
        }
        require(materials.map(Item::id).distinct().size == materials.size) {
            "Материалы для повышения качества не должны повторяться"
        }
        require(materials.none { it.id == item.id }) {
            "Нельзя использовать улучшаемый предмет как материал"
        }
        require(materials.all { material ->
            material::class == item::class &&
                    material.definitionId == item.definitionId &&
                    material.quality == item.quality
        }) {
            "Материалы должны совпадать с предметом по типу, виду и качеству"
        }

        val qualityBonus = item.quality.qualityLevel * QUALITY_BONUS_STEP *
                definition.primaryFirstGrowthMultiplier

        val upgradedItem = when (item) {
            is WeaponItem -> {
                require(definition is WeaponDefinition) {
                    "Для оружия требуется определение оружия"
                }

                item.copy(
                    quality = nextQuality,
                    damage = item.damage + qualityBonus
                )
            }

            is ArmorItem -> {
                require(definition is ArmorDefinition) {
                    "Для брони требуется определение брони"
                }

                item.copy(
                    quality = nextQuality,
                    hp = item.hp + qualityBonus
                )
            }

            is ArtifactItem -> {
                require(definition is ArtifactDefinition) {
                    "Для артефакта требуется определение артефакта"
                }

                item.copy(
                    quality = nextQuality,
                    cooldownReductionPercent = item.cooldownReductionPercent + qualityBonus
                )
            }
        }

        return EquipmentQualityUpgradeResult(
            upgradedItem = upgradedItem,
            consumedMaterialIds = materials.map(Item::id)
        )
    }

    private companion object {
        const val QUALITY_BONUS_STEP = 25f
    }
}