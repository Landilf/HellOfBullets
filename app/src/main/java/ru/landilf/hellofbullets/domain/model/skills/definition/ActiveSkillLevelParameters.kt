package ru.landilf.hellofbullets.domain.model.skills.definition

data class ActiveSkillLevelParameters(
    val durationMs: Int,
    val cooldownMs: Int
) {
    init {
        require(durationMs > 0) {
            "Длительность навыка должна быть положительной"
        }
        require(cooldownMs >= 0) {
            "Откат навыка не может быть отрицательным"
        }
    }
}