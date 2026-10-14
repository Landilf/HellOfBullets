package ru.landilf.hellofbullets.domain.fixtures

import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType
import ru.landilf.hellofbullets.domain.model.equipment.definition.AdditionalStatConfig
import ru.landilf.hellofbullets.domain.model.equipment.definition.StatRange
import ru.landilf.hellofbullets.domain.repository.EquipmentStatConfigRepository

class FakeEquipmentStatConfigRepository : EquipmentStatConfigRepository {
    override fun getReferenceRange(statType: EquipmentStatType): StatRange {
        error("Диапазоны характеристик не используются в тестах улучшения снаряжения")
    }

    override fun getAdditionalStatConfig(statType: EquipmentStatType): AdditionalStatConfig {
        return configs.getValue(statType)
    }

    private companion object {
        val configs = mapOf(
            EquipmentStatType.DAMAGE to
                    AdditionalStatConfig(0.5f, 0.3f),
            EquipmentStatType.ATTACK_SPEED to
                    AdditionalStatConfig(0.5f, 0.05f),
            EquipmentStatType.HP to
                    AdditionalStatConfig(0.25f, 0.5f),
            EquipmentStatType.DEFENSE to
                    AdditionalStatConfig(0.25f, 0.1f),
            EquipmentStatType.COOLDOWN_REDUCTION to
                    AdditionalStatConfig(0.25f, 0.05f),
            EquipmentStatType.DURATION to
                    AdditionalStatConfig(0.25f, 0.05f),
        )
    }
}