package ru.landilf.hellofbullets.data.catalog.equipment

import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType
import ru.landilf.hellofbullets.domain.model.equipment.definition.AdditionalStatConfig
import ru.landilf.hellofbullets.domain.model.equipment.definition.StatRange
import ru.landilf.hellofbullets.domain.repository.EquipmentDefinitionRepository
import ru.landilf.hellofbullets.domain.repository.EquipmentStatConfigRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StaticEquipmentStatConfigRepository @Inject constructor(
    private val equipmentDefinitionRepository: EquipmentDefinitionRepository
) : EquipmentStatConfigRepository {
    override fun getReferenceRange(statType: EquipmentStatType): StatRange {
        val definitionId = referenceDefinitionIds.getValue(statType)
        val definition = requireNotNull(
            equipmentDefinitionRepository.getDefinitionById(definitionId)
        ) {
            "Не найдено эталонное определение экипировки с id $definitionId"
        }

        return requireNotNull(definition.primaryStatRangeFor(statType)) {
            "Эталонное определение $definitionId не содержит характеристику $statType"
        }
    }

    override fun getAdditionalStatConfig(statType: EquipmentStatType): AdditionalStatConfig {
        return additionalStatConfig.getValue(statType)
    }

    private companion object {
        const val PISTOL_ID = 1L
        const val TRAINING_ARMOR_ID = 2L
        const val HOURGLASS_ID = 3L

        val referenceDefinitionIds = mapOf(
            EquipmentStatType.DAMAGE to PISTOL_ID,
            EquipmentStatType.ATTACK_SPEED to PISTOL_ID,
            EquipmentStatType.HP to TRAINING_ARMOR_ID,
            EquipmentStatType.DEFENSE to TRAINING_ARMOR_ID,
            EquipmentStatType.COOLDOWN_REDUCTION to HOURGLASS_ID,
            EquipmentStatType.DURATION to HOURGLASS_ID
        )

        val additionalStatConfig = mapOf(
            EquipmentStatType.DAMAGE to AdditionalStatConfig(
                initialValueCoef = 0.5f,
                levelGrowthMultiplier = 0.3f
            ),
            EquipmentStatType.ATTACK_SPEED to AdditionalStatConfig(
                initialValueCoef = 0.5f,
                levelGrowthMultiplier = 0.05f
            ),
            EquipmentStatType.HP to AdditionalStatConfig(
                initialValueCoef = 0.25f,
                levelGrowthMultiplier = 0.5f
            ),
            EquipmentStatType.DEFENSE to AdditionalStatConfig(
                initialValueCoef = 0.25f,
                levelGrowthMultiplier = 0.1f
            ),
            EquipmentStatType.COOLDOWN_REDUCTION to AdditionalStatConfig(
                initialValueCoef = 0.25f,
                levelGrowthMultiplier = 0.05f
            ),
            EquipmentStatType.DURATION to AdditionalStatConfig(
                initialValueCoef = 0.25f,
                levelGrowthMultiplier = 0.05f
            )
        )
    }
}