package ru.landilf.hellofbullets.domain.model.skills.definition

data class ShieldSkillDefinition(
    override val id: Long,
    override val unlockCost: Int,
    val levels: List<ActiveSkillLevelParameters>
) : SkillDefinition() {
    override val maxLevel: Int
        get() = levels.size

    init {
        require(levels.isNotEmpty()) {
            "Для щита должны быть заданы параметры уровней"
        }
        require(unlockCost >= 0) {
            "Стоимость разблокировки не может быть отрицательной"
        }
    }

    fun parametersFor(level: Int): ActiveSkillLevelParameters {
        require(level in 1..maxLevel) {
            "Уровень щита должен быть в диапазоне от 1 до $maxLevel"
        }

        return levels[level - 1]
    }
}