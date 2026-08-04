package ru.landilf.hellofbullets.domain.model.player

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQuality
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentStatType
import ru.landilf.hellofbullets.domain.model.equipment.Item
import ru.landilf.hellofbullets.domain.model.equipment.WeaponItem

class PlayerStateTest {
    @Test
    fun `replaces equipped item in inventory and build`() {
        val weapon = createWeapon(
            id = 1L,
            damage = 10f
        )
        val spareWeapon = createWeapon(
            id = 2L,
            damage = 15f
        )
        val updatedWeapon = weapon.copy(
            level = 2,
            damage = 11.5f
        )
        val playerState = createPlayerState(
            equippedWeapon = weapon,
            items = listOf(weapon, spareWeapon)
        )

        val updatedState = playerState.replaceItem(updatedWeapon)

        assertEquals(
            listOf(updatedWeapon, spareWeapon),
            updatedState.inventory.ownedItems
        )
        assertEquals(
            updatedWeapon,
            updatedState.playerBuild.equippedWeaponItem
        )
    }

    @Test
    fun `keeps build unchanged when replacing unequipped item`() {
        val equippedWeapon = createWeapon(
            id = 1L,
            damage = 10f
        )
        val spareWeapon = createWeapon(
            id = 2L,
            damage = 15f
        )
        val updatedSpareWeapon = spareWeapon.copy(
            level = 2,
            damage = 16.5f
        )
        val playerState = createPlayerState(
            equippedWeapon = equippedWeapon,
            items = listOf(equippedWeapon, spareWeapon)
        )

        val updatedState = playerState.replaceItem(updatedSpareWeapon)

        assertEquals(
            equippedWeapon,
            updatedState.playerBuild.equippedWeaponItem
        )
        assertEquals(
            listOf(equippedWeapon, updatedSpareWeapon),
            updatedState.inventory.ownedItems
        )
    }

    @Test
    fun `removes consumed equipped material from inventory and build`() {
        val targetWeapon = createWeapon(
            id = 1L,
            damage = 10f
        )
        val equippedMaterial = createWeapon(
            id = 2L,
            damage = 15f
        )
        val upgradedWeapon = targetWeapon.copy(
            level = 2,
            damage = 35f,
            quality = EquipmentQuality.FINE
        )
        val playerState = createPlayerState(
            equippedWeapon = equippedMaterial,
            items = listOf(targetWeapon, equippedMaterial)
        )

        val updatedState = playerState.replaceItem(
            updatedItem = upgradedWeapon,
            removedItemIds = setOf(equippedMaterial.id)
        )

        assertEquals(
            listOf(upgradedWeapon),
            updatedState.inventory.ownedItems
        )
        assertEquals(
            null,
            updatedState.playerBuild.equippedWeaponItem
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throws when replacing item absent from inventory`() {
        val ownedWeapon = createWeapon(id = 1L)
        val unownedWeapon = createWeapon(id = 2L)
        val playerState = createPlayerState(
            equippedWeapon = ownedWeapon,
            items = listOf(ownedWeapon)
        )

        playerState.replaceItem(unownedWeapon)
    }

    private fun createPlayerState(
        equippedWeapon: WeaponItem?,
        items: List<Item>
    ): PlayerState {
        return PlayerState(
            playerProfile = PlayerProfile(
                id = 1L,
                name = "Player",
                level = 1,
                totalExperience = 0,
                silverAmount = 0,
                skillPointAmount = 0
            ),
            playerBuild = PlayerBuild(
                equippedWeaponItem = equippedWeapon,
                equippedArmorItem = null,
                equippedArtifactItem = null,
                firstSkillSlot = null,
                secondSkillSlot = null
            ),
            inventory = Inventory(items)
        )
    }

    private fun createWeapon(
        id: Long,
        damage: Float = 10f,
        specializationCoef: Float = 0f
    ): WeaponItem {
        return WeaponItem(
            id = id,
            definitionId = 1L,
            level = 1,
            quality = EquipmentQuality.NORMAL,
            additionalStatType = EquipmentStatType.HP,
            additionalStatValue = 0f,
            damage = damage,
            attackSpeed = 2f,
            specializationCoef = specializationCoef
        )
    }
}