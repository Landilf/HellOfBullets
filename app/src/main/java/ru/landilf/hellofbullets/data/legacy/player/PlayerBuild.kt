package ru.landilf.hellofbullets.data.legacy.player

import ru.landilf.hellofbullets.data.legacy.equipment.ArmorItem
import ru.landilf.hellofbullets.data.legacy.equipment.ArtifactItem
import ru.landilf.hellofbullets.data.legacy.equipment.WeaponItem
import ru.landilf.hellofbullets.data.legacy.skills.Skill

data class PlayerBuild(
    val equippedWeaponItem: WeaponItem?,
    val equippedArmorItem: ArmorItem?,
    val equippedArtifactItem: ArtifactItem?,
    val firstSkillSlot: Skill?,
    val secondSkillSlot: Skill?
)
