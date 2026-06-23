package com.example.hellofbullets.domain.model.player

import com.example.hellofbullets.domain.model.equipment.ArmorItem
import com.example.hellofbullets.domain.model.equipment.ArtifactItem
import com.example.hellofbullets.domain.model.equipment.WeaponItem
import com.example.hellofbullets.domain.model.skills.Skill

data class PlayerBuild(
    val equippedWeaponItem: WeaponItem?,
    val equippedArmorItem: ArmorItem?,
    val equippedArtifactItem: ArtifactItem?,
    val firstSkillSlot: Skill?,
    val secondSkillSlot: Skill?
)
