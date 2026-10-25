package ru.landilf.hellofbullets.domain.model.skills.definition

import ru.landilf.hellofbullets.domain.model.skills.PassiveSkillStat

data class PassiveSkillDefinition(
    override val id: Long,
    override val unlockCost: Int,
    val stat: PassiveSkillStat,
    val bonusPercentByLevel: List<Float>,
) : SkillDefinition() {
    override val maxLevel: Int
        get() = bonusPercentByLevel.size

    init {
        require(bonusPercentByLevel.isNotEmpty()) {
            "Для пассивного навыка должны быть заданы бонусы по уровням"
        }
        require(bonusPercentByLevel.all { it.isFinite() && it >= 0f }) {
            "Бонусы должны быть неотрицательными конечными числами"
        }
        require(
            stat != PassiveSkillStat.COOLDOWN_REDUCTION
                    || bonusPercentByLevel.all { it <= 100f }) {
            "Сокращение отката не может превышать 100%"
        }
        require(unlockCost >= 0) {
            "Стоимость разблокировки не может быть отрицательной"
        }
    }

    fun bonusPercentFor(level: Int): Float {
        require(level in 1..maxLevel) {
            "Уровень навыка должен быть в диапазоне от 1 до $maxLevel"
        }

        return bonusPercentByLevel[level - 1]
    }
}
