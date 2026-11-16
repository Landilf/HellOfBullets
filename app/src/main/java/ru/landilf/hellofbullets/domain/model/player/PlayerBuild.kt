package ru.landilf.hellofbullets.domain.model.player

import ru.landilf.hellofbullets.domain.model.equipment.ArmorItem
import ru.landilf.hellofbullets.domain.model.equipment.ArtifactItem
import ru.landilf.hellofbullets.domain.model.equipment.WeaponItem

data class PlayerBuild(
    val equippedWeaponItem: WeaponItem?,
    val equippedArmorItem: ArmorItem?,
    val equippedArtifactItem: ArtifactItem?,
    val firstSkillId: Long?,
    val secondSkillId: Long?
) {
    init {
        require(firstSkillId == null || firstSkillId != secondSkillId) {
            "Один навык нельзя экипировать в оба слота"
        }
    }
}
