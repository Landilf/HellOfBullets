package ru.landilf.hellofbullets.domain.model.skills.definition

data class TimeSlowSkillDefinition(
    override val id: Long,
    override val unlockCost: Int,
    val levels: List<TimeSlowLevelParameters>
) : SkillDefinition() {
    override val maxLevel: Int
        get() = levels.size

    init {
        require(levels.isNotEmpty()) {
            "Для замедления должны быть заданы параметры уровней"
        }
        require(unlockCost >= 0) {
            "Стоимость разблокировки не может быть отрицательной"
        }
    }

    fun parametersFor(level: Int): TimeSlowLevelParameters {
        require(level in 1..maxLevel) {
            "Уровень замедления должен быть в диапазоне от 1 до $maxLevel"
        }

        return levels[level - 1]
    }
}