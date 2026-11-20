package ru.landilf.hellofbullets.domain.model.battle.common.skill

sealed interface BattleSkill {
    val skillId: Long
}

sealed interface ActiveBattleSkill : BattleSkill {
    val durationMs: Int
    val cooldownMs: Int
}

sealed interface PassiveBattleSkill : BattleSkill {
    val bonusPercent: Float
}

data class TimeSlowBattleSkill(
    override val skillId: Long,
    override val durationMs: Int,
    override val cooldownMs: Int,
    val slowdownPercent: Float
) : ActiveBattleSkill {
    init {
        require(durationMs > 0) {
            "Длительность навыка должна быть положительной"
        }
        require(cooldownMs >= 0) {
            "Откат навыка не может быть отрицательным"
        }
        require(slowdownPercent > 0f && slowdownPercent < 100f) {
            "Замедление должно быть больше 0% и меньше 100%"
        }
    }
}

data class ShieldBattleSkill(
    override val skillId: Long,
    override val durationMs: Int,
    override val cooldownMs: Int
) : ActiveBattleSkill {
    init {
        require(durationMs > 0) {
            "Длительность навыка должна быть положительной"
        }
        require(cooldownMs >= 0) {
            "Откат навыка не может быть отрицательным"
        }
    }
}

data class DurationBonusBattleSkill(
    override val skillId: Long,
    override val bonusPercent: Float
) : PassiveBattleSkill {
    init {
        require(bonusPercent.isFinite() && bonusPercent >= 0f) {
            "Бонус длительности должен быть неотрицательным конечным числом"
        }
    }
}

data class CooldownReductionBattleSkill(
    override val skillId: Long,
    override val bonusPercent: Float
) : PassiveBattleSkill {
    init {
        require(bonusPercent.isFinite() && bonusPercent >= 0f) {
            "Сокращение отката должно быть неотрицательным конечным числом"
        }
    }
}
