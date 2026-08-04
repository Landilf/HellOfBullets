package ru.landilf.hellofbullets.domain.engine.equipment

import ru.landilf.hellofbullets.domain.generator.EquipmentItemIdGenerator
import ru.landilf.hellofbullets.domain.generator.EquipmentRandomGenerator
import ru.landilf.hellofbullets.domain.model.equipment.ArmorItem
import ru.landilf.hellofbullets.domain.model.equipment.ArtifactItem
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQuality
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType
import ru.landilf.hellofbullets.domain.model.equipment.Item
import ru.landilf.hellofbullets.domain.model.equipment.WeaponItem
import ru.landilf.hellofbullets.domain.model.equipment.definition.ArmorDefinition
import ru.landilf.hellofbullets.domain.model.equipment.definition.ArtifactDefinition
import ru.landilf.hellofbullets.domain.model.equipment.definition.EquipmentDefinition
import ru.landilf.hellofbullets.domain.model.equipment.definition.WeaponDefinition
import ru.landilf.hellofbullets.domain.repository.EquipmentStatConfigRepository
import javax.inject.Inject

class EquipmentItemFactory @Inject constructor(
    private val equipmentItemIdGenerator: EquipmentItemIdGenerator,
    private val equipmentRandomGenerator: EquipmentRandomGenerator,
    private val equipmentStatConfigRepository: EquipmentStatConfigRepository
) {
    suspend operator fun invoke(
        definition: EquipmentDefinition,
        quality: EquipmentQuality = EquipmentQuality.NORMAL
    ): Item {
        val specializationCoef = equipmentRandomGenerator.nextFloat(
            from = MIN_SPECIALIZATION_COEF,
            until = MAX_SPECIALIZATION_COEF
        )
        val additionalStatType = EquipmentStatType.entries[equipmentRandomGenerator.nextInt(
            until = EquipmentStatType.entries.size
        )]

        val id = equipmentItemIdGenerator.generateId()
        val additionalStatValue = createAdditionalStatValue(
            definition = definition,
            additionalStatType = additionalStatType
        )

        return when (definition) {
            is WeaponDefinition -> WeaponItem(
                id = id,
                definitionId = definition.id,
                level = INITIAL_LEVEL,
                quality = quality,
                additionalStatType = additionalStatType,
                additionalStatValue = additionalStatValue,
                damage = definition.primaryFirstStatRange.valueFor(specializationCoef),
                attackSpeed = definition.primarySecondStatRange.valueFor(-specializationCoef),
                specializationCoef = specializationCoef
            )

            is ArmorDefinition -> ArmorItem(
                id = id,
                definitionId = definition.id,
                level = INITIAL_LEVEL,
                quality = quality,
                additionalStatType = additionalStatType,
                additionalStatValue = additionalStatValue,
                hp = definition.primaryFirstStatRange.valueFor(specializationCoef),
                defense = definition.primarySecondStatRange.valueFor(-specializationCoef),
                specializationCoef = specializationCoef
            )

            is ArtifactDefinition -> ArtifactItem(
                id = id,
                definitionId = definition.id,
                level = INITIAL_LEVEL,
                quality = quality,
                additionalStatType = additionalStatType,
                additionalStatValue = additionalStatValue,
                cooldownReductionPercent = definition.primaryFirstStatRange.valueFor(
                    specializationCoef = specializationCoef
                ),
                durationBonusPercent = definition.primarySecondStatRange.valueFor(
                    specializationCoef = -specializationCoef
                ),
                specializationCoef = specializationCoef
            )
        }

    }

    private fun createAdditionalStatValue(
        definition: EquipmentDefinition,
        additionalStatType: EquipmentStatType
    ): Float {
        val referenceRange = definition.primaryStatRangeFor(additionalStatType)
            ?: equipmentStatConfigRepository.getReferenceRange(additionalStatType)
        val config = equipmentStatConfigRepository.getAdditionalStatConfig(additionalStatType)

        val scaledRange = referenceRange.scaleBy(config.initialValueCoef)

        return scaledRange.valueFor(
            equipmentRandomGenerator.nextFloat(
                from = MIN_RANGE_COEF,
                until = MAX_RANGE_COEF
            )
        )
    }

    private companion object {
        const val INITIAL_LEVEL = 1
        const val MIN_SPECIALIZATION_COEF = -1f
        const val MAX_SPECIALIZATION_COEF = 1f
        const val MIN_RANGE_COEF = -1f
        const val MAX_RANGE_COEF = 1f
    }
}