package ru.landilf.hellofbullets.domain.model.player

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.landilf.hellofbullets.domain.fixtures.EquipmentTestFixtures.createArmor
import ru.landilf.hellofbullets.domain.fixtures.EquipmentTestFixtures.createWeapon
import ru.landilf.hellofbullets.domain.fixtures.PlayerTestFixtures.createPlayerState
import ru.landilf.hellofbullets.domain.model.equipment.EquipmentQuality
import ru.landilf.hellofbullets.domain.model.skills.SkillProgress
import ru.landilf.hellofbullets.domain.model.skills.SkillSlot

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

    @Test
    fun `equips owned weapon into weapon slot`() {
        val weapon = createWeapon(id = 1L)
        val playerState = createPlayerState(
            equippedWeapon = null,
            items = listOf(weapon)
        )

        val updatedState = playerState.setEquippedItem(
            slot = EquipmentSlot.WEAPON,
            item = weapon
        )

        assertEquals(
            weapon,
            updatedState.playerBuild.equippedWeaponItem
        )
    }

    @Test
    fun `clears selected equipment slot`() {
        val weapon = createWeapon(id = 1L)
        val playerState = createPlayerState(
            equippedWeapon = weapon,
            items = listOf(weapon)
        )

        val updatedState = playerState.setEquippedItem(
            slot = EquipmentSlot.WEAPON,
            item = null
        )

        assertEquals(
            null,
            updatedState.playerBuild.equippedWeaponItem
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throws when item type does not match equipment slot`() {
        val armor = createArmor()
        val playerState = createPlayerState(
            equippedWeapon = null,
            items = listOf(armor)
        )

        playerState.setEquippedItem(
            slot = EquipmentSlot.WEAPON,
            item = armor
        )
    }

    @Test
    fun `equips unlocked skill into selected slot`() {
        val playerState = createPlayerState(
            skillProgresses = listOf(
                SkillProgress(
                    skillId = 1L,
                    level = 1
                )
            )
        )

        val updatedState = playerState.setEquippedSkill(
            slot = SkillSlot.FIRST,
            skillId = 1L
        )

        assertEquals(1L, updatedState.playerBuild.firstSkillId)
        assertEquals(null, updatedState.playerBuild.secondSkillId)
    }

    @Test
    fun `clears selected skill slot`() {
        val playerState = createPlayerState(
            firstSkillId = 1L,
            skillProgresses = listOf(
                SkillProgress(
                    skillId = 1L,
                    level = 1
                )
            )
        )

        val updatedState = playerState.setEquippedSkill(
            slot = SkillSlot.FIRST,
            skillId = null
        )

        assertEquals(null, updatedState.playerBuild.firstSkillId)
    }

    @Test
    fun `moves equipped skill from another slot`() {
        val playerState = createPlayerState(
            firstSkillId = 1L,
            secondSkillId = 2L,
            skillProgresses = listOf(
                SkillProgress(skillId = 1L, level = 1),
                SkillProgress(skillId = 2L, level = 1)
            )
        )

        val updatedState = playerState.setEquippedSkill(
            slot = SkillSlot.FIRST,
            skillId = 2L
        )

        assertEquals(2L, updatedState.playerBuild.firstSkillId)
        assertEquals(null, updatedState.playerBuild.secondSkillId)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throws when equipping locked skill`() {
        val playerState = createPlayerState()

        playerState.setEquippedSkill(
            slot = SkillSlot.FIRST,
            skillId = 1L
        )
    }
}
