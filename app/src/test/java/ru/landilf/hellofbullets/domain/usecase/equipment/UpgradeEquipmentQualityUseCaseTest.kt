package ru.landilf.hellofbullets.domain.usecase.equipment

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.landilf.hellofbullets.domain.model.equipment.ArmorItem
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQuality
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType
import ru.landilf.hellofbullets.domain.model.equipment.WeaponItem
import ru.landilf.hellofbullets.domain.model.equipment.definition.WeaponDefinition

class UpgradeEquipmentQualityUseCaseTest {
    private val useCase = UpgradeEquipmentQualityUseCase()

    private val weaponDefinition = WeaponDefinition(
        id = 1L,
        name = "Pistol",
        primaryFirstGrowthMultiplier = 1.5f,
        primarySecondGrowthMultiplier = 2f,
        baseDamage = 10f,
        baseAttackSpeed = 5f,
        attackRange = 50f
    )

    @Test
    fun `upgrades normal weapon and returns selected material ids`() {
        val item = createWeapon(
            id = 1L,
            level = 7
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
        assertEquals(7, upgradeItem.level)
        assertEquals(25, upgradeItem.maxLevel)
        assertEquals(47.5f, upgradeItem.damage, EPSILON)
        assertEquals(materials.map { it.id }, result.consumedMaterialIds)
    }

    @Test
    fun `uses current quality level and definition multiplier for bonus`() {
        val item = createWeapon(
            id = 1L,
            quality = EquipmentQuality.FINE
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
        assertEquals(85f, upgradeItem.damage, EPSILON)
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

    private fun createWeapon(
        id: Long,
        definitionId: Long = weaponDefinition.id,
        quality: EquipmentQuality = EquipmentQuality.NORMAL,
        level: Int = 1,
        damage: Float = 10f
    ): WeaponItem {
        return WeaponItem(
            id = id,
            definitionId = definitionId,
            level = level,
            quality = quality,
            additionalStatType = EquipmentStatType.HP,
            additionalStatValue = 3f,
            damage = damage,
            attackSpeed = 5f
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

    private fun createArmor(): ArmorItem {
        return ArmorItem(
            id = 7L,
            definitionId = 2L,
            level = 1,
            quality = EquipmentQuality.NORMAL,
            additionalStatType = EquipmentStatType.DAMAGE,
            additionalStatValue = 3f,
            hp = 20f,
            defense = 4f
        )
    }

    private companion object {
        const val EPSILON = 0.0001f
    }
}