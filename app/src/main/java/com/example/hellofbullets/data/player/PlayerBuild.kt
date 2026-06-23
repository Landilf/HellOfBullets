package com.example.hellofbullets.data.player

import com.example.hellofbullets.data.equipment.ArmorItem
import com.example.hellofbullets.data.equipment.ArtifactItem
import com.example.hellofbullets.data.equipment.WeaponItem
import com.example.hellofbullets.data.skills.Skill

data class PlayerBuild(
    val equippedWeaponItem: WeaponItem?,
    val equippedArmorItem: ArmorItem?,
    val equippedArtifactItem: ArtifactItem?,
    val firstSkillSlot: Skill?,
    val secondSkillSlot: Skill?
)
