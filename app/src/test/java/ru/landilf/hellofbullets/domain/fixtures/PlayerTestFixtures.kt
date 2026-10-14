package ru.landilf.hellofbullets.domain.fixtures

import ru.landilf.hellofbullets.domain.model.equipment.Item
import ru.landilf.hellofbullets.domain.model.equipment.WeaponItem
import ru.landilf.hellofbullets.domain.model.player.Inventory
import ru.landilf.hellofbullets.domain.model.player.PlayerBuild
import ru.landilf.hellofbullets.domain.model.player.PlayerProfile
import ru.landilf.hellofbullets.domain.model.player.PlayerState

object PlayerTestFixtures {
    fun createPlayerState(
        playerId: Long = 1L,
        level: Int = 1,
        totalExperience: Int = 0,
        silverAmount: Int = 0,
        skillPointAmount: Int = 0,
        equippedWeapon: WeaponItem? = null,
        items: List<Item> = emptyList()
    ): PlayerState {
        return PlayerState(
            playerProfile = PlayerProfile(
                id = playerId,
                name = "Player",
                level = level,
                totalExperience = totalExperience,
                silverAmount = silverAmount,
                skillPointAmount = skillPointAmount
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
}