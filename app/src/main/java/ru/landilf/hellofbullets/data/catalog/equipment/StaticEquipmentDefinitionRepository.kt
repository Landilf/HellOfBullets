package ru.landilf.hellofbullets.data.catalog.equipment

import ru.landilf.hellofbullets.domain.model.equipment.definition.ArmorDefinition
import ru.landilf.hellofbullets.domain.model.equipment.definition.ArtifactDefinition
import ru.landilf.hellofbullets.domain.model.equipment.definition.EquipmentDefinition
import ru.landilf.hellofbullets.domain.model.equipment.definition.WeaponDefinition
import ru.landilf.hellofbullets.domain.repository.EquipmentDefinitionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StaticEquipmentDefinitionRepository @Inject constructor() : EquipmentDefinitionRepository {
    private val definitions = listOf(
        WeaponDefinition(
            id = 1L,
            name = "Pistol",
            primaryFirstGrowthMultiplier = 1.5f,
            primarySecondGrowthMultiplier = 0.25f,
            baseDamage = 10f,
            baseAttackSpeed = 2f,
            attackRange = 500f
        ),
        ArmorDefinition(
            id = 2L,
            name = "Training armor",
            primaryFirstGrowthMultiplier = 10f,
            primarySecondGrowthMultiplier = 1f,
            baseHp = 100f,
            baseDefense = 5f
        ),
        ArtifactDefinition(
            id = 3L,
            name = "Hourglass",
            primaryFirstGrowthMultiplier = 2.5f,
            primarySecondGrowthMultiplier = 2.5f,
            baseCooldownReductionPercent = 5f,
            baseDurationBonusPercent = 5f
        )
    )

    override fun getDefinitions(): List<EquipmentDefinition> {
        return definitions
    }

    override fun getDefinitionById(id: Long): EquipmentDefinition? {
        return definitions.firstOrNull { it.id == id }
    }
}