package ru.landilf.hellofbullets.domain.model.battle.common.skill

data class ActiveBattleSkillRuntimeState(
    val skill: ActiveBattleSkill,
    val remainingEffectMs: Int = 0,
    val remainingCooldownMs: Int = 0
) {
    init {
        require(remainingEffectMs >= 0) {
            "Оставшаяся длительность эффекта не может быть отрицательной"
        }
        require(remainingCooldownMs >= 0) {
            "Оставшийся откат не может быть отрицательным"
        }
    }

    val isEffectActive: Boolean
        get() = remainingEffectMs > 0

    val isAvailable: Boolean
        get() = remainingCooldownMs == 0
}
