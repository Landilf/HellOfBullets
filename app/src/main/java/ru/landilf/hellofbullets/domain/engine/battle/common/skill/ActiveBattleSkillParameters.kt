package ru.landilf.hellofbullets.domain.engine.battle.common.skill

data class ActiveBattleSkillParameters(
    val durationMs: Int,
    val cooldownMs: Int
) {
    init {
        require(durationMs > 0) {
            "Длительность навыка должна быть положительной"
        }
        require(cooldownMs > 0) {
            "Откат навыка должен быть положительным"
        }
    }
}