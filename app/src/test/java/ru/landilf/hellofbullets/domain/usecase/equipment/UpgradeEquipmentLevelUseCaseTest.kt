package ru.landilf.hellofbullets.domain.usecase.equipment

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.landilf.hellofbullets.domain.engine.equipment.EquipmentLevelUpgradeStatCalculator
import ru.landilf.hellofbullets.domain.fixtures.EquipmentTestFixtures.armorDefinition
import ru.landilf.hellofbullets.domain.fixtures.EquipmentTestFixtures.artifactDefinition
import ru.landilf.hellofbullets.domain.fixtures.EquipmentTestFixtures.createArmor
import ru.landilf.hellofbullets.domain.fixtures.EquipmentTestFixtures.createArtifact
import ru.landilf.hellofbullets.domain.fixtures.EquipmentTestFixtures.createWeapon
import ru.landilf.hellofbullets.domain.fixtures.EquipmentTestFixtures.weaponDefinition
import ru.landilf.hellofbullets.domain.fixtures.FLOAT_EPSILON
import ru.landilf.hellofbullets.domain.fixtures.FakeEquipmentStatConfigRepository
import ru.landilf.hellofbullets.domain.model.equipment.ArmorItem
import ru.landilf.hellofbullets.domain.model.equipment.ArtifactItem
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQuality
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType
import ru.landilf.hellofbullets.domain.model.equipment.WeaponItem
import ru.landilf.hellofbullets.domain.model.equipment.upgrade.FifthLevelUpgradeTarget

class UpgradeEquipmentLevelUseCaseTest {
    private val useCase = UpgradeEquipmentLevelUseCase(
        equipmentLevelUpgradeStatCalculator = EquipmentLevelUpgradeStatCalculator(
            equipmentStatConfigRepository = FakeEquipmentStatConfigRepository()
        )
    )

    @Test
    fun `uses first stat growth multiplier on a regular level`() {
        val updatedItem = useCase(
            item = createWeapon(level = 1),
            definition = weaponDefinition,
            fifthLevelUpgradeTarget = FifthLevelUpgradeTarget.PRIMARY_SECOND
        ) as WeaponItem

        assertEquals(2, updatedItem.level)
        assertEquals(11.5f, updatedItem.damage, FLOAT_EPSILON)
        assertEquals(5f, updatedItem.attackSpeed, FLOAT_EPSILON)
        assertEquals(3f, updatedItem.additionalStatValue, FLOAT_EPSILON)
    }

    @Test
    fun `upgrades both weapon primary stats on fifth level`() {
        val updatedItem = useCase(
            item = createWeapon(level = 4),
            definition = weaponDefinition,
            fifthLevelUpgradeTarget = FifthLevelUpgradeTarget.PRIMARY_SECOND
        ) as WeaponItem

        assertEquals(5, updatedItem.level)
        assertEquals(11.5f, updatedItem.damage, FLOAT_EPSILON)
        assertEquals(7f, updatedItem.attackSpeed, FLOAT_EPSILON)
        assertEquals(3f, updatedItem.additionalStatValue, FLOAT_EPSILON)
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

        assertEquals(11.65f, updatedItem.damage, FLOAT_EPSILON)
        assertEquals(6.8f, updatedItem.attackSpeed, FLOAT_EPSILON)
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

        assertEquals(11.35f, updatedItem.damage, FLOAT_EPSILON)
        assertEquals(7.2f, updatedItem.attackSpeed, FLOAT_EPSILON)
    }

    @Test
    fun `uses configured multiplier for foreign additional armor stat`() {
        val updatedItem = useCase(
            item = createArmor(),
            definition = armorDefinition,
            fifthLevelUpgradeTarget = FifthLevelUpgradeTarget.ADDITIONAL
        ) as ArmorItem

        assertEquals(5, updatedItem.level)
        assertEquals(22f, updatedItem.hp, FLOAT_EPSILON)
        assertEquals(4f, updatedItem.defense, FLOAT_EPSILON)
        assertEquals(3.3f, updatedItem.additionalStatValue, FLOAT_EPSILON)
    }

    @Test
    fun `uses configured multiplier for matching additional stat`() {
        val updatedItem = useCase(
            item = createWeapon(
                level = 4,
                additionalStatType = EquipmentStatType.DAMAGE
            ),
            definition = weaponDefinition,
            fifthLevelUpgradeTarget = FifthLevelUpgradeTarget.ADDITIONAL
        ) as WeaponItem

        assertEquals(5, updatedItem.level)
        assertEquals(11.5f, updatedItem.damage, FLOAT_EPSILON)
        assertEquals(5f, updatedItem.attackSpeed, FLOAT_EPSILON)
        assertEquals(3.3f, updatedItem.additionalStatValue, FLOAT_EPSILON)
    }

    @Test
    fun `uses next level range for primary first stat`() {
        val updatedItem = useCase(
            item = createWeapon(level = 10),
            definition = weaponDefinition,
            fifthLevelUpgradeTarget = FifthLevelUpgradeTarget.PRIMARY_SECOND
        ) as WeaponItem

        assertEquals(11, updatedItem.level)
        assertEquals(17.5f, updatedItem.damage, FLOAT_EPSILON)
    }

    @Test
    fun `uses artifact primary first multiplier on regular level`() {
        val updatedItem = useCase(
            item = createArtifact(),
            definition = artifactDefinition,
            fifthLevelUpgradeTarget = FifthLevelUpgradeTarget.ADDITIONAL
        ) as ArtifactItem

        assertEquals(2, updatedItem.level)
        assertEquals(7.25f, updatedItem.cooldownReductionPercent, FLOAT_EPSILON)
        assertEquals(8f, updatedItem.durationBonusPercent, FLOAT_EPSILON)
        assertEquals(3f, updatedItem.additionalStatValue, FLOAT_EPSILON)
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
}