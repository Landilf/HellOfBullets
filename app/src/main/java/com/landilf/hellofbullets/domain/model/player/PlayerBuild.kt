package com.landilf.hellofbullets.domain.model.player

import com.landilf.hellofbullets.domain.model.equipment.ArmorItem
import com.landilf.hellofbullets.domain.model.equipment.ArtifactItem
import com.landilf.hellofbullets.domain.model.equipment.WeaponItem
import com.landilf.hellofbullets.domain.model.skills.Skill

data class PlayerBuild(
    val equippedWeaponItem: WeaponItem?,
    val equippedArmorItem: ArmorItem?,
    val equippedArtifactItem: ArtifactItem?,
    val firstSkillSlot: Skill?,
    val secondSkillSlot: Skill?
)
