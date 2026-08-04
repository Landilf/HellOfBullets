package ru.landilf.hellofbullets.domain.usecase.equipment

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.landilf.hellofbullets.domain.model.equipment.ArmorItem
import ru.landilf.hellofbullets.domain.model.equipment.ArtifactItem
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQuality
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType
import ru.landilf.hellofbullets.domain.model.equipment.WeaponItem
import ru.landilf.hellofbullets.domain.model.equipment.definition.ArmorDefinition
import ru.landilf.hellofbullets.domain.model.equipment.definition.ArtifactDefinition
import ru.landilf.hellofbullets.domain.model.equipment.definition.StatRange
import ru.landilf.hellofbullets.domain.model.equipment.definition.WeaponDefinition

class UpgradeEquipmentLevelUseCaseTest {
    private val useCase = UpgradeEquipmentLevelUseCase()

    private val weaponDefinition = WeaponDefinition(
        id = 1L,
        name = "Pistol",
        primaryFirstGrowthMultiplier = 1.5f,
        primarySecondGrowthMultiplier = 2f,
        damageRange = StatRange(9f, 11f),
        attackSpeedRange = StatRange(1.8f, 2.2f),
        attackRange = 50f,
        baseLevelUpgradeCost = 10
    )

    private val armorDefinition = ArmorDefinition(
        id = 2L,
        name = "Light armor",
        primaryFirstGrowthMultiplier = 2f,
        primarySecondGrowthMultiplier = 1.5f,
        hpRange = StatRange(80f, 120f),
        defenseRange = StatRange(3f, 7f),
        baseLevelUpgradeCost = 15
    )

    private val artifactDefinition = ArtifactDefinition(
        id = 3L,
        name = "Hourglass",
        primaryFirstGrowthMultiplier = 1.25f,
        primarySecondGrowthMultiplier = 2f,
        cooldownReductionPercentRange = StatRange(3f, 7f),
        durationBonusPercentRange = StatRange(3f, 7f),
        baseLevelUpgradeCost = 12
    )

    @Test
    fun `uses first stat growth multiplier on a regular level`() {
        val updatedItem = useCase(
            item = createWeapon(level = 1),
            definition = weaponDefinition,
            fifthLevelUpgradeTarget = FifthLevelUpgradeTarget.PRIMARY_SECOND
        ) as WeaponItem

        assertEquals(2, updatedItem.level)
        assertEquals(11.5f, updatedItem.damage, EPSILON)
        assertEquals(5f, updatedItem.attackSpeed, EPSILON)
        assertEquals(3f, updatedItem.additionalStatValue, EPSILON)
    }

    @Test
    fun `upgrades both weapon primary stats on fifth level`() {
        val updatedItem = useCase(
            item = createWeapon(level = 4),
            definition = weaponDefinition,
            fifthLevelUpgradeTarget = FifthLevelUpgradeTarget.PRIMARY_SECOND
        ) as WeaponItem

        assertEquals(5, updatedItem.level)
        assertEquals(11.5f, updatedItem.damage, EPSILON)
        assertEquals(7f, updatedItem.attackSpeed, EPSILON)
        assertEquals(3f, updatedItem.additionalStatValue, EPSILON)
    }

    @Test
    fun `increases first stat growth and decreases second stat growth for positive specialization`() {
        val updatedItem = useCase(
            item = createWeapon(
                level = 4,
                specializationCoef = 1f
            ),
            definition = weaponDefinition,
            fifthLevelUpgradeTarget = FifthLevelUpgradeTarget.PRIMARY_SECOND
        ) as WeaponItem

        assertEquals(11.65f, updatedItem.damage, EPSILON)
        assertEquals(6.8f, updatedItem.attackSpeed, EPSILON)
    }

    @Test
    fun `decreases first stat growth and increases second stat growth for negative specialization`() {
        val updatedItem = useCase(
            item = createWeapon(
                level = 4,
                specializationCoef = -1f
            ),
            definition = weaponDefinition,
            fifthLevelUpgradeTarget = FifthLevelUpgradeTarget.PRIMARY_SECOND
        ) as WeaponItem

        assertEquals(11.35f, updatedItem.damage, EPSILON)
        assertEquals(7.2f, updatedItem.attackSpeed, EPSILON)
    }

    @Test
    fun `uses foreign stat multiplier for additional armor stat`() {
        val updatedItem = useCase(
            item = createArmor(),
            definition = armorDefinition,
            fifthLevelUpgradeTarget = FifthLevelUpgradeTarget.ADDITIONAL
        ) as ArmorItem

        assertEquals(5, updatedItem.level)
        assertEquals(22f, updatedItem.hp, EPSILON)
        assertEquals(4f, updatedItem.defense, EPSILON)
        assertEquals(3.5f, updatedItem.additionalStatValue, EPSILON)
    }

    @Test
    fun `uses matching primary multiplier for additional stat`() {
        val updatedItem = useCase(
            item = createWeapon(
                level = 4,
                additionalStatType = EquipmentStatType.DAMAGE
            ),
            definition = weaponDefinition,
            fifthLevelUpgradeTarget = FifthLevelUpgradeTarget.ADDITIONAL
        ) as WeaponItem

        assertEquals(5, updatedItem.level)
        assertEquals(11.5f, updatedItem.damage, EPSILON)
        assertEquals(5f, updatedItem.attackSpeed, EPSILON)
        assertEquals(4.5f, updatedItem.additionalStatValue, EPSILON)
    }

    @Test
    fun `uses next level range for primary first stat`() {
        val updatedItem = useCase(
            item = createWeapon(level = 10),
            definition = weaponDefinition,
            fifthLevelUpgradeTarget = FifthLevelUpgradeTarget.PRIMARY_SECOND
        ) as WeaponItem

        assertEquals(11, updatedItem.level)
        assertEquals(17.5f, updatedItem.damage, EPSILON)
    }

    @Test
    fun `uses artifact primary first multiplier on regular level`() {
        val updatedItem = useCase(
            item = createArtifact(),
            definition = artifactDefinition,
            fifthLevelUpgradeTarget = FifthLevelUpgradeTarget.ADDITIONAL
        ) as ArtifactItem

        assertEquals(2, updatedItem.level)
        assertEquals(7.25f, updatedItem.cooldownReductionPercent, EPSILON)
        assertEquals(8f, updatedItem.durationBonusPercent, EPSILON)
        assertEquals(3f, updatedItem.additionalStatValue, EPSILON)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throws when item is already at maximum level`() {
        useCase(
            item = createWeapon(
                level = EquipmentQuality.NORMAL.maxLevel
            ),
            definition = weaponDefinition,
            fifthLevelUpgradeTarget = FifthLevelUpgradeTarget.PRIMARY_SECOND
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throws when definition does not match item`() {
        useCase(
            item = createWeapon(level = 1),
            definition = armorDefinition,
            fifthLevelUpgradeTarget = FifthLevelUpgradeTarget.PRIMARY_SECOND
        )
    }

    private fun createWeapon(
        level: Int,
        additionalStatType: EquipmentStatType = EquipmentStatType.HP,
        specializationCoef: Float = 0f
    ): WeaponItem {
        return WeaponItem(
            id = 1L,
            definitionId = weaponDefinition.id,
            level = level,
            quality = EquipmentQuality.NORMAL,
            additionalStatType = additionalStatType,
            additionalStatValue = 3f,
            damage = 10f,
            attackSpeed = 5f,
            specializationCoef = specializationCoef
        )
    }

    private fun createArmor(
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

    private fun createArtifact(
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

    private companion object {
        const val EPSILON = 0.0001f
    }
}