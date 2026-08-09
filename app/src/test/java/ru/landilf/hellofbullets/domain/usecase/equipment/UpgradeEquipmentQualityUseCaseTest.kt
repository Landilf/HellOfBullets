package ru.landilf.hellofbullets.domain.usecase.equipment

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.landilf.hellofbullets.domain.fixtures.EquipmentTestFixtures.createArmor
import ru.landilf.hellofbullets.domain.fixtures.EquipmentTestFixtures.createWeapon
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQuality
import ru.landilf.hellofbullets.domain.model.equipment.WeaponItem
import ru.landilf.hellofbullets.domain.model.equipment.definition.StatRange
import ru.landilf.hellofbullets.domain.model.equipment.definition.WeaponDefinition

class UpgradeEquipmentQualityUseCaseTest {
    private val useCase = UpgradeEquipmentQualityUseCase()

    private val weaponDefinition = WeaponDefinition(
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

    @Test
    fun `upgrades normal weapon and returns selected material ids`() {
        val item = createWeapon(
            id = 1L,
            level = 10,
            damage = 23.5f
        )
        val materials = createWeaponMaterials(
            quality = EquipmentQuality.NORMAL,
            count = 6
        )

        val result = useCase(
            item = item,
            definition = weaponDefinition,
            materials = materials
        )

        val upgradeItem = result.upgradedItem as WeaponItem

        assertEquals(EquipmentQuality.FINE, upgradeItem.quality)
        assertEquals(10, upgradeItem.level)
        assertEquals(25, upgradeItem.maxLevel)
        assertEquals(25.85f, upgradeItem.damage, EPSILON)
        assertEquals(materials.map { it.id }, result.consumedMaterialIds)
    }

    @Test
    fun `scales accumulated first stat when quality is upgraded`() {
        val item = createWeapon(
            id = 1L,
            quality = EquipmentQuality.FINE,
            level = 10,
            damage = 25.85f
        )
        val materials = createWeaponMaterials(
            quality = EquipmentQuality.FINE,
            count = 5
        )

        val result = useCase(
            item = item,
            definition = weaponDefinition,
            materials = materials
        )

        val upgradeItem = result.upgradedItem as WeaponItem

        assertEquals(EquipmentQuality.SUPERIOR, upgradeItem.quality)
        assertEquals(29.375f, upgradeItem.damage, EPSILON)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throws when legendary item quality is upgraded`() {
        useCase(
            item = createWeapon(
                id = 1L,
                quality = EquipmentQuality.LEGENDARY
            ),
            definition = weaponDefinition,
            materials = emptyList()
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throws when materials contain duplicate item`() {
        val duplicateMaterial = createWeapon(id = 2L)

        useCase(
            item = createWeapon(id = 1L),
            definition = weaponDefinition,
            materials = List(6) { duplicateMaterial }
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throws when target item is used as material`() {
        val item = createWeapon(id = 1L)
        val materials = createWeaponMaterials(
            quality = EquipmentQuality.NORMAL,
            count = 5
        ) + item

        useCase(
            item = item,
            definition = weaponDefinition,
            materials = materials
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throws when material type does not match item`() {
        val materials = createWeaponMaterials(
            quality = EquipmentQuality.NORMAL,
            count = 5
        ) + createArmor()

        useCase(
            item = createWeapon(id = 1L),
            definition = weaponDefinition,
            materials = materials
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throws when material quality does not match item`() {
        val materials = createWeaponMaterials(
            quality = EquipmentQuality.NORMAL,
            count = 5
        ) + createWeapon(
            id = 7L,
            quality = EquipmentQuality.FINE
        )

        useCase(
            item = createWeapon(id = 1L),
            definition = weaponDefinition,
            materials = materials
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throws when materials count is incorrect`() {
        useCase(
            item = createWeapon(id = 1L),
            definition = weaponDefinition,
            materials = createWeaponMaterials(
                quality = EquipmentQuality.NORMAL,
                count = 5
            )
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throws when material definition does not match item`() {
        val materials = createWeaponMaterials(
            quality = EquipmentQuality.NORMAL,
            count = 5
        ) + createWeapon(
            id = 7L,
            definitionId = 2L
        )

        useCase(
            item = createWeapon(id = 1L),
            definition = weaponDefinition,
            materials = materials
        )
    }

    private fun createWeaponMaterials(
        quality: EquipmentQuality,
        count: Int
    ): List<WeaponItem> {
        return List(count) { index ->
            createWeapon(
                id = (index + 2).toLong(),
                quality = quality
            )
        }
    }

    private companion object {
        const val EPSILON = 0.0001f
    }
}