package com.landilf.hellofbullets.data.player

import com.landilf.hellofbullets.data.equipment.ArmorItem
import com.landilf.hellofbullets.data.equipment.ArtifactItem
import com.landilf.hellofbullets.data.equipment.WeaponItem
import com.landilf.hellofbullets.data.skills.Skill

data class PlayerBuild(
    val equippedWeaponItem: WeaponItem?,
    val equippedArmorItem: ArmorItem?,
    val equippedArtifactItem: ArtifactItem?,
    val firstSkillSlot: Skill?,
    val secondSkillSlot: Skill?
)
