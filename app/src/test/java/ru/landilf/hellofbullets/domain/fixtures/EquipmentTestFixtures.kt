package ru.landilf.hellofbullets.domain.fixtures

import ru.landilf.hellofbullets.domain.model.equipment.ArmorItem
import ru.landilf.hellofbullets.domain.model.equipment.ArtifactItem
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQuality
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType
import ru.landilf.hellofbullets.domain.model.equipment.WeaponItem
import ru.landilf.hellofbullets.domain.model.equipment.definition.ArmorDefinition
import ru.landilf.hellofbullets.domain.model.equipment.definition.ArtifactDefinition
import ru.landilf.hellofbullets.domain.model.equipment.definition.StatRange
import ru.landilf.hellofbullets.domain.model.equipment.definition.WeaponDefinition

object EquipmentTestFixtures {
    val weaponDefinition = WeaponDefinition(
        id = 1L,
        name = "Pistol",
        primaryFirstGrowthMultiplier = 1.5f,
        primarySecondGrowthMultiplier = 2f,
        basePurchasePrice = 100,
        baseLevelUpgradeCost = 10,
        damageRange = StatRange(9f, 11f),
        attackSpeedRange = StatRange(1.8f, 2.2f),
        attackRange = 50f
    )

    val armorDefinition = ArmorDefinition(
        id = 2L,
        name = "Light armor",
        primaryFirstGrowthMultiplier = 2f,
        primarySecondGrowthMultiplier = 1.5f,
        basePurchasePrice = 150,
        baseLevelUpgradeCost = 15,
        hpRange = StatRange(80f, 120f),
        defenseRange = StatRange(3f, 7f)
    )

    val artifactDefinition = ArtifactDefinition(
        id = 3L,
        name = "Hourglass",
        primaryFirstGrowthMultiplier = 1.25f,
        primarySecondGrowthMultiplier = 2f,
        basePurchasePrice = 120,
        baseLevelUpgradeCost = 12,
        cooldownReductionPercentRange = StatRange(3f, 7f),
        durationBonusPercentRange = StatRange(3f, 7f)
    )

    fun createWeapon(
        id: Long = 1L,
        definitionId: Long = weaponDefinition.id,
        quality: EquipmentQuality = EquipmentQuality.NORMAL,
        level: Int = 1,
        additionalStatType: EquipmentStatType = EquipmentStatType.HP,
        additionalStatValue: Float = 3f,
        damage: Float = 10f,
        attackSpeed: Float = 5f,
        specializationCoef: Float = 0f
    ): WeaponItem {
        return WeaponItem(
            id = id,
            definitionId = definitionId,
            level = level,
            quality = quality,
            additionalStatType = additionalStatType,
            additionalStatValue = additionalStatValue,
            damage = damage,
            attackSpeed = attackSpeed,
            specializationCoef = specializationCoef
        )
    }

    fun createArmor(
        specializationCoef: Float = 0f
    ): ArmorItem {
        return ArmorItem(
            id = 2L,
            definitionId = armorDefinition.id,
            level = 4,
            quality = EquipmentQuality.NORMAL,
            additionalStatType = EquipmentStatType.DAMAGE,
            additionalStatValue = 3f,
            hp = 20f,
            defense = 4f,
            specializationCoef = specializationCoef
        )
    }

    fun createArtifact(
        specializationCoef: Float = 0f
    ): ArtifactItem {
        return ArtifactItem(
            id = 3L,
            definitionId = artifactDefinition.id,
            level = 1,
            quality = EquipmentQuality.NORMAL,
            additionalStatType = EquipmentStatType.DEFENSE,
            additionalStatValue = 3f,
            cooldownReductionPercent = 6f,
            durationBonusPercent = 8f,
            specializationCoef = specializationCoef
        )
    }
}