package ru.landilf.hellofbullets.data.player

import ru.landilf.hellofbullets.data.equipment.ArmorItem
import ru.landilf.hellofbullets.data.equipment.ArtifactItem
import ru.landilf.hellofbullets.data.equipment.WeaponItem
import ru.landilf.hellofbullets.data.skills.Skill

data class PlayerBuild(
    val equippedWeaponItem: WeaponItem?,
    val equippedArmorItem: ArmorItem?,
    val equippedArtifactItem: ArtifactItem?,
    val firstSkillSlot: Skill?,
    val secondSkillSlot: Skill?
)
