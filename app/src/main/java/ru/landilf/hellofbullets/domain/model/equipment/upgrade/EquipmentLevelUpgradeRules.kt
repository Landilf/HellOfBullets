package ru.landilf.hellofbullets.domain.model.equipment.upgrade

object EquipmentLevelUpgradeRules {
    const val FIFTH_LEVEL_STEP = 5

    fun isFifthLevel(level: Int): Boolean {
        return level % FIFTH_LEVEL_STEP == 0
    }
}