package ru.landilf.hellofbullets.domain.model.player

import ru.landilf.hellofbullets.domain.model.equipment.ArmorItem
import ru.landilf.hellofbullets.domain.model.equipment.ArtifactItem
import ru.landilf.hellofbullets.domain.model.equipment.WeaponItem
import ru.landilf.hellofbullets.domain.model.skills.Skill

data class PlayerBuild(
    val equippedWeaponItem: WeaponItem?,
    val equippedArmorItem: ArmorItem?,
    val equippedArtifactItem: ArtifactItem?,
    val firstSkillSlot: Skill?,
    val secondSkillSlot: Skill?
)
