package ru.landilf.hellofbullets.domain.fixtures

import ru.landilf.hellofbullets.domain.model.equipment.Item
import ru.landilf.hellofbullets.domain.model.equipment.WeaponItem
import ru.landilf.hellofbullets.domain.model.player.Inventory
import ru.landilf.hellofbullets.domain.model.player.PlayerBuild
import ru.landilf.hellofbullets.domain.model.player.PlayerProfile
import ru.landilf.hellofbullets.domain.model.player.PlayerState
import ru.landilf.hellofbullets.domain.model.skills.SkillProgress

object PlayerTestFixtures {
    fun createPlayerState(
        playerId: Long = 1L,
        level: Int = 1,
        totalExperience: Int = 0,
        silverAmount: Int = 0,
        skillPointAmount: Int = 0,
        equippedWeapon: WeaponItem? = null,
        firstSkillId: Long? = null,
        secondSkillId: Long? = null,
        skillProgresses: List<SkillProgress> = emptyList(),
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
                firstSkillId = firstSkillId,
                secondSkillId = secondSkillId
            ),
            skillProgresses = skillProgresses,
            inventory = Inventory(items)
        )
    }
}